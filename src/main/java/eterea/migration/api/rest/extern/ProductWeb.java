package eterea.migration.api.rest.extern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWeb {

    private String sku = "";

    @JsonProperty("line_id")
    private Integer lineId = 0;

    private String name = "";
    private String qty = "";

    @JsonProperty("item_price")
    private BigDecimal itemPrice = BigDecimal.ZERO;

    @JsonProperty("booking_start")
    private String bookingStart = "";

    @JsonProperty("booking_end")
    private String bookingEnd = "";

    @JsonProperty("booking_duration")
    private Integer bookingDuration = 0;

    @JsonProperty("booking_persons")
    private Integer bookingPersons = 0;

    @JsonProperty("person_types")
    private String personTypes = "";

    @JsonProperty("servicios-adicionales")
    private String serviciosAdicionales = "";

    @JsonProperty("punto-de-encuentro")
    private String puntoDeEncuentro = "";

    @JsonProperty("encuentro-hotel")
    private String encuentroHotel = "";

}
