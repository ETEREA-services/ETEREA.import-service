package eterea.migration.api.rest.extern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderNoteWeb {

    @JsonProperty("order_number")
    private String orderNumber = "";

    @JsonProperty("order_status")
    private String orderStatus = "";

    @JsonProperty("order_date")
    private String orderDate = "";

    @JsonProperty("paid_date")
    private String paidDate = "";

    @JsonProperty("completed_date")
    private String completedDate = "";

    @JsonProperty("modified_date")
    private String modifiedDate = "";

    @JsonProperty("order_currency")
    private String orderCurrency = "";

    @JsonProperty("customer_note")
    private String customerNote = "";

    @JsonProperty("billing_first_name")
    private String billingFirstName = "";

    @JsonProperty("billing_last_name")
    private String billingLastName = "";

    @JsonProperty("billing_full_name")
    private String billingFullName = "";

    @JsonProperty("_billing_dni_o_pasaporte")
    private String billingDniPasaporte = "";

    @JsonProperty("billing_address")
    private String billingAddress = "";

    @JsonProperty("billing_city")
    private String billingCity = "";

    @JsonProperty("billing_state")
    private String billingState = "";

    @JsonProperty("billing_postcode")
    private String billingPostCode = "";

    @JsonProperty("billing_country")
    private String billingCountry = "";

    @JsonProperty("billing_email")
    private String billingEmail = "";

    @JsonProperty("billing_phone")
    private String billingPhone = "";

    @JsonProperty("shipping_first_name")
    private String shippingFirstName = "";

    @JsonProperty("shipping_last_name")
    private String shippingLastName = "";

    @JsonProperty("shipping_full_name")
    private String shippingFullName = "";

    @JsonProperty("shipping_address")
    private String shippingAddress = "";

    @JsonProperty("shipping_city")
    private String shippingCity = "";

    @JsonProperty("shipping_state")
    private String shippingState = "";

    @JsonProperty("shipping_postcode")
    private String shippingPostCode = "";

    @JsonProperty("shipping_country_full")
    private String shippingCountryFull = "";

    @JsonProperty("payment_method_title")
    private String paymentMethodTitle = "";

    @JsonProperty("cart_discount")
    private String cartDiscount = "";

    @JsonProperty("order_subtotal")
    private BigDecimal orderSubtotal = BigDecimal.ZERO;

    @JsonProperty("order_subtotal_refunded")
    private BigDecimal orderSubtotalRefunded = BigDecimal.ZERO;

    @JsonProperty("shipping_method_title")
    private String shippingMethodTitle = "";

    @JsonProperty("order_shipping")
    private String orderShipping = "";

    @JsonProperty("order_shipping_refunded")
    private BigDecimal orderShippingRefunded = BigDecimal.ZERO;

    @JsonProperty("order_total")
    private BigDecimal orderTotal = BigDecimal.ZERO;

    @JsonProperty("order_total_tax")
    private BigDecimal orderTotalTax = BigDecimal.ZERO;

    private List<ProductWeb> products = null;

    @JsonProperty("order_notes")
    private String orderNotes = "";

    private PaymentWeb payment;

}