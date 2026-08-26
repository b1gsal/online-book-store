package mate.academy.onlinebookstore.exception;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException(String message) {
        super(message);
    }
}
