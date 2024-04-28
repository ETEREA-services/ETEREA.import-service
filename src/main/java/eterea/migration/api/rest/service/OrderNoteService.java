package eterea.migration.api.rest.service;

import eterea.migration.api.rest.exception.OrderNoteException;
import eterea.migration.api.rest.kotlin.model.OrderNote;
import eterea.migration.api.rest.repository.OrderNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OrderNoteService {

    private final OrderNoteRepository repository;

    @Autowired
    public OrderNoteService(OrderNoteRepository repository) {
        this.repository = repository;
    }

    public OrderNote findByOrderNumberId(Long orderNumberId) {
        return repository.findByOrderNumberId(orderNumberId).orElseThrow(() -> new OrderNoteException(orderNumberId));
    }

    public List<OrderNote> findAllCompletedByLastTwoDays() {
        OffsetDateTime completedDate = OffsetDateTime.now().minusDays(1);
        return repository.findAllByOrderStatusAndCompletedDateGreaterThanEqual("Completado", completedDate);
    }

    public OrderNote add(OrderNote orderNote) {
        return repository.save(orderNote);
    }

    public OrderNote update(OrderNote newOrderNote, Long orderNumberId) {
        return repository.findByOrderNumberId(orderNumberId).map(orderNote -> {
            orderNote = new OrderNote(orderNumberId, newOrderNote.getOrderStatus(), newOrderNote.getOrderDate(), newOrderNote.getPaidDate(), newOrderNote.getCompletedDate(), newOrderNote.getModifiedDate()
                    , newOrderNote.getOrderCurrency(), newOrderNote.getCustomerNote(), newOrderNote.getBillingFirstName(), newOrderNote.getBillingLastName(), newOrderNote.getBillingFullName()
                    , newOrderNote.getBillingDniPasaporte(), newOrderNote.getBillingAddress(), newOrderNote.getBillingCity(), newOrderNote.getBillingState(), newOrderNote.getBillingPostCode()
                    , newOrderNote.getBillingCountry(), newOrderNote.getBillingEmail(), newOrderNote.getBillingPhone(), newOrderNote.getShippingFirstName(), newOrderNote.getShippingLastName()
                    , newOrderNote.getShippingFullName(), newOrderNote.getShippingAddress(), newOrderNote.getShippingCity(), newOrderNote.getShippingState(), newOrderNote.getShippingPostCode()
                    , newOrderNote.getShippingCountryFull(), newOrderNote.getPaymentMethodTitle(), newOrderNote.getCartDiscount(), newOrderNote.getOrderSubtotal(), newOrderNote.getOrderSubtotalRefunded()
                    , newOrderNote.getShippingMethodTitle(), newOrderNote.getOrderShipping(), newOrderNote.getOrderShippingRefunded(), newOrderNote.getOrderTotal(), newOrderNote.getOrderTotalTax()
                    , newOrderNote.getOrderNotes(), newOrderNote.getProducts(), newOrderNote.getPayment());
            return repository.save(orderNote);

        }).orElseThrow(() -> new OrderNoteException(orderNumberId));
    }
}
