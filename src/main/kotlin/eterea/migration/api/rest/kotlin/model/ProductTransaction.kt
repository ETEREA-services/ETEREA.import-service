package eterea.migration.api.rest.kotlin.model

import jakarta.persistence.*

@Entity
@Table
data class ProductTransaction(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var productTransactionId: Long? = null,
    var orderNumberId: Long? = null,
    var nombreProducto: String = "",
    var montoProducto: String? = null,

    @ManyToOne
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    var payment: Payment? = null

) : Auditable()
