package eterea.migration.rest.exception;

public class ProductException extends RuntimeException {

    public ProductException(Long orderNumberId, Integer lineId) {
        super("Cannot find Product " + orderNumberId + "/" + lineId);
    }

}
