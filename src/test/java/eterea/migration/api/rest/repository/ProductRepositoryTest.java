package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.configuration.EtereaMigrationConfiguration;
import eterea.migration.api.rest.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EtereaMigrationConfiguration.class)
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void whenFindById_thenReturnProduct() {
        // given
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("TEST-SKU-001");
        Product savedProduct = entityManager.persistAndFlush(product);

        // when
        Optional<Product> found = productRepository.findById(savedProduct.getProductId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(product.getName());
    }
}
