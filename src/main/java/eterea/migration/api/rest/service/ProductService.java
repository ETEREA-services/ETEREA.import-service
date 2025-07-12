package eterea.migration.api.rest.service;

import eterea.migration.api.rest.exception.ProductException;
import eterea.migration.api.rest.model.Product;
import eterea.migration.api.rest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository repository;

    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public Product findByUnique(Long orderNumberId, Integer lineId) {
        return repository.findByOrderNumberIdAndLineId(orderNumberId, lineId).orElseThrow(() -> new ProductException(orderNumberId, lineId));
    }

}
