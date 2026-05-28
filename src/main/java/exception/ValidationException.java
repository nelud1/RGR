package exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private List<String> errors;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(List<String> errors) {
        super(errors != null && !errors.isEmpty() ? errors.get(0) : "Ошибка валидации");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}