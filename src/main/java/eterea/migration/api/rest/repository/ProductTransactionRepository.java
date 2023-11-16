package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.kotlin.model.ProductTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long> {

    public void deleteAllByOrderNumberId(Long orderNumberId);

}
