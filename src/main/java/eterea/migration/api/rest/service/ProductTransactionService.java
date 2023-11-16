package eterea.migration.api.rest.service;

import eterea.migration.api.rest.kotlin.model.ProductTransaction;
import eterea.migration.api.rest.repository.ProductTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProductTransactionService {

    private final ProductTransactionRepository repository;

    public ProductTransactionService(ProductTransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void deleteAllByOrderNumberId(Long orderNumberId) {
        repository.deleteAllByOrderNumberId(orderNumberId);
    }

    public ProductTransaction save(ProductTransaction productTransaction) {
        return repository.save(productTransaction);
    }

}
