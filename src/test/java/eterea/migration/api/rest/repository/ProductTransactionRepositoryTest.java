package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.configuration.EtereaMigrationConfiguration;
import eterea.migration.api.rest.model.ProductTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EtereaMigrationConfiguration.class)
class ProductTransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductTransactionRepository productTransactionRepository;

    @Test
    void whenFindById_thenReturnProductTransaction() {
        // given
        ProductTransaction transaction = new ProductTransaction();
        transaction.setNombreProducto("Service Fee");
        ProductTransaction savedTransaction = entityManager.persistAndFlush(transaction);

        // when
        Optional<ProductTransaction> found = productTransactionRepository.findById(savedTransaction.getProductTransactionId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNombreProducto()).isEqualTo(transaction.getNombreProducto());
    }
}
