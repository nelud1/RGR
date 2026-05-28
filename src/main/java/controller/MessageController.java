package controller;

import service.MessageService;
import validation.MessageValidator;
import validation.ValidationErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestParam int receiverId,
                                           @RequestParam(required = false) String subject,
                                           @RequestParam String content,
                                           HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        ValidationErrors errors = MessageValidator.validateSendMessage(content);
        if (errors.hasErrors()) {
            response.put("success", false);
            response.put("error", errors.getFirstError());
            return response;
        }

        messageService.sendMessage(userId, receiverId, content);
        response.put("success", true);
        return response;
    }

    @GetMapping("/dialogs")
    public List<Map<String, Object>> getDialogs(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return List.of();
        }
        return messageService.getDialogs(userId);
    }

    @GetMapping("/dialog/{otherUserId}")
    public List<Map<String, Object>> getDialog(@PathVariable int otherUserId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return List.of();
        }
        return messageService.getDialog(userId, otherUserId);
    }
}