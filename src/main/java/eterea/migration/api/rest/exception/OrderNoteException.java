package eterea.migration.api.rest.exception;

public class OrderNoteException extends RuntimeException {

    public OrderNoteException(Long orderNumberId) {
        super("Cannot find OrderNote " + orderNumberId);
    }

}
