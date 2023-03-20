package eterea.migration.api.rest.kotlin.model

import com.fasterxml.jackson.annotation.JsonProperty
import eterea.migration.api.rest.model.Auditable
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table
data class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var productId: Long? = null,
    var orderNumberId: Long? = null,
    var sku: String? = "",
    var lineId: Int = 0,
    var name: String = "",
    var qty: Int = 0,
    var itemPrice: BigDecimal = BigDecimal.ZERO,
    var bookingStart: OffsetDateTime? = null,
    var bookingEnd: OffsetDateTime? = null,
    var bookingDuration: Int = 0,
    var bookingPersons: Int = 0,
    var personTypes: String = "",
    var serviciosAdicionales: String = "",
    var puntoDeEncuentro: String = "",
    var encuentroHotel: String = "",

    @ManyToOne
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    var orderNote: OrderNote? = null

) : Auditable()
