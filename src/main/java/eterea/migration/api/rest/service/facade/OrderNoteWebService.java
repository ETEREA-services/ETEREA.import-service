package eterea.migration.api.rest.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eterea.migration.api.rest.client.WordPressApiClient;
import eterea.migration.api.rest.exception.ProductException;
import eterea.migration.api.rest.extern.*;
import eterea.migration.api.rest.model.*;
import eterea.migration.api.rest.service.*;
import eterea.migration.api.rest.service.internal.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderNoteWebService {

   private final FileService fileService;
   private final OrderNoteService orderNoteService;
   private final ProductService productService;
   private final PaymentService paymentService;
   private final InformacionPagadorService informacionPagadorService;
   private final ProductTransactionService productTransactionService;
   private final WordPressApiClient wordPressApiClientSite1;
   private final WordPressApiClient wordPressApiClientSite2;

   public OrderNoteWebService(
         FileService fileService,
         OrderNoteService orderNoteService,
         ProductService productService,
         PaymentService paymentService,
         InformacionPagadorService informacionPagadorService,
         ProductTransactionService productTransactionService,
         @Qualifier("wordPressApiClientSite1") WordPressApiClient wordPressApiClientSite1,
         @Qualifier("wordPressApiClientSite2") WordPressApiClient wordPressApiClientSite2) {
      this.fileService = fileService;
      this.orderNoteService = orderNoteService;
      this.productService = productService;
      this.paymentService = paymentService;
      this.informacionPagadorService = informacionPagadorService;
      this.productTransactionService = productTransactionService;
      this.wordPressApiClientSite1 = wordPressApiClientSite1;
      this.wordPressApiClientSite2 = wordPressApiClientSite2;
   }

   public List<OrderNoteWeb> capture() {
      log.info("Capturing orders...");
      ObjectMapper objectMapper = new ObjectMapper();
      File file = new File(fileService.getFile());
      List<OrderNoteWeb> orderNotes = null;
      try {
         JsonNode jsonArray = objectMapper.readTree(file);
         orderNotes = new ArrayList<>();
         for (JsonNode node : jsonArray) {
            OrderNoteWeb orderNote = objectMapper.treeToValue(node, OrderNoteWeb.class);
            orderNote.setOriginalJson(node.toString());
            orderNotes.add(orderNote);
         }

         for (OrderNoteWeb orderNote : orderNotes) {
            int inicioPago = orderNote.getOrderNotes().lastIndexOf("PlusPago");
            int finPago = orderNote.getOrderNotes().indexOf("Una nueva reserva");
            String plusPagoString = "";
            if (inicioPago > -1 && finPago > -1) {
               plusPagoString = orderNote.getOrderNotes().substring(inicioPago + 8, finPago - 1);
               PaymentWeb payment = objectMapper.readValue(plusPagoString, PaymentWeb.class);
               orderNote.setPayment(payment);
            }
         }
      } catch (JsonProcessingException e) {
         log.debug("JsonProcessingException - {}", e.getMessage());
      } catch (IOException e) {
         log.debug("IOException - {}", e.getMessage());
      }
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      DateTimeFormatter formatterLocal = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
      assert orderNotes != null;
      for (OrderNoteWeb orderNoteWeb : orderNotes) {
         log.info("orderNoteWeb={}", orderNoteWeb);
         try {
            OffsetDateTime orderDate = null;
            if (!orderNoteWeb.getOrderDate().isEmpty()) {
               orderDate = LocalDateTime.parse(orderNoteWeb.getOrderDate(), formatter).atOffset(ZoneOffset.UTC);
            }
            OffsetDateTime paidDate = null;
            if (!orderNoteWeb.getPaidDate().isEmpty()) {
               paidDate = LocalDateTime.parse(orderNoteWeb.getPaidDate(), formatter).atOffset(ZoneOffset.UTC);
            }
            OffsetDateTime completedDate = null;
            if (!orderNoteWeb.getCompletedDate().isEmpty()) {
               completedDate = LocalDateTime.parse(orderNoteWeb.getCompletedDate(), formatter)
                     .atOffset(ZoneOffset.UTC);
            }
            OffsetDateTime modifiedDate = null;
            if (!orderNoteWeb.getModifiedDate().isEmpty()) {
               modifiedDate = LocalDateTime.parse(orderNoteWeb.getModifiedDate(), formatter)
                     .atOffset(ZoneOffset.UTC);
            }

            OrderNote orderNote = new OrderNote(
                  Long.valueOf(orderNoteWeb.getOrderNumber()),
                  orderNoteWeb.getOrderStatus(),
                  orderDate,
                  paidDate,
                  completedDate,
                  modifiedDate,
                  orderNoteWeb.getOrderCurrency(),
                  orderNoteWeb.getCustomerNote(),
                  orderNoteWeb.getBillingFirstName(),
                  orderNoteWeb.getBillingLastName(),
                  orderNoteWeb.getBillingFullName(),
                  orderNoteWeb.getBillingDniPasaporte() != null
                        ? orderNoteWeb.getBillingDniPasaporte().replace(".", "")
                        : null,
                  orderNoteWeb.getBillingAddress(),
                  orderNoteWeb.getBillingCity(),
                  orderNoteWeb.getBillingState(),
                  orderNoteWeb.getBillingPostCode(),
                  orderNoteWeb.getBillingCountry(),
                  orderNoteWeb.getBillingEmail(),
                  orderNoteWeb.getBillingPhone(),
                  orderNoteWeb.getShippingFirstName(),
                  orderNoteWeb.getShippingLastName(),
                  orderNoteWeb.getShippingFullName(),
                  orderNoteWeb.getShippingAddress(),
                  orderNoteWeb.getShippingCity(),
                  orderNoteWeb.getShippingState(),
                  orderNoteWeb.getShippingPostCode(),
                  orderNoteWeb.getShippingCountryFull(),
                  orderNoteWeb.getPaymentMethodTitle(),
                  orderNoteWeb.getCartDiscount(),
                  orderNoteWeb.getOrderSubtotal(),
                  orderNoteWeb.getOrderSubtotalRefunded(),
                  orderNoteWeb.getShippingMethodTitle(),
                  orderNoteWeb.getOrderShipping(),
                  orderNoteWeb.getOrderShippingRefunded(),
                  orderNoteWeb.getOrderTotal(),
                  orderNoteWeb.getOrderTotalTax(),
                  orderNoteWeb.getOrderNotes(),
                  orderNoteWeb.getOriginalJson(),
                  null,
                  null);
            orderNote = orderNoteService.add(orderNote);
            for (ProductWeb productWeb : orderNoteWeb.getProducts()) {
               OffsetDateTime bookingStart = null;
               if (!productWeb.getBookingStart().isEmpty()) {
                  bookingStart = LocalDateTime.parse(productWeb.getBookingStart() + " 00:00:00", formatter)
                        .atOffset(ZoneOffset.UTC);
               }
               OffsetDateTime bookingEnd = null;
               if (!productWeb.getBookingEnd().isEmpty()) {
                  bookingEnd = LocalDateTime.parse(productWeb.getBookingEnd() + " 00:00:00", formatter)
                        .atOffset(ZoneOffset.UTC);
               }

               Long productId = null;
               try {
                  productId = productService.findByUnique(orderNote.getOrderNumberId(), productWeb.getLineId())
                        .getProductId();
               } catch (ProductException e) {
                  productId = null;
               }

               Integer bookingDuration = productWeb.getBookingDuration();
               if (bookingDuration == null) {
                  bookingDuration = 0;
               }

               Integer bookingPersons = productWeb.getBookingPersons();
               if (bookingPersons == null) {
                  bookingPersons = 0;
               }

               Product product = new Product(
                     productId,
                     orderNote.getOrderNumberId(),
                     productWeb.getSku(),
                     productWeb.getLineId(),
                     productWeb.getName(),
                     Integer.parseInt(productWeb.getQty()),
                     productWeb.getItemPrice(),
                     bookingStart,
                     bookingEnd,
                     bookingDuration,
                     bookingPersons,
                     productWeb.getPersonTypes(),
                     productWeb.getServiciosAdicionales(),
                     productWeb.getPuntoDeEncuentro(),
                     productWeb.getEncuentroHotel());
               product = productService.save(product);
            }
            PaymentWeb paymentWeb = orderNoteWeb.getPayment();
            if (paymentWeb != null) {
               OffsetDateTime fechaTransaccion = null;
               if (paymentWeb.getFechaTransaccion() != null) {
                  fechaTransaccion = LocalDateTime.parse(paymentWeb.getFechaTransaccion(), formatterLocal)
                        .atOffset(ZoneOffset.UTC);
               }
               OffsetDateTime fechaPago = null;
               if (paymentWeb.getFechaPago() != null) {
                  fechaPago = LocalDateTime.parse(paymentWeb.getFechaPago(), formatterLocal)
                        .atOffset(ZoneOffset.UTC);
               }
               Integer cuotas = null;
               if (paymentWeb.getCuotas() != null) {
                  cuotas = Integer.parseInt(paymentWeb.getCuotas());
               }
               Payment payment = new Payment(
                     orderNote.getOrderNumberId(),
                     paymentWeb.getTransaccionComercioId(),
                     paymentWeb.getTransaccionPlataformaId(),
                     paymentWeb.getTipo(),
                     new BigDecimal(paymentWeb.getMonto().replace(",", ".")),
                     paymentWeb.getEstado(),
                     paymentWeb.getDetalle(),
                     paymentWeb.getMetodoPago(),
                     paymentWeb.getMedioPago(),
                     Integer.valueOf(paymentWeb.getEstadoId()),
                     cuotas,
                     paymentWeb.getInformacionAdicional(),
                     paymentWeb.getMarcaTarjeta(),
                     paymentWeb.getInformacionAdicionalLink(),
                     fechaTransaccion,
                     fechaPago,
                     null,
                     null);
               payment = paymentService.save(payment);

               // Agregado para compensar la falta de date_completed y date_paid en order_note
               if (orderNote.getCompletedDate() == null) {
                  orderNote.setCompletedDate(payment.getFechaPago());
                  if (orderNote.getPaidDate() == null) {
                     orderNote.setPaidDate(payment.getFechaPago());
                  }
                  orderNote = orderNoteService.update(orderNote, orderNote.getOrderNumberId());
               }

               InformacionPagadorWeb informacionPagadorWeb = paymentWeb.getInformacionPagador();
               if (informacionPagadorWeb != null) {
                  if ("0".equals(paymentWeb.getMedioPago())) {
                     String email = (informacionPagadorWeb.getEMail() == null) ? ""
                           : informacionPagadorWeb.getEMail();
                     String nombre = (informacionPagadorWeb.getNombre() == null) ? ""
                           : informacionPagadorWeb.getNombre();
                     String numeroDocumento = (informacionPagadorWeb.getNumeroDocumento() == null) ? ""
                           : informacionPagadorWeb.getNumeroDocumento();
                     String telefono = (informacionPagadorWeb.getTelefono() == null) ? ""
                           : informacionPagadorWeb.getTelefono();
                     String tipoDocumento = (informacionPagadorWeb.getTipoDocumento() == null) ? ""
                           : informacionPagadorWeb.getTipoDocumento();
                     InformacionPagador informacionPagador = new InformacionPagador(
                           payment.getOrderNumberId(),
                           email,
                           nombre,
                           numeroDocumento,
                           telefono,
                           tipoDocumento);
                     log.info("informacionPagador={}", informacionPagador);
                     informacionPagadorService.save(informacionPagador);
                  } else {
                     if (informacionPagadorWeb.getNombre() == null
                           || informacionPagadorWeb.getNumeroDocumento() == null
                           || informacionPagadorWeb.getTipoDocumento() == null) {
                        log.error(
                              "InformacionPagador data is incomplete for orderNumberId={} and MedioPago is not 0. Skipping save.",
                              payment.getOrderNumberId());
                     } else {
                        InformacionPagador informacionPagador = new InformacionPagador(
                              payment.getOrderNumberId(),
                              informacionPagadorWeb.getEMail(),
                              informacionPagadorWeb.getNombre(),
                              informacionPagadorWeb.getNumeroDocumento(),
                              informacionPagadorWeb.getTelefono(),
                              informacionPagadorWeb.getTipoDocumento());
                        log.info("informacionPagador={}", informacionPagador);
                        informacionPagadorService.save(informacionPagador);
                     }
                  }
               }

               productTransactionService.deleteAllByOrderNumberId(orderNote.getOrderNumberId());

               for (ProductTransactionWeb productTransactionWeb : paymentWeb.getProductoTransactions()) {
                  ProductTransaction productTransaction = new ProductTransaction(
                        null,
                        orderNote.getOrderNumberId(),
                        productTransactionWeb.getNombreProducto(),
                        productTransactionWeb.getMontoProducto());
                  productTransaction = productTransactionService.save(productTransaction);
               }
            }
         } catch (NumberFormatException e) {
            log.info("Error importing -> {}", e.getMessage());
         }

      }

      return orderNotes;

   }

   public List<OrderNoteWeb> captureFromApi(ZonedDateTime from, ZonedDateTime to) {
      log.info("Capturing orders from Wordpress APIs: {} to {}", from, to);

      List<OrderNoteWeb> orderNotesSite1 = wordPressApiClientSite1.fetchOrders(from, to);
      List<OrderNoteWeb> orderNotesSite2 = wordPressApiClientSite2.fetchOrders(from, to);

      if (orderNotesSite1 == null) {
         orderNotesSite1 = new ArrayList<>();
      }
      if (orderNotesSite2 == null) {
         orderNotesSite2 = new ArrayList<>();
      }

      List<OrderNoteWeb> allOrderNotes = new ArrayList<>();
      allOrderNotes.addAll(orderNotesSite1);
      allOrderNotes.addAll(orderNotesSite2);

      log.info("Total orders fetched: {} (Site1: {}, Site2: {})",
            allOrderNotes.size(), orderNotesSite1.size(), orderNotesSite2.size());

      if (allOrderNotes.isEmpty()) {
         log.warn("No orders fetched from any API");
         return allOrderNotes;
      }

      processOrderNotes(allOrderNotes);

      return allOrderNotes;
   }

   private void extractPaymentFromOrderNotes(OrderNoteWeb orderNote) {
      if (orderNote.getOrderNotes() == null || orderNote.getOrderNotes().isEmpty()) {
         return;
      }

      try {
         int inicioPago = orderNote.getOrderNotes().lastIndexOf("PlusPago");
         String paymentType = "PlusPago";
         int paymentTypeLength = 8; // "PlusPago" word length

         // If PlusPago not found, try MacroClick
         if (inicioPago == -1) {
            inicioPago = orderNote.getOrderNotes().lastIndexOf("MacroClick");
            paymentType = "MacroClick";
            paymentTypeLength = 10; // "MacroClick" word length
         }

         int finPago = orderNote.getOrderNotes().indexOf("Una nueva reserva");

         if (inicioPago > -1 && finPago > -1) {
            String paymentString = orderNote.getOrderNotes().substring(inicioPago + paymentTypeLength, finPago - 1);
            PaymentWeb payment = new ObjectMapper().readValue(paymentString, PaymentWeb.class);
            orderNote.setPayment(payment);
            log.debug("Extracted {} payment data for order {}", paymentType, orderNote.getOrderNumber());
         }
      } catch (JsonProcessingException e) {
         log.warn("Could not parse PaymentWeb from order_notes for order {}: {}",
               orderNote.getOrderNumber(), e.getMessage());
      }
   }

   private void processOrderNotes(List<OrderNoteWeb> orderNotes) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      DateTimeFormatter formatterLocal = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

      for (OrderNoteWeb orderNoteWeb : orderNotes) {
         log.info("Processing orderNoteWeb={}", orderNoteWeb);
         try {
            extractPaymentFromOrderNotes(orderNoteWeb);
            OffsetDateTime orderDate = null;
            if (orderNoteWeb.getOrderDate() != null && !orderNoteWeb.getOrderDate().isEmpty()) {
               orderDate = LocalDateTime.parse(orderNoteWeb.getOrderDate(), formatter).atOffset(ZoneOffset.UTC);
            }
            OffsetDateTime paidDate = null;
            if (orderNoteWeb.getPaidDate() != null && !orderNoteWeb.getPaidDate().isEmpty()) {
               paidDate = LocalDateTime.parse(orderNoteWeb.getPaidDate(), formatter).atOffset(ZoneOffset.UTC);
            }
            OffsetDateTime completedDate = null;
            if (orderNoteWeb.getCompletedDate() != null && !orderNoteWeb.getCompletedDate().isEmpty()) {
               completedDate = LocalDateTime.parse(orderNoteWeb.getCompletedDate(), formatter)
                     .atOffset(ZoneOffset.UTC);
            }
            OffsetDateTime modifiedDate = null;
            if (orderNoteWeb.getModifiedDate() != null && !orderNoteWeb.getModifiedDate().isEmpty()) {
               modifiedDate = LocalDateTime.parse(orderNoteWeb.getModifiedDate(), formatter)
                     .atOffset(ZoneOffset.UTC);
            }

            OrderNote orderNote = new OrderNote(
                  Long.valueOf(orderNoteWeb.getOrderNumber()),
                  orderNoteWeb.getOrderStatus(),
                  orderDate,
                  paidDate,
                  completedDate,
                  modifiedDate,
                  orderNoteWeb.getOrderCurrency(),
                  orderNoteWeb.getCustomerNote(),
                  orderNoteWeb.getBillingFirstName(),
                  orderNoteWeb.getBillingLastName(),
                  orderNoteWeb.getBillingFullName(),
                  orderNoteWeb.getBillingDniPasaporte() != null
                        ? orderNoteWeb.getBillingDniPasaporte().replace(".", "")
                        : null,
                  orderNoteWeb.getBillingAddress(),
                  orderNoteWeb.getBillingCity(),
                  orderNoteWeb.getBillingState(),
                  orderNoteWeb.getBillingPostCode(),
                  orderNoteWeb.getBillingCountry(),
                  orderNoteWeb.getBillingEmail(),
                  orderNoteWeb.getBillingPhone(),
                  orderNoteWeb.getShippingFirstName(),
                  orderNoteWeb.getShippingLastName(),
                  orderNoteWeb.getShippingFullName(),
                  orderNoteWeb.getShippingAddress(),
                  orderNoteWeb.getShippingCity(),
                  orderNoteWeb.getShippingState(),
                  orderNoteWeb.getShippingPostCode(),
                  orderNoteWeb.getShippingCountryFull(),
                  orderNoteWeb.getPaymentMethodTitle(),
                  orderNoteWeb.getCartDiscount(),
                  orderNoteWeb.getOrderSubtotal(),
                  orderNoteWeb.getOrderSubtotalRefunded(),
                  orderNoteWeb.getShippingMethodTitle(),
                  orderNoteWeb.getOrderShipping(),
                  orderNoteWeb.getOrderShippingRefunded(),
                  orderNoteWeb.getOrderTotal(),
                  orderNoteWeb.getOrderTotalTax(),
                  orderNoteWeb.getOrderNotes(),
                  orderNoteWeb.getOriginalJson(),
                  null,
                  null);
            orderNote = orderNoteService.add(orderNote);

            if (orderNoteWeb.getProducts() != null) {
               for (ProductWeb productWeb : orderNoteWeb.getProducts()) {
                  OffsetDateTime bookingStart = null;
                  if (productWeb.getBookingStart() != null && !productWeb.getBookingStart().isEmpty()) {
                     bookingStart = LocalDateTime.parse(productWeb.getBookingStart() + " 00:00:00", formatter)
                           .atOffset(ZoneOffset.UTC);
                  }
                  OffsetDateTime bookingEnd = null;
                  if (productWeb.getBookingEnd() != null && !productWeb.getBookingEnd().isEmpty()) {
                     bookingEnd = LocalDateTime.parse(productWeb.getBookingEnd() + " 00:00:00", formatter)
                           .atOffset(ZoneOffset.UTC);
                  }

                  Long productId = null;
                  try {
                     productId = productService
                           .findByUnique(orderNote.getOrderNumberId(), productWeb.getLineId()).getProductId();
                  } catch (ProductException e) {
                     productId = null;
                  }

                  Integer bookingDuration = productWeb.getBookingDuration();
                  if (bookingDuration == null) {
                     bookingDuration = 0;
                  }

                  Integer bookingPersons = productWeb.getBookingPersons();
                  if (bookingPersons == null) {
                     bookingPersons = 0;
                  }

                  Product product = new Product(
                        productId,
                        orderNote.getOrderNumberId(),
                        productWeb.getSku(),
                        productWeb.getLineId(),
                        productWeb.getName(),
                        Integer.parseInt(productWeb.getQty()),
                        productWeb.getItemPrice(),
                        bookingStart,
                        bookingEnd,
                        bookingDuration,
                        bookingPersons,
                        productWeb.getPersonTypes(),
                        productWeb.getServiciosAdicionales(),
                        productWeb.getPuntoDeEncuentro(),
                        productWeb.getEncuentroHotel());
                  productService.save(product);
               }
            }

            PaymentWeb paymentWeb = orderNoteWeb.getPayment();
            if (paymentWeb != null) {
               OffsetDateTime fechaTransaccion = null;
               if (paymentWeb.getFechaTransaccion() != null) {
                  fechaTransaccion = LocalDateTime.parse(paymentWeb.getFechaTransaccion(), formatterLocal)
                        .atOffset(ZoneOffset.UTC);
               }
               OffsetDateTime fechaPago = null;
               if (paymentWeb.getFechaPago() != null) {
                  fechaPago = LocalDateTime.parse(paymentWeb.getFechaPago(), formatterLocal)
                        .atOffset(ZoneOffset.UTC);
               }
               Integer cuotas = null;
               if (paymentWeb.getCuotas() != null) {
                  cuotas = Integer.parseInt(paymentWeb.getCuotas());
               }
               Payment payment = new Payment(
                     orderNote.getOrderNumberId(),
                     paymentWeb.getTransaccionComercioId(),
                     paymentWeb.getTransaccionPlataformaId(),
                     paymentWeb.getTipo(),
                     new BigDecimal(paymentWeb.getMonto().replace(",", ".")),
                     paymentWeb.getEstado(),
                     paymentWeb.getDetalle(),
                     paymentWeb.getMetodoPago(),
                     paymentWeb.getMedioPago(),
                     Integer.valueOf(paymentWeb.getEstadoId()),
                     cuotas,
                     paymentWeb.getInformacionAdicional(),
                     paymentWeb.getMarcaTarjeta(),
                     paymentWeb.getInformacionAdicionalLink(),
                     fechaTransaccion,
                     fechaPago,
                     null,
                     null);
               payment = paymentService.save(payment);

               // Agregado para compensar la falta de date_completed y date_paid en order_note
               if (orderNote.getCompletedDate() == null) {
                  orderNote.setCompletedDate(payment.getFechaPago());
                  if (orderNote.getPaidDate() == null) {
                     orderNote.setPaidDate(payment.getFechaPago());
                  }
                  orderNote = orderNoteService.update(orderNote, orderNote.getOrderNumberId());
               }

               InformacionPagadorWeb informacionPagadorWeb = paymentWeb.getInformacionPagador();
               if (informacionPagadorWeb != null) {
                  if ("0".equals(paymentWeb.getMedioPago())) {
                     String email = (informacionPagadorWeb.getEMail() == null) ? ""
                           : informacionPagadorWeb.getEMail();
                     String nombre = (informacionPagadorWeb.getNombre() == null) ? ""
                           : informacionPagadorWeb.getNombre();
                     String numeroDocumento = (informacionPagadorWeb.getNumeroDocumento() == null) ? ""
                           : informacionPagadorWeb.getNumeroDocumento();
                     String telefono = (informacionPagadorWeb.getTelefono() == null) ? ""
                           : informacionPagadorWeb.getTelefono();
                     String tipoDocumento = (informacionPagadorWeb.getTipoDocumento() == null) ? ""
                           : informacionPagadorWeb.getTipoDocumento();
                     InformacionPagador informacionPagador = new InformacionPagador(
                           payment.getOrderNumberId(),
                           email,
                           nombre,
                           numeroDocumento,
                           telefono,
                           tipoDocumento);
                     log.info("informacionPagador={}", informacionPagador);
                     informacionPagadorService.save(informacionPagador);
                  } else {
                     if (informacionPagadorWeb.getNombre() == null
                           || informacionPagadorWeb.getNumeroDocumento() == null
                           || informacionPagadorWeb.getTipoDocumento() == null) {
                        log.error(
                              "InformacionPagador data is incomplete for orderNumberId={} and MedioPago is not 0. Skipping save.",
                              payment.getOrderNumberId());
                     } else {
                        InformacionPagador informacionPagador = new InformacionPagador(
                              payment.getOrderNumberId(),
                              informacionPagadorWeb.getEMail(),
                              informacionPagadorWeb.getNombre(),
                              informacionPagadorWeb.getNumeroDocumento(),
                              informacionPagadorWeb.getTelefono(),
                              informacionPagadorWeb.getTipoDocumento());
                        log.info("informacionPagador={}", informacionPagador);
                        informacionPagadorService.save(informacionPagador);
                     }
                  }
               }

               productTransactionService.deleteAllByOrderNumberId(orderNote.getOrderNumberId());

               if (paymentWeb.getProductoTransactions() != null) {
                  for (ProductTransactionWeb productTransactionWeb : paymentWeb.getProductoTransactions()) {
                     ProductTransaction productTransaction = new ProductTransaction(
                           null,
                           orderNote.getOrderNumberId(),
                           productTransactionWeb.getNombreProducto(),
                           productTransactionWeb.getMontoProducto());
                     productTransactionService.save(productTransaction);
                  }
               }
            }
         } catch (NumberFormatException e) {
            log.info("Error importing -> {}", e.getMessage());
         }
      }
   }

}
