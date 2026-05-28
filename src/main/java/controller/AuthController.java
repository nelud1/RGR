package controller;

import dto.UserRegistrationDto;
import model.User;
import service.UserService;
import validation.UserValidator;
import validation.ValidationErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    private Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private Map<String, Long> lockoutTime = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public Map<String, Object> register(@ModelAttribute UserRegistrationDto dto) {
        Map<String, Object> response = new HashMap<>();

        User existing = userService.findByEmail(dto.getEmail());
        ValidationErrors errors = UserValidator.validateRegistration(dto, existing);

        if (errors.hasErrors()) {
            response.put("success", false);
            response.put("error", errors.getFirstError());
            return response;
        }

        boolean success = userService.register(dto);
        if (success) {
            response.put("success", true);
            response.put("message", "Регистрация успешна. Проверьте email для подтверждения.");
        } else {
            response.put("success", false);
            response.put("error", "Ошибка регистрации");
        }
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        ValidationErrors errors = UserValidator.validateLogin(email, password);
        if (errors.hasErrors()) {
            response.put("success", false);
            response.put("error", errors.getFirstError());
            return response;
        }

        String key = email.toLowerCase();

        if (lockoutTime.containsKey(key)) {
            long lockUntil = lockoutTime.get(key);
            if (System.currentTimeMillis() < lockUntil) {
                response.put("success", false);
                response.put("error", "Аккаунт заблокирован на 15 минут.");
                return response;
            } else {
                lockoutTime.remove(key);
                loginAttempts.remove(key);
            }
        }

        User user = userService.login(email, password);
        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());
            loginAttempts.remove(key);
            lockoutTime.remove(key);
            response.put("success", true);
            response.put("role", user.getRole());
        } else {
            int attempts = loginAttempts.getOrDefault(key, 0) + 1;
            loginAttempts.put(key, attempts);
            if (attempts >= 5) {
                lockoutTime.put(key, System.currentTimeMillis() + 15 * 60 * 1000);
                response.put("error", "Аккаунт заблокирован на 15 минут");
            } else {
                response.put("error", "Неверный email или пароль. Осталось попыток: " + (5 - attempts));
            }
            response.put("success", false);
        }
        return response;
    }

    @GetMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            User user = userService.findById(userId);
            response.put("authenticated", true);
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
        } else {
            response.put("authenticated", false);
        }
        return response;
    }

    @GetMapping("/activate")
    public String activate(@RequestParam String token) {
        boolean success = userService.activateAccount(token);
        if (success) {
            return "<html><body><h2>Аккаунт активирован!</h2><p>Теперь вы можете войти в систему.</p><a href='/login.html'>Перейти на страницу входа</a></body></html>";
        } else {
            return "<html><body><h2>Ошибка активации</h2><p>Неверный или просроченный токен активации.</p><a href='/login.html'>Вернуться на страницу входа</a></body></html>";
        }
    }
}