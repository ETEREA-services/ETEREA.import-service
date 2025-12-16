package eterea.migration.api.rest.client;

import eterea.migration.api.rest.extern.OrderNoteWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WordPressApiClient {

   private static final int PER_PAGE = 50; // Maximum value allowed by WordPress API
   private static final int MAX_PAGES = 100;
   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
   private static final String ORDER_STATUS_FILTER = "completed,processing,on-hold"; // Comma-separated status values

   private final WebClient webClient;
   private final String baseUrl;
   private final String username;
   private final String password;
   private final String siteName;

   public WordPressApiClient(String baseUrl, String username, String password, String siteName) {
      this.baseUrl = baseUrl;
      this.username = username;
      this.password = password;
      this.siteName = siteName;
      this.webClient = WebClient.builder()
            .baseUrl(this.baseUrl)
            .defaultHeaders(headers -> {
               headers.setBasicAuth(this.username, this.password);
               headers.setContentType(MediaType.APPLICATION_JSON);
               headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            })
            .build();
   }

   public List<OrderNoteWeb> fetchOrders(ZonedDateTime dateFrom, ZonedDateTime dateTo) {
      log.info("[{}] Fetching orders from WordPress API: {} to {}", siteName, dateFrom, dateTo);

      List<OrderNoteWeb> allOrders = new ArrayList<>();

      try {
         int totalPages = 1;
         int currentPage = 1;

         while (currentPage <= totalPages && currentPage <= MAX_PAGES) {
            log.debug("[{}] Fetching page {}/{}", siteName, currentPage, totalPages);

            final int page = currentPage;
            var responseSpec = webClient.get()
                  .uri(uriBuilder -> uriBuilder
                        .queryParam("date_from", dateFrom.format(DATE_FORMATTER))
                        .queryParam("date_to", dateTo.format(DATE_FORMATTER))
                        .queryParam("status", ORDER_STATUS_FILTER)
                        .queryParam("page", page)
                        .queryParam("per_page", PER_PAGE)
                        .build())
                  .retrieve();

            var response = responseSpec
                  .toEntity(new ParameterizedTypeReference<List<OrderNoteWeb>>() {
                  })
                  .block();

            if (response != null && response.getBody() != null) {
               // Get total pages from header on first request
               if (currentPage == 1) {
                  String totalPagesHeader = response.getHeaders().getFirst("x-wp-totalpages");
                  if (totalPagesHeader != null) {
                     try {
                        totalPages = Integer.parseInt(totalPagesHeader);
                        log.debug("[{}] Total pages available: {}", siteName, totalPages);
                     } catch (NumberFormatException e) {
                        log.warn("[{}] Could not parse x-wp-totalpages header: {}", siteName, totalPagesHeader);
                     }
                  }
               }

               for (OrderNoteWeb orderNote : response.getBody()) {
                  orderNote.setOriginalJson("{\"order_number\": \"" + orderNote.getOrderNumber() + "\"}");
                  allOrders.add(orderNote);
               }
            } else {
               log.warn("[{}] Empty response received", siteName);
               break;
            }

            currentPage++;
         }

         log.info("[{}] Successfully fetched {} orders", siteName, allOrders.size());
         return allOrders;

      } catch (WebClientResponseException e) {
         log.error("[{}] HTTP error ({}): {}", siteName, e.getStatusCode(), e.getMessage());
         return allOrders;
      } catch (Exception e) {
         log.error("[{}] Unexpected error while fetching orders: {}", siteName, e.getMessage(), e);
         return allOrders;
      }
   }
}
