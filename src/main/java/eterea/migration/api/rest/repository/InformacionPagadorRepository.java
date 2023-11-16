package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.kotlin.model.InformacionPagador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformacionPagadorRepository extends JpaRepository<InformacionPagador, Long> {
}
