package eterea.migration.api.rest.repository;

import eterea.migration.api.rest.kotlin.model.OrderNote;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {

    Optional<OrderNote> findByOrderNumberId(Long orderNumberId);

    Optional<OrderNote> findTopByBillingDniPasaporteOrderByOrderNumberIdDesc(String billingDniPasaporte);

    List<OrderNote> findAllByOrderStatusAndCompletedDateGreaterThanEqual(String completado, OffsetDateTime completedDate);

    List<OrderNote> findAllByOrderStatusInAndCompletedDateGreaterThanEqual(List<String> orderStatus, OffsetDateTime completedDate);

}
