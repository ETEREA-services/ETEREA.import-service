package eterea.migration.api.rest.kotlin.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table
data class Payment(

    @Id
    var orderNumberId: Long? = null,
    var transaccionComercioId: String = "",
    var transaccionPlataformaId: String = "",
    var tipo: String = "",
    var monto: BigDecimal = BigDecimal.ZERO,
    var estado: String = "",
    var detalle: String = "",
    var metodoPago: String? = null,
    var medioPago: String? = null,
    var estadoId: Int? = null,
    var cuotas: Int? = null,
    var informacionAdicional: String? = null,
    var marcaTarjeta: String? = null,
    var informacionAdicionalLink: String? = null,
    var fechaTransaccion: OffsetDateTime? = null,
    var fechaPago: OffsetDateTime? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    var orderNote: OrderNote? = null

) : Auditable()
