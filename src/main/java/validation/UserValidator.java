package validation;

import dto.UserRegistrationDto;
import model.User;

public class UserValidator {

    public static ValidationErrors validateRegistration(UserRegistrationDto dto, User existingUser) {
        ValidationErrors errors = new ValidationErrors();

        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            errors.addError("Имя обязательно для заполнения");
        }

        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            errors.addError("Фамилия обязательна для заполнения");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.addError("Email обязателен для заполнения");
        } else if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.addError("Некорректный формат email");
        }

        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            errors.addError("Пароль должен содержать не менее 6 символов");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            errors.addError("Пароли не совпадают");
        }

        if (existingUser != null) {
            errors.addError("Пользователь с таким email уже существует");
        }

        return errors;
    }

    public static ValidationErrors validateLogin(String email, String password) {
        ValidationErrors errors = new ValidationErrors();

        if (email == null || email.trim().isEmpty()) {
            errors.addError("Email обязателен для заполнения");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.addError("Пароль обязателен для заполнения");
        }

        return errors;
    }
}