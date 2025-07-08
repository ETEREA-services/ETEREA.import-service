package eterea.migration.api.rest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eterea.migration.api.rest.exception.OrderNoteException;
import eterea.migration.api.rest.kotlin.model.OrderNote;
import eterea.migration.api.rest.repository.OrderNoteRepository;
import eterea.migration.api.rest.service.internal.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class OrderNoteService {

    private final OrderNoteRepository repository;

    public OrderNoteService(OrderNoteRepository repository) {
        this.repository = repository;
    }

    public OrderNote findByOrderNumberId(Long orderNumberId) {
        return repository.findByOrderNumberId(orderNumberId).orElseThrow(() -> new OrderNoteException(orderNumberId));
    }

    public List<OrderNote> findAllCompletedByLastTwoDays() {
        OffsetDateTime completedDate = OffsetDateTime.now().minusDays(1);
        var completed_status = Arrays.asList("Completado", "Completed");
        return repository.findAllByOrderStatusInAndCompletedDateGreaterThanEqual(completed_status, completedDate);
    }

    public OrderNote findLastByNumeroDocumento(Long numeroDocumento) {
        var orderNote = repository.findTopByBillingDniPasaporteOrderByOrderNumberIdDesc(String.valueOf(numeroDocumento)).orElseThrow(() -> new OrderNoteException(numeroDocumento));
        logOrderNote(orderNote);
        return orderNote;
    }

    public OrderNote add(OrderNote orderNote) {
        sanitizeOrderNote(orderNote);
        return repository.save(orderNote);
    }

    public OrderNote update(OrderNote newOrderNote, Long orderNumberId) {
        return repository.findByOrderNumberId(orderNumberId).map(orderNote -> {
            sanitizeOrderNote(newOrderNote);
            orderNote = new OrderNote(orderNumberId,
                    newOrderNote.getOrderStatus(),
                    newOrderNote.getOrderDate(),
                    newOrderNote.getPaidDate(),
                    newOrderNote.getCompletedDate(),
                    newOrderNote.getModifiedDate(),
                    newOrderNote.getOrderCurrency(),
                    newOrderNote.getCustomerNote(),
                    newOrderNote.getBillingFirstName(),
                    newOrderNote.getBillingLastName(),
                    newOrderNote.getBillingFullName(),
                    newOrderNote.getBillingDniPasaporte(),
                    newOrderNote.getBillingAddress(),
                    newOrderNote.getBillingCity(),
                    newOrderNote.getBillingState(),
                    newOrderNote.getBillingPostCode(),
                    newOrderNote.getBillingCountry(),
                    newOrderNote.getBillingEmail(),
                    newOrderNote.getBillingPhone(),
                    newOrderNote.getShippingFirstName(),
                    newOrderNote.getShippingLastName(),
                    newOrderNote.getShippingFullName(),
                    newOrderNote.getShippingAddress(),
                    newOrderNote.getShippingCity(),
                    newOrderNote.getShippingState(),
                    newOrderNote.getShippingPostCode(),
                    newOrderNote.getShippingCountryFull(),
                    newOrderNote.getPaymentMethodTitle(),
                    newOrderNote.getCartDiscount(),
                    newOrderNote.getOrderSubtotal(),
                    newOrderNote.getOrderSubtotalRefunded(),
                    newOrderNote.getShippingMethodTitle(),
                    newOrderNote.getOrderShipping(),
                    newOrderNote.getOrderShippingRefunded(),
                    newOrderNote.getOrderTotal(),
                    newOrderNote.getOrderTotalTax(),
                    newOrderNote.getOrderNotes(),
                    newOrderNote.getFullPayload(),
                    newOrderNote.getProducts(),
                    newOrderNote.getPayment()
            );
            return repository.save(orderNote);

        }).orElseThrow(() -> new OrderNoteException(orderNumberId));
    }

    private void sanitizeOrderNote(OrderNote orderNote) {
        orderNote.setCustomerNote(StringUtils.stripDiacritics(orderNote.getCustomerNote()));
        orderNote.setBillingFirstName(StringUtils.stripDiacritics(orderNote.getBillingFirstName()));
        orderNote.setBillingLastName(StringUtils.stripDiacritics(orderNote.getBillingLastName()));
        orderNote.setBillingFullName(StringUtils.stripDiacritics(orderNote.getBillingFullName()));
        orderNote.setBillingDniPasaporte(StringUtils.stripDiacritics(orderNote.getBillingDniPasaporte()));
        orderNote.setBillingAddress(StringUtils.stripDiacritics(orderNote.getBillingAddress()));
        orderNote.setBillingCity(StringUtils.stripDiacritics(orderNote.getBillingCity()));
        orderNote.setBillingState(StringUtils.stripDiacritics(orderNote.getBillingState()));
        orderNote.setBillingEmail(StringUtils.stripDiacritics(orderNote.getBillingEmail()));
        orderNote.setShippingFirstName(StringUtils.stripDiacritics(orderNote.getShippingFirstName()));
        orderNote.setShippingLastName(StringUtils.stripDiacritics(orderNote.getShippingLastName()));
        orderNote.setShippingFullName(StringUtils.stripDiacritics(orderNote.getShippingFullName()));
        orderNote.setShippingAddress(StringUtils.stripDiacritics(orderNote.getShippingAddress()));
        orderNote.setShippingCity(StringUtils.stripDiacritics(orderNote.getShippingCity()));
        orderNote.setShippingState(StringUtils.stripDiacritics(orderNote.getShippingState()));
        orderNote.setPaymentMethodTitle(StringUtils.stripDiacritics(orderNote.getPaymentMethodTitle()));
        orderNote.setShippingMethodTitle(StringUtils.stripDiacritics(orderNote.getShippingMethodTitle()));
        orderNote.setOrderNotes(StringUtils.stripDiacritics(orderNote.getOrderNotes()));
    }

    private void logOrderNote(OrderNote orderNote) {
        try {
            log.debug("OrderNote -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(orderNote));
        } catch (JsonProcessingException e) {
            log.debug("OrderNote jsonify error -> {}", e.getMessage());
        }
    }

}
