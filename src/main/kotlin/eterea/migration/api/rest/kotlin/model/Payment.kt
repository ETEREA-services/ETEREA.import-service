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

    @Column(columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
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

    @OneToOne
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    var informacionPagador: InformacionPagador? = null,

    @OneToMany
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    var productTransactions: List<ProductTransaction?>? = null

) : Auditable()
