package eterea.migration.api.rest.extern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWeb {

    @JsonProperty("TransaccionComercioId")
    private String transaccionComercioId = "";

    @JsonProperty("TransaccionPlataformaId")
    private String transaccionPlataformaId = "";

    @JsonProperty("Tipo")
    private String tipo = "";

    @JsonProperty("Monto")
    private String monto = "";

    @JsonProperty("Estado")
    private String estado = "";

    @JsonProperty("Detalle")
    private String detalle = "";

    @JsonProperty("MetodoPago")
    private String metodoPago = null;

    @JsonProperty("MedioPago")
    private String medioPago = "";

    @JsonProperty("EstadoId")
    private String estadoId = "";

    @JsonProperty("Cuotas")
    private String cuotas = "";

    @JsonProperty("InformacionPagador")
    private InformacionPagadorWeb informacionPagador;

    @JsonProperty("InformacionAdicional")
    private String informacionAdicional = null;

    @JsonProperty("MarcaTarjeta")
    private String marcaTarjeta = "";

    @JsonProperty("InformacionAdicionalLink")
    private String informacionAdicionalLink = null;

    @JsonProperty("FechaTransaccion")
    private String fechaTransaccion = "";

    @JsonProperty("FechaPago")
    private String fechaPago = "";

    @JsonProperty("ProductoTransaccion")
    private List<ProductTransactionWeb> productoTransactions;

}
