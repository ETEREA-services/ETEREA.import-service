package eterea.migration.api.rest.extern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransactionWeb {

    @JsonProperty("NombreProducto")
    private String nombreProducto = "";

    @JsonProperty("MontoProducto")
    private String montoProducto = null;

}
