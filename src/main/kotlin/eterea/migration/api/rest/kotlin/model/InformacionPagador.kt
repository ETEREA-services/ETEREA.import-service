package eterea.migration.api.rest.kotlin.model

import jakarta.persistence.*

@Entity
@Table
data class InformacionPagador(

    @Id
    var orderNumberId: Long? = null,
    var eMail: String? = "",
    var nombre: String = "",
    var numeroDocumento: String = "",
    var telefono: String? = null,
    var tipoDocumento: String = "",

    @OneToOne(optional = true)
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    var payment: Payment? = null

) : Auditable()
