package controller;

import dto.UserRegistrationDto;
import model.User;
import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@ModelAttribute UserRegistrationDto dto) {
        Map<String, Object> response = new HashMap<>();
        boolean success = userService.register(dto);
        if (success) {
            response.put("success", true);
            response.put("message", "Регистрация успешна. Проверьте email для подтверждения.");
        } else {
            response.put("success", false);
            response.put("error", "Ошибка регистрации. Email уже существует или пароли не совпадают.");
        }
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.login(email, password);
        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());
            response.put("success", true);
            response.put("role", user.getRole());
        } else {
            response.put("success", false);
            response.put("error", "Неверный email, пароль или аккаунт не активирован");
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