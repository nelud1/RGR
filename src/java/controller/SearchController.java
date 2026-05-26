package controller;

import model.Profile;
import model.User;
import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/search")
    public List<Map<String, Object>> searchUsers(@RequestParam(required = false) String firstName,
                                                 @RequestParam(required = false) String lastName,
                                                 @RequestParam(required = false) String city,
                                                 HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        List<Map<String, Object>> results = new ArrayList<>();

        List<User> allUsers = userService.getAllUsers();

        for (User user : allUsers) {
            if (user.getId() == currentUserId) {
                continue;
            }

            Profile profile = userService.getProfile(user.getId());
            if (profile == null) {
                continue;
            }

            boolean match = true;
            if (firstName != null && !firstName.isEmpty()) {
                if (!profile.getFirstName().toLowerCase().contains(firstName.toLowerCase())) {
                    match = false;
                }
            }
            if (match && lastName != null && !lastName.isEmpty()) {
                if (!profile.getLastName().toLowerCase().contains(lastName.toLowerCase())) {
                    match = false;
                }
            }
            if (match && city != null && !city.isEmpty()) {
                if (profile.getCity() == null || !profile.getCity().toLowerCase().contains(city.toLowerCase())) {
                    match = false;
                }
            }

            if (match) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", user.getId());
                result.put("firstName", profile.getFirstName());
                result.put("lastName", profile.getLastName());
                result.put("role", user.getRole());
                result.put("city", profile.getCity());
                result.put("birthDate", profile.getBirthDate());
                result.put("email", user.getEmail());
                results.add(result);
            }
        }

        return results;
    }

    @GetMapping("/users/{userId}")
    public Map<String, Object> getUserById(@PathVariable int userId) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findById(userId);
        Profile profile = userService.getProfile(userId);

        if (user == null) {
            response.put("error", "Пользователь не найден");
            return response;
        }

        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        if (profile != null) {
            response.put("firstName", profile.getFirstName());
            response.put("lastName", profile.getLastName());
            response.put("city", profile.getCity());
            response.put("birthDate", profile.getBirthDate());
        }

        return response;
    }
}