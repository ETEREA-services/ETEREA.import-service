package eterea.migration.rest.repository;

import eterea.migration.rest.model.InformacionPagador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformacionPagadorRepository extends JpaRepository<InformacionPagador, Long> {
}
