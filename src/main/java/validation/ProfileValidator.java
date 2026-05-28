package validation;

import org.springframework.web.multipart.MultipartFile;

public class ProfileValidator {

    public static ValidationErrors validateFile(MultipartFile file) {
        ValidationErrors errors = new ValidationErrors();

        if (file == null || file.isEmpty()) {
            errors.addError("Файл не выбран");
            return errors;
        }

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            errors.addError("Только JPG и PNG");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            errors.addError("Файл не должен превышать 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\"))) {
            errors.addError("Недопустимое имя файла");
        }

        return errors;
    }
}