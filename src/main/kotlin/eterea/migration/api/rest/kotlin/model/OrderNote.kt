package eterea.migration.api.rest.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
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

    @Column(columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
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
    @Column(columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    var orderNotes: String = "",

    @OneToMany
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    var products: List<Product?>? = null,

    @OneToOne
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    var payment: Payment? = null

) : Auditable()
