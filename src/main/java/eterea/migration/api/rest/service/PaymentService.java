package eterea.migration.api.rest.service;

import eterea.migration.api.rest.model.Payment;
import eterea.migration.api.rest.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    @Autowired
    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment save(Payment payment) {
        return repository.save(payment);
    }

}
