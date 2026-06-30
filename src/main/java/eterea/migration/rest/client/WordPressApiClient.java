package eterea.migration.rest.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eterea.migration.rest.extern.OrderNoteWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

@Slf4j
public class WordPressApiClient {

   private static final int PAGE_SIZE = 50; // Maximum value allowed by the Query API (size, max 50)
   private static final int MAX_PAGES = 100;
   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

   private final WebClient webClient;
   private final String baseUrl;
   private final String username;
   private final String password;
   private final String siteName;
   private final ObjectMapper objectMapper = new ObjectMapper();

   public WordPressApiClient(String baseUrl, String username, String password, String siteName) {
      this.baseUrl = baseUrl;
      this.username = username;
      this.password = password;
      this.siteName = siteName;
      this.webClient = WebClient.builder()
            .baseUrl(this.baseUrl)
            .defaultHeaders(headers -> {
               if (this.username != null && !this.username.isBlank()
                     && this.password != null && !this.password.isBlank()) {
                  headers.setBasicAuth(this.username, this.password);
               }
               headers.setContentType(MediaType.APPLICATION_JSON);
               headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            })
            .build();
   }

   /**
    * Fetches the export-feed work queue (GET /orders/export-feed). Each completed order is delivered
    * once, plus any order completed within {@code exportRecheckDays} days. This endpoint has a write
    * side effect: it stamps {@code woe_order_exported} on orders the first time they are fetched.
    */
   public List<OrderNoteWeb> fetchExportFeed(int exportRecheckDays) {
      log.info("[{}] Fetching export-feed from WordPress API (export_recheck_days={})", siteName, exportRecheckDays);
      return fetchPaged("export-feed", page -> uriBuilder -> uriBuilder
            .path("/orders/export-feed")
            .queryParam("export_recheck_days", exportRecheckDays)
            .queryParam("page", page)
            .queryParam("size", PAGE_SIZE)
            .build());
   }

   /**
    * Fetches orders within a date range (GET /orders). Read-only, no side effects.
    */
   public List<OrderNoteWeb> fetchOrders(ZonedDateTime dateFrom, ZonedDateTime dateTo) {
      log.info("[{}] Fetching orders from WordPress API: {} to {}", siteName, dateFrom, dateTo);
      return fetchPaged("orders", page -> uriBuilder -> uriBuilder
            .path("/orders")
            .queryParam("date_from", dateFrom.format(DATE_FORMATTER))
            .queryParam("date_to", dateTo.format(DATE_FORMATTER))
            .queryParam("status", "completed", "processing", "on-hold")
            .queryParam("page", page)
            .queryParam("size", PAGE_SIZE)
            .build());
   }

   /**
    * Walks the paged envelope ({@code {content, page}}) of a Query API endpoint, accumulating every
    * page's content. Total pages are read from the response body ({@code page.totalPages}). On any
    * error the orders accumulated so far are returned (graceful degradation).
    */
   private List<OrderNoteWeb> fetchPaged(String label, IntFunction<Function<UriBuilder, URI>> uriForPage) {
      List<OrderNoteWeb> allOrders = new ArrayList<>();

      try {
         int totalPages = 1;
         int currentPage = 0; // Query API pages are 0-indexed

         while (currentPage < totalPages && currentPage < MAX_PAGES) {
            log.debug("[{}] Fetching {} page {}/{}", siteName, label, currentPage, totalPages);

            final int page = currentPage;
            var response = webClient.get()
                  .uri(uriForPage.apply(page))
                  .retrieve()
                  .toEntity(new ParameterizedTypeReference<OrderPage>() {
                  })
                  .block();

            if (response == null || response.getBody() == null) {
               log.warn("[{}] Empty {} response received on page {}", siteName, label, page);
               break;
            }

            OrderPage body = response.getBody();
            if (currentPage == 0 && body.page() != null) {
               totalPages = body.page().totalPages();
               log.debug("[{}] Total {} pages available: {} ({} elements)",
                     siteName, label, totalPages, body.page().totalElements());
            }

            if (body.content() != null) {
               for (OrderNoteWeb orderNote : body.content()) {
                  orderNote.setOriginalJson(serializeOriginalJson(orderNote));
                  allOrders.add(orderNote);
               }
            }

            currentPage++;
         }

         log.info("[{}] Successfully fetched {} orders from {}", siteName, allOrders.size(), label);
         return allOrders;

      } catch (WebClientResponseException e) {
         log.error("[{}] HTTP error ({}) on {}: {}", siteName, e.getStatusCode(), label, e.getMessage());
         return allOrders;
      } catch (Exception e) {
         log.error("[{}] Unexpected error while fetching {}: {}", siteName, label, e.getMessage(), e);
         return allOrders;
      }
   }

   private String serializeOriginalJson(OrderNoteWeb orderNote) {
      try {
         return objectMapper.writeValueAsString(orderNote);
      } catch (JsonProcessingException e) {
         log.warn("[{}] Could not serialize original JSON for order {}: {}",
               siteName, orderNote.getOrderNumber(), e.getMessage());
         return "{\"order_number\": \"" + orderNote.getOrderNumber() + "\"}";
      }
   }

   /** Spring Data {@code PagedModel} envelope returned by the Query API. */
   @JsonIgnoreProperties(ignoreUnknown = true)
   record OrderPage(
         List<OrderNoteWeb> content,
         @JsonProperty("page") PageMetadata page) {
   }

   @JsonIgnoreProperties(ignoreUnknown = true)
   record PageMetadata(int size, int number, long totalElements, int totalPages) {
   }
}
