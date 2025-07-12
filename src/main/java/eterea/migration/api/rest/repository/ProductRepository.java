package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    public Optional<Product> findByOrderNumberIdAndLineId(Long orderNumberId, Integer lineId);

}
