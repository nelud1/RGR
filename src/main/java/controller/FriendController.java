package controller;

import repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/request")
    public Map<String, Object> sendRequest(@RequestParam int toUserId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        if (userId == toUserId) {
            response.put("success", false);
            response.put("error", "Нельзя добавить себя в друзья");
            return response;
        }

        String status = friendshipRepository.getFriendshipStatus(userId, toUserId);
        if (status != null) {
            if (status.equals("ACCEPTED")) {
                response.put("success", false);
                response.put("error", "Вы уже друзья");
            } else if (status.equals("PENDING")) {
                response.put("success", false);
                response.put("error", "Заявка уже отправлена");
            }
            return response;
        }

        boolean success = friendshipRepository.sendRequest(userId, toUserId);
        if (success) {
            response.put("success", true);
            response.put("message", "Заявка отправлена");
        } else {
            response.put("success", false);
            response.put("error", "Не удалось отправить заявку");
        }
        return response;
    }

    @PostMapping("/accept")
    public Map<String, Object> acceptRequest(@RequestParam int fromUserId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        boolean success = friendshipRepository.acceptRequest(fromUserId, userId);
        if (success) {
            response.put("success", true);
            response.put("message", "Заявка принята");
        } else {
            response.put("success", false);
            response.put("error", "Не удалось принять заявку");
        }
        return response;
    }

    @PostMapping("/remove")
    public Map<String, Object> removeFriend(@RequestParam int friendId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        boolean success = friendshipRepository.removeFriend(userId, friendId);
        if (success) {
            response.put("success", true);
            response.put("message", "Друг удален");
        } else {
            response.put("success", false);
            response.put("error", "Не удалось удалить друга");
        }
        return response;
    }

    @GetMapping("/requests")
    public List<Map<String, Object>> getPendingRequests(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> requests = friendshipRepository.getPendingRequestsForUser(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> req : requests) {
            Map<String, Object> item = new HashMap<>();
            Object fromUserId = req.get("from_user_id");
            item.put("fromUserId", fromUserId);

            String firstName = (String) req.get("first_name");
            String lastName = (String) req.get("last_name");
            String email = (String) req.get("email");

            String name;
            if (firstName != null && !firstName.isEmpty()) {
                name = firstName + " " + (lastName != null ? lastName : "");
            } else {
                name = email;
            }
            if (name == null || name.isEmpty()) {
                name = "Пользователь " + fromUserId;
            }
            item.put("fromName", name);
            result.add(item);
        }
        return result;
    }

    @GetMapping("/list")
    public List<Map<String, Object>> getFriendsWithNames(HttpSession session) {
        List<Map<String, Object>> result = new ArrayList<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return result;
        }

        List<Integer> friendIds = friendshipRepository.getFriendIds(userId);
        for (int friendId : friendIds) {
            Map<String, Object> friend = new HashMap<>();
            friend.put("id", friendId);

            String sql = "SELECT p.first_name, p.last_name, u.email FROM users u LEFT JOIN profiles p ON u.id = p.user_id WHERE u.id = ?";
            Map<String, Object> userInfo = jdbcTemplate.queryForMap(sql, friendId);

            String firstName = (String) userInfo.get("first_name");
            String lastName = (String) userInfo.get("last_name");
            String email = (String) userInfo.get("email");

            String name;
            if (firstName != null && !firstName.isEmpty()) {
                name = firstName + " " + (lastName != null ? lastName : "");
            } else {
                name = email;
            }
            if (name == null || name.isEmpty()) {
                name = "Пользователь " + friendId;
            }
            friend.put("name", name);
            result.add(friend);
        }
        return result;
    }

    @GetMapping("/status")
    public Map<String, String> getStatus(@RequestParam int otherUserId, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("status", "NOT_AUTH");
            return response;
        }

        String status = friendshipRepository.getFriendshipStatus(userId, otherUserId);
        if (status == null) {
            response.put("status", "NONE");
        } else {
            response.put("status", status);
        }
        return response;
    }
}