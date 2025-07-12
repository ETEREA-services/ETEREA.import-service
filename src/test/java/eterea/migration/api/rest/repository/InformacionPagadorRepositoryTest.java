package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.configuration.EtereaMigrationConfiguration;
import eterea.migration.api.rest.model.InformacionPagador;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EtereaMigrationConfiguration.class)
class InformacionPagadorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InformacionPagadorRepository informacionPagadorRepository;

    @Test
    void whenFindById_thenReturnInformacionPagador() {
        // given
        InformacionPagador info = new InformacionPagador();
        info.setOrderNumberId(112233L);
        info.setNombre("John Doe");
        info.setEMail("john.doe@example.com");
        entityManager.persistAndFlush(info);

        // when
        Optional<InformacionPagador> found = informacionPagadorRepository.findById(info.getOrderNumberId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo(info.getNombre());
    }
}
