package eterea.migration.api.rest.service;

import eterea.migration.api.rest.model.Payment;
import eterea.migration.api.rest.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

   private final PaymentRepository repository;

   public Payment save(Payment payment) {
      return repository.save(payment);
   }

}
