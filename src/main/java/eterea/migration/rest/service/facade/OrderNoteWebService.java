package eterea.migration.rest.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eterea.migration.rest.client.WordPressApiClient;
import eterea.migration.rest.exception.ProductException;
import eterea.migration.rest.extern.*;
import eterea.migration.rest.model.*;
import eterea.migration.rest.service.*;
import eterea.migration.rest.service.internal.FileService;
import eterea.migration.tool.ToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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
@RequiredArgsConstructor
public class OrderNoteWebService {

   private final FileService fileService;
   private final OrderNoteService orderNoteService;
   private final ProductService productService;
   private final PaymentService paymentService;
   private final InformacionPagadorService informacionPagadorService;
   private final ProductTransactionService productTransactionService;
   private final WordPressApiClient wordPressApiClient;
   private final Environment environment;

   public List<OrderNoteWeb> capture() {
      log.info("Capturing orders...");
      ObjectMapper objectMapper = new ObjectMapper();
      File file = new File(fileService.getFile());
      List<OrderNoteWeb> orderNotes = new ArrayList<>();
      try {
         JsonNode jsonArray = objectMapper.readTree(file);
         for (JsonNode node : jsonArray) {
            OrderNoteWeb orderNote = objectMapper.treeToValue(node, OrderNoteWeb.class);
            orderNote.setOriginalJson(node.toString());
            orderNotes.add(orderNote);
         }
      } catch (IOException e) {
         log.debug("IOException - {}", e.getMessage());
      }

      processOrderNotes(orderNotes);

      return orderNotes;
   }

   public List<OrderNoteWeb> captureFromApi(ZonedDateTime from, ZonedDateTime to) {
      log.info("Capturing orders from Wordpress APIs: {} to {}", from, to);

      List<OrderNoteWeb> orderNotes = wordPressApiClient.fetchOrders(from, to);

      if (orderNotes == null) {
         orderNotes = new ArrayList<>();
      }

      log.info("Total orders fetched: {}", orderNotes.size());

      if (orderNotes.isEmpty()) {
         log.warn("No orders fetched from API");
         return orderNotes;
      }

      processOrderNotes(orderNotes);

      return orderNotes;
   }

   public List<OrderNoteWeb> captureFromExportFeed() {
      int exportRecheckDays = environment.getProperty("app.wordpress.export-recheck-days", Integer.class, 2);
      log.info("Capturing orders from Wordpress export-feed (export_recheck_days={})", exportRecheckDays);

      List<OrderNoteWeb> orderNotes = wordPressApiClient.fetchExportFeed(exportRecheckDays);

      if (orderNotes == null) {
         orderNotes = new ArrayList<>();
      }

      log.info("Total orders fetched from export-feed: {}", orderNotes.size());

      if (orderNotes.isEmpty()) {
         log.warn("No orders fetched from export-feed");
         return orderNotes;
      }

      processOrderNotes(orderNotes);

      return orderNotes;
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

   // TODO (mejora futura): hacer este método @Transactional por orden, de modo que las 5 escrituras
   // (order_note, product, payment, informacion_pagador, product_transaction) de cada orden se
   // confirmen atómicamente o se reviertan en bloque. Hoy no hay límite transaccional: un fallo a
   // mitad de orden puede dejarla a medio escribir. Se deja tal cual a propósito durante la migración
   // de archivo->HTTP para preservar el comportamiento idéntico al anterior; la mejora va después.
   private void processOrderNotes(List<OrderNoteWeb> orderNotes) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      DateTimeFormatter formatterLocal = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

      for (OrderNoteWeb orderNoteWeb : orderNotes) {
         log.info("Processing order {} (status={})", orderNoteWeb.getOrderNumber(), orderNoteWeb.getOrderStatus());
         log.debug("orderNoteWeb={}", orderNoteWeb);
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

            var orderNote = new OrderNote(
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
                          ? ToolService.onlyNumbers(orderNoteWeb.getBillingDniPasaporte())
                          : null,
                  orderNoteWeb.getBillingAddress(),
                  ToolService.truncate(orderNoteWeb.getBillingCity(), 255),
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

                  var product = new Product(
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

            var paymentWeb = orderNoteWeb.getPayment();
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

               var informacionPagadorWeb = paymentWeb.getInformacionPagador();
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
                     log.debug("informacionPagador={}", informacionPagador);
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
                        log.debug("informacionPagador={}", informacionPagador);
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
            log.warn("Skipped order {}: {}", orderNoteWeb.getOrderNumber(), e.getMessage());
         }
      }
   }

}
