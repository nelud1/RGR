package validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrors {
    private List<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
}