package eterea.migration.dto;

import java.time.LocalDateTime;

public record ServiceStatusDto(
    LocalDateTime lastUpdate,
    int lastUpdateCount,
    long lastOrderNumberId,
    long minutesSinceLastUpdate
) {
   
}
