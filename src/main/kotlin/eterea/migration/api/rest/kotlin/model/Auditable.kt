package eterea.migration.api.rest.kotlin.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import jakarta.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class Auditable {

    @field:CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    open var created: LocalDateTime? = null

    @field:LastModifiedDate
    @Column(name = "updated")
    open var updated: LocalDateTime? = null
}
