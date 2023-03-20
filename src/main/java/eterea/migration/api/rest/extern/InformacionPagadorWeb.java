package eterea.migration.api.rest.extern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformacionPagadorWeb {

    @JsonProperty("Email")
    private String eMail = "";

    @JsonProperty("Nombre")
    private String nombre = "";

    @JsonProperty("NumeroDocumento")
    private String numeroDocumento = "";

    @JsonProperty("Telefono")
    private String telefono = null;

    @JsonProperty("TipoDocumento")
    private String tipoDocumento = "";

}