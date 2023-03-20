package eterea.migration.api.rest.kotlin.model

import eterea.migration.api.rest.model.Auditable
import jakarta.persistence.*

@Entity
@Table
data class ProductTransaction(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var productTransactionId: Long? = null,
    var paymentId: Long? = null,
    var nombreProducto: String = "",
    var montoProducto: String? = null,

    @ManyToOne
    @JoinColumn(name = "paymentId", insertable = false, updatable = false)
    var payment: Payment? = null

) : Auditable()
