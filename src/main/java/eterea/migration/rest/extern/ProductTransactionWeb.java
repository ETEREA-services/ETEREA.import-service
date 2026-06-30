package eterea.migration.rest.extern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductTransactionWeb {

    @JsonProperty("NombreProducto")
    private String nombreProducto = "";

    @JsonProperty("MontoProducto")
    private String montoProducto = null;

}
