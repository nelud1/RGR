package controller;

import model.Profile;
import model.User;
import service.UserService;
import service.FileStorageService;
import validation.ProfileValidator;
import validation.ValidationErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public Map<String, Object> getProfile(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("error", "Не авторизован");
            return response;
        }

        User user = userService.findById(userId);
        Profile profile = userService.getProfile(userId);

        response.put("userId", userId);
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        if (profile != null) {
            response.put("firstName", profile.getFirstName());
            response.put("lastName", profile.getLastName());
            response.put("birthDate", profile.getBirthDate());
            response.put("gender", profile.getGender());
            response.put("city", profile.getCity());
            response.put("about", profile.getAboutMe());
            response.put("avatarPath", profile.getAvatarPath());
        }

        return response;
    }

    @PostMapping("/update")
    public Map<String, Object> updateProfile(@RequestParam(required = false) String firstName,
                                             @RequestParam(required = false) String lastName,
                                             @RequestParam(required = false) String birthDate,
                                             @RequestParam(required = false) String gender,
                                             @RequestParam(required = false) String city,
                                             @RequestParam(required = false) String about,
                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        Profile profile = userService.getProfile(userId);
        if (profile == null) {
            profile = new Profile();
            profile.setUserId(userId);
        }

        if (firstName != null) profile.setFirstName(firstName);
        if (lastName != null) profile.setLastName(lastName);
        if (birthDate != null && !birthDate.isEmpty()) profile.setBirthDate(birthDate);
        if (gender != null) profile.setGender(gender);
        if (city != null) profile.setCity(city);
        if (about != null) profile.setAboutMe(about);

        userService.updateProfile(profile);

        response.put("success", true);
        return response;
    }

    @PostMapping("/upload-avatar")
    public Map<String, Object> uploadAvatar(@RequestParam("avatar") MultipartFile avatar, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        ValidationErrors errors = ProfileValidator.validateFile(avatar);
        if (errors.hasErrors()) {
            response.put("success", false);
            response.put("error", errors.getFirstError());
            return response;
        }

        String avatarPath = fileStorageService.saveAvatar(userId, avatar);

        Profile profile = userService.getProfile(userId);
        if (profile == null) {
            profile = new Profile();
            profile.setUserId(userId);
        }
        profile.setAvatarPath(avatarPath);
        userService.updateProfile(profile);

        response.put("success", true);
        response.put("avatarPath", avatarPath);
        return response;
    }
}