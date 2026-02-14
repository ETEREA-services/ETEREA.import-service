package eterea.migration.rest.repository;

import eterea.migration.rest.model.OrderNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {

    Optional<OrderNote> findByOrderNumberId(Long orderNumberId);

    Optional<OrderNote> findTopByBillingDniPasaporteOrderByOrderNumberIdDesc(String billingDniPasaporte);

    Optional<OrderNote> findTopByBillingDniPasaporteContainsAndOrderTotalOrderByOrderNumberIdDesc(String billingDniPasaporte, BigDecimal orderTotal);

    List<OrderNote> findAllByOrderStatusAndCompletedDateGreaterThanEqual(String completado, OffsetDateTime completedDate);

    List<OrderNote> findAllByOrderStatusInAndCompletedDateGreaterThanEqual(List<String> orderStatus, OffsetDateTime completedDate);

    Optional<OrderNote> findTopByOrderByCreatedDesc();

    int countByCreatedBetween(LocalDateTime start, LocalDateTime end);
}
