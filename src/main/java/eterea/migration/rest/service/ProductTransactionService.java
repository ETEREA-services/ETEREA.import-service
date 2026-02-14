package eterea.migration.rest.service;

import eterea.migration.rest.model.ProductTransaction;
import eterea.migration.rest.repository.ProductTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductTransactionService {

    private final ProductTransactionRepository repository;

    @Transactional
    public void deleteAllByOrderNumberId(Long orderNumberId) {
        repository.deleteAllByOrderNumberId(orderNumberId);
    }

    public ProductTransaction save(ProductTransaction productTransaction) {
        return repository.save(productTransaction);
    }

}
