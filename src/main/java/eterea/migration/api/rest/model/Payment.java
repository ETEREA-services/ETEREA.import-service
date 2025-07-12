package eterea.migration.api.rest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Payment extends Auditable {

    @Id
    private Long orderNumberId;
    private String transaccionComercioId;
    private String transaccionPlataformaId;
    private String tipo;
    private BigDecimal monto;
    private String estado;

    @Lob
    private String detalle;
    private String metodoPago;
    private String medioPago;
    private Integer estadoId;
    private Integer cuotas;
    private String informacionAdicional;
    private String marcaTarjeta;
    private String informacionAdicionalLink;
    private OffsetDateTime fechaTransaccion;
    private OffsetDateTime fechaPago;

    @OneToOne
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    private InformacionPagador informacionPagador;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "orderNumberId", insertable = false, updatable = false)
    private List<ProductTransaction> productTransactions;
}
