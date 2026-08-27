package mate.academy.onlinebookstore.exception;

public class EmptyShoppingCartException extends RuntimeException {
    public EmptyShoppingCartException(String message) {
        super(message);
    }
}
