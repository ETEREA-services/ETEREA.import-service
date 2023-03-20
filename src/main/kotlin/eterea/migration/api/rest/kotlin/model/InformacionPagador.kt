package eterea.migration.api.rest.kotlin.model

import eterea.migration.api.rest.model.Auditable
import jakarta.persistence.*

@Entity
@Table
data class InformacionPagador(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var informacionPagadorId: Long? = null,
    var paymentId: Long? = null,
    var eMail: String? = "",
    var nombre: String = "",
    var numeroDocumento: String = "",
    var telefono: String? = null,
    var tipoDocumento: String = "",

    @OneToOne(optional = true)
    @JoinColumn(name = "paymentId", insertable = false, updatable = false)
    var payment: Payment? = null

) : Auditable()
