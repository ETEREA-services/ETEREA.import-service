package eterea.migration.api.dto;

import java.time.LocalDateTime;

public record ServiceStatusDto(
    LocalDateTime lastUpdate,
    int lastUpdateCount,
    long lastOrderNumberId,
    long minutesSinceLastUpdate
) {
   
}
