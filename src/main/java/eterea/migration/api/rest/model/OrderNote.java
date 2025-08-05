package eterea.migration.api.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderNote extends Auditable {

    @Id
    private Long orderNumberId;
    private String orderStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime orderDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime paidDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime completedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime modifiedDate;

    private String orderCurrency;

    @Lob
    private String customerNote;

    private String billingFirstName;
    private String billingLastName;
    private String billingFullName;
    private String billingDniPasaporte;
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingPostCode;
    private String billingCountry;
    private String billingEmail;
    private String billingPhone;
    private String shippingFirstName;
    private String shippingLastName;
    private String shippingFullName;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPostCode;
    private String shippingCountryFull;
    private String paymentMethodTitle;
    private String cartDiscount;
    private BigDecimal orderSubtotal;
    private BigDecimal orderSubtotalRefunded;
    private String shippingMethodTitle;
    private String orderShipping;
    private BigDecimal orderShippingRefunded;
    private BigDecimal orderTotal;
    private BigDecimal orderTotalTax;

    @Lob
    private String orderNotes;

    @Lob
    private String fullPayload;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    private List<Product> products;

    @OneToOne
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    private Payment payment;

    public String jsonify() {
        try {
            return JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "jsonify error " + e.getMessage();
        }
    }

}
