package controller;

import model.Profile;
import model.User;
import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private UserService userService;

    private int calculateAge(String birthDate) {
        if (birthDate == null || birthDate.isEmpty()) {
            return -1;
        }
        try {
            LocalDate birth = LocalDate.parse(birthDate);
            LocalDate now = LocalDate.now();
            int age = Period.between(birth, now).getYears();
            return age < 0 ? -1 : age;
        } catch (Exception e) {
            return -1;
        }
    }

    @GetMapping("/users/search")
    public List<Map<String, Object>> searchUsers(@RequestParam(required = false) String firstName,
                                                 @RequestParam(required = false) String lastName,
                                                 @RequestParam(required = false) String city,
                                                 @RequestParam(required = false) Integer ageFrom,
                                                 @RequestParam(required = false) Integer ageTo,
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
                String dbFirstName = profile.getFirstName() != null ? profile.getFirstName().toLowerCase() : "";
                if (!dbFirstName.contains(firstName.toLowerCase())) {
                    match = false;
                }
            }
            if (match && lastName != null && !lastName.isEmpty()) {
                String dbLastName = profile.getLastName() != null ? profile.getLastName().toLowerCase() : "";
                if (!dbLastName.contains(lastName.toLowerCase())) {
                    match = false;
                }
            }
            if (match && city != null && !city.isEmpty()) {
                String dbCity = profile.getCity() != null ? profile.getCity().toLowerCase() : "";
                if (!dbCity.contains(city.toLowerCase())) {
                    match = false;
                }
            }
            if (match && (ageFrom != null || ageTo != null)) {
                int age = calculateAge(profile.getBirthDate());
                if (ageFrom != null && age < ageFrom) {
                    match = false;
                }
                if (match && ageTo != null && age > ageTo) {
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

                int age = calculateAge(profile.getBirthDate());
                result.put("age", age > 0 ? age : null);

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