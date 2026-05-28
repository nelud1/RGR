package validation;

public class NewsValidator {

    public static ValidationErrors validateCreateNews(String title, String content, String accessLevel) {
        ValidationErrors errors = new ValidationErrors();

        if (title == null || title.trim().isEmpty()) {
            errors.addError("Заголовок новости обязателен");
        } else if (title.length() > 30) {
            errors.addError("Заголовок не должен превышать 30 символов");
        }

        if (content == null || content.trim().isEmpty()) {
            errors.addError("Текст новости обязателен");
        } else if (content.length() > 500) {
            errors.addError("Текст новости не должен превышать 500 символов");
        }

        if (accessLevel == null || (!accessLevel.equals("PUBLIC") && !accessLevel.equals("FRIENDS_ONLY"))) {
            errors.addError("Некорректный уровень доступа");
        }

        return errors;
    }

    public static ValidationErrors validateComment(String content) {
        ValidationErrors errors = new ValidationErrors();

        if (content == null || content.trim().isEmpty()) {
            errors.addError("Комментарий не может быть пустым");
        } else if (content.length() > 100) {
            errors.addError("Комментарий не должен превышать 100 символов");
        }

        return errors;
    }
}