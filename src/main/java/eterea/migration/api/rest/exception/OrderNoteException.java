package eterea.migration.api.rest.exception;

import java.math.BigDecimal;

public class OrderNoteException extends RuntimeException {

    public OrderNoteException(Long orderNumberId) {
        super("Cannot find OrderNote " + orderNumberId);
    }

    public OrderNoteException(Long numeroDocumento, BigDecimal importe) {
        super("Cannot find OrderNote by numeroDocumento " + numeroDocumento + " and importe " + importe);
    }

}
