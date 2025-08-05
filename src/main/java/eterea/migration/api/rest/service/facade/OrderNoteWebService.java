package eterea.migration.api.rest.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eterea.migration.api.rest.exception.ProductException;
import eterea.migration.api.rest.extern.*;
import eterea.migration.api.rest.model.*;
import eterea.migration.api.rest.service.*;
import eterea.migration.api.rest.service.internal.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    public OrderNoteWebService(FileService fileService, OrderNoteService orderNoteService, ProductService productService, PaymentService paymentService, InformacionPagadorService informacionPagadorService, ProductTransactionService productTransactionService) {
        this.fileService = fileService;
        this.orderNoteService = orderNoteService;
        this.productService = productService;
        this.paymentService = paymentService;
        this.informacionPagadorService = informacionPagadorService;
        this.productTransactionService = productTransactionService;
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
                    completedDate = LocalDateTime.parse(orderNoteWeb.getCompletedDate(), formatter).atOffset(ZoneOffset.UTC);
                }
                OffsetDateTime modifiedDate = null;
                if (!orderNoteWeb.getModifiedDate().isEmpty()) {
                    modifiedDate = LocalDateTime.parse(orderNoteWeb.getModifiedDate(), formatter).atOffset(ZoneOffset.UTC);
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
                        orderNoteWeb.getBillingDniPasaporte() != null ? orderNoteWeb.getBillingDniPasaporte().replace(".", "") : null,
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
                        null
                );
                orderNote = orderNoteService.add(orderNote);
                for (ProductWeb productWeb : orderNoteWeb.getProducts()) {
                    OffsetDateTime bookingStart = null;
                    if (!productWeb.getBookingStart().isEmpty()) {
                        bookingStart = LocalDateTime.parse(productWeb.getBookingStart() + " 00:00:00", formatter).atOffset(ZoneOffset.UTC);
                    }
                    OffsetDateTime bookingEnd = null;
                    if (!productWeb.getBookingEnd().isEmpty()) {
                        bookingEnd = LocalDateTime.parse(productWeb.getBookingEnd() + " 00:00:00", formatter).atOffset(ZoneOffset.UTC);
                    }

                    Long productId = null;
                    try {
                        productId = productService.findByUnique(orderNote.getOrderNumberId(), productWeb.getLineId()).getProductId();
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
                            productWeb.getEncuentroHotel()
                    );
                    product = productService.save(product);
                }
                PaymentWeb paymentWeb = orderNoteWeb.getPayment();
                if (paymentWeb != null) {
                    OffsetDateTime fechaTransaccion = null;
                    if (paymentWeb.getFechaTransaccion() != null) {
                        fechaTransaccion = LocalDateTime.parse(paymentWeb.getFechaTransaccion(), formatterLocal).atOffset(ZoneOffset.UTC);
                    }
                    OffsetDateTime fechaPago = null;
                    if (paymentWeb.getFechaPago() != null) {
                        fechaPago = LocalDateTime.parse(paymentWeb.getFechaPago(), formatterLocal).atOffset(ZoneOffset.UTC);
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
                            null
                    );
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
                            String email = (informacionPagadorWeb.getEMail() == null) ? "" : informacionPagadorWeb.getEMail();
                            String nombre = (informacionPagadorWeb.getNombre() == null) ? "" : informacionPagadorWeb.getNombre();
                            String numeroDocumento = (informacionPagadorWeb.getNumeroDocumento() == null) ? "" : informacionPagadorWeb.getNumeroDocumento();
                            String telefono = (informacionPagadorWeb.getTelefono() == null) ? "" : informacionPagadorWeb.getTelefono();
                            String tipoDocumento = (informacionPagadorWeb.getTipoDocumento() == null) ? "" : informacionPagadorWeb.getTipoDocumento();
                            InformacionPagador informacionPagador = new InformacionPagador(
                                    payment.getOrderNumberId(),
                                    email,
                                    nombre,
                                    numeroDocumento,
                                    telefono,
                                    tipoDocumento
                            );
                            log.info("informacionPagador={}", informacionPagador);
                            informacionPagadorService.save(informacionPagador);
                        } else {
                            if (informacionPagadorWeb.getNombre() == null || informacionPagadorWeb.getNumeroDocumento() == null || informacionPagadorWeb.getTipoDocumento() == null) {
                                log.error("InformacionPagador data is incomplete for orderNumberId={} and MedioPago is not 0. Skipping save.", payment.getOrderNumberId());
                            } else {
                                InformacionPagador informacionPagador = new InformacionPagador(
                                        payment.getOrderNumberId(),
                                        informacionPagadorWeb.getEMail(),
                                        informacionPagadorWeb.getNombre(),
                                        informacionPagadorWeb.getNumeroDocumento(),
                                        informacionPagadorWeb.getTelefono(),
                                        informacionPagadorWeb.getTipoDocumento()
                                );
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
                                productTransactionWeb.getMontoProducto()
                        );
                        productTransaction = productTransactionService.save(productTransaction);
                    }
                }
            } catch (NumberFormatException e) {
                log.info("Error importing -> {}", e.getMessage());
            }

        }

        return orderNotes;

    }

}
