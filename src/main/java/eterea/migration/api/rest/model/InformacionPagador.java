package eterea.migration.api.rest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class InformacionPagador extends Auditable {

    @Id
    private Long orderNumberId;
    private String eMail;
    private String nombre;
    private String numeroDocumento;
    private String telefono;
    private String tipoDocumento;

}
