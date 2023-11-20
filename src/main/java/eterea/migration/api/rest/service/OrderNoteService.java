package eterea.migration.api.rest.service;

import eterea.migration.api.rest.exception.OrderNoteException;
import eterea.migration.api.rest.kotlin.model.OrderNote;
import eterea.migration.api.rest.repository.OrderNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderNoteService {

    private final OrderNoteRepository repository;

    @Autowired
    public OrderNoteService(OrderNoteRepository repository) {
        this.repository = repository;
    }

    public OrderNote add(OrderNote orderNote) {
        return repository.save(orderNote);
    }

    public OrderNote findByOrderNumberId(Long orderNumberId) {
        return repository.findByOrderNumberId(orderNumberId).orElseThrow(() -> new OrderNoteException(orderNumberId));
    }

}
