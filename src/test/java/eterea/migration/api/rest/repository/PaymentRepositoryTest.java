package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.configuration.EtereaMigrationConfiguration;
import eterea.migration.api.rest.model.Payment;
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
class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void whenFindById_thenReturnPayment() {
        // given
        Payment payment = new Payment();
        payment.setOrderNumberId(98765L);
        payment.setMonto(new BigDecimal("150.75"));
        payment.setMetodoPago("Credit Card");
        entityManager.persistAndFlush(payment);

        // when
        Optional<Payment> found = paymentRepository.findById(payment.getOrderNumberId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getMetodoPago()).isEqualTo(payment.getMetodoPago());
    }
}
