package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.model.ProductTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long> {

    void deleteAllByOrderNumberId(Long orderNumberId);

}
