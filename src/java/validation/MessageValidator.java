package validation;

public class MessageValidator {

    public static ValidationErrors validateSendMessage(String content) {
        ValidationErrors errors = new ValidationErrors();

        if (content == null || content.trim().isEmpty()) {
            errors.addError("Сообщение не может быть пустым");
        } else if (content.length() > 500) {
            errors.addError("Сообщение не должно превышать 500 символов");
        }

        return errors;
    }
}