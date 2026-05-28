package controller;

import service.UserService;
import service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;

    @PostMapping("/make-moderator")
    public Map<String, Object> makeModerator(@RequestParam int userId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String userRole = (String) session.getAttribute("role");

        if (!"ADMIN".equals(userRole)) {
            response.put("success", false);
            response.put("error", "Доступ запрещен");
            return response;
        }

        userService.updateRole(userId, "MODERATOR");
        response.put("success", true);
        return response;
    }

    @PostMapping("/remove-moderator")
    public Map<String, Object> removeModerator(@RequestParam int userId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String userRole = (String) session.getAttribute("role");

        if (!"ADMIN".equals(userRole)) {
            response.put("success", false);
            response.put("error", "Доступ запрещен");
            return response;
        }

        userService.updateRole(userId, "USER");
        response.put("success", true);
        return response;
    }

    @PostMapping("/delete-user")
    public Map<String, Object> deleteUser(@RequestParam int userId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String userRole = (String) session.getAttribute("role");

        if (!"ADMIN".equals(userRole)) {
            response.put("success", false);
            response.put("error", "Доступ запрещен");
            return response;
        }

        userService.deleteUser(userId);
        response.put("success", true);
        return response;
    }
}