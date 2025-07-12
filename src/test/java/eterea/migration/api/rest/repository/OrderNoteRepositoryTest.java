package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.configuration.EtereaMigrationConfiguration;
import eterea.migration.api.rest.model.OrderNote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EtereaMigrationConfiguration.class)
class OrderNoteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderNoteRepository orderNoteRepository;

    @Test
    void whenFindById_thenReturnOrderNote() {
        // given
        OrderNote orderNote = new OrderNote();
        orderNote.setOrderNumberId(12345L);
        orderNote.setBillingEmail("test@example.com");
        orderNote.setOrderTotal(new BigDecimal("99.99"));
        entityManager.persistAndFlush(orderNote);

        // when
        Optional<OrderNote> found = orderNoteRepository.findById(orderNote.getOrderNumberId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getBillingEmail()).isEqualTo(orderNote.getBillingEmail());
    }
}
