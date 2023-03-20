package eterea.migration.api.rest.kotlin.model

import eterea.migration.api.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table
data class OrderNote(

    @Id
    var orderNumberId: Long? = null,
    var orderStatus: String = "",
    var orderDate: OffsetDateTime? = null,
    var paidDate: OffsetDateTime? = null,
    var completedDate: OffsetDateTime? = null,
    var modifiedDate: OffsetDateTime? = null,
    var orderCurrency: String = "",
    var customerNote: String = "",
    var billingFirstName: String = "",
    var billingLastName: String = "",
    var billingFullName: String = "",
    var billingDniPasaporte: String = "",
    var billingAddress: String = "",
    var billingCity: String = "",
    var billingState: String = "",
    var billingPostCode: String = "",
    var billingCountry: String = "",
    var billingEmail: String = "",
    var billingPhone: String = "",
    var shippingFirstName: String = "",
    var shippingLastName: String = "",
    var shippingFullName: String = "",
    var shippingAddress: String = "",
    var shippingCity: String = "",
    var shippingState: String = "",
    var shippingPostCode: String = "",
    var shippingCountryFull: String = "",
    var paymentMethodTitle: String = "",
    var cartDiscount: String = "",
    var orderSubtotal: BigDecimal = BigDecimal.ZERO,
    var orderSubtotalRefunded: BigDecimal = BigDecimal.ZERO,
    var shippingMethodTitle: String = "",
    var orderShipping: String = "",
    var orderShippingRefunded: BigDecimal = BigDecimal.ZERO,
    var orderTotal: BigDecimal = BigDecimal.ZERO,
    var orderTotalTax: BigDecimal = BigDecimal.ZERO,
    var orderNotes: String = ""

) : Auditable()
