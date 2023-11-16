package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.kotlin.model.OrderNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {
}
