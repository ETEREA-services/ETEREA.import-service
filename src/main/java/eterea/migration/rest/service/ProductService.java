package eterea.migration.rest.service;

import eterea.migration.rest.exception.ProductException;
import eterea.migration.rest.model.Product;
import eterea.migration.rest.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Product save(Product product) {
        return repository.save(product);
    }

    public Product findByUnique(Long orderNumberId, Integer lineId) {
        return repository.findByOrderNumberIdAndLineId(orderNumberId, lineId).orElseThrow(() -> new ProductException(orderNumberId, lineId));
    }

}
