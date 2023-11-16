package eterea.migration.api.rest.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import eterea.migration.api.rest.model.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table
data class OrderNote(

    @Id
    var orderNumberId: Long? = null,
    var orderStatus: String = "",

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var orderDate: OffsetDateTime? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var paidDate: OffsetDateTime? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var completedDate: OffsetDateTime? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
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

    @Lob
    @Column(columnDefinition = "TEXT")
    var orderNotes: String = ""

) : Auditable()
