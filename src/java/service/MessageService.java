package service;

import model.Message;
import model.Profile;
import repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void sendMessage(int fromUserId, int toUserId, String content) {
        Message message = new Message();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setSubject("");
        message.setBody(content);
        message.setRead(false);

        messageRepository.save(message);

        Profile fromProfile = userService.getProfile(fromUserId);
        String fromName = fromProfile != null ? fromProfile.getFirstName() + " " + fromProfile.getLastName() : "Пользователь";

        String toEmail = userService.findById(toUserId).getEmail();

        emailService.sendMessageNotification(toEmail, fromName, "Новое сообщение", content);

        System.out.println("Сообщение отправлено от " + fromUserId + " к " + toUserId);
    }

    public List<Map<String, Object>> getDialogs(int userId) {
        String sql = "SELECT DISTINCT " +
                "CASE WHEN from_user_id = ? THEN to_user_id ELSE from_user_id END as other_user_id " +
                "FROM messages WHERE from_user_id = ? OR to_user_id = ?";

        List<Integer> otherUserIds = jdbcTemplate.queryForList(sql, Integer.class, userId, userId, userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (int otherUserId : otherUserIds) {
            Map<String, Object> dialog = new HashMap<>();
            dialog.put("userId", otherUserId);

            Profile profile = userService.getProfile(otherUserId);
            String name;
            if (profile != null && profile.getFirstName() != null) {
                name = profile.getFirstName() + " " + profile.getLastName();
            } else {
                name = userService.findById(otherUserId).getEmail();
            }
            dialog.put("userName", name);
            dialog.put("avatarPath", profile != null ? profile.getAvatarPath() : null);

            String lastMsgSql = "SELECT body, created_at FROM messages WHERE (from_user_id = ? AND to_user_id = ?) OR (from_user_id = ? AND to_user_id = ?) ORDER BY created_at DESC LIMIT 1";
            List<Map<String, Object>> lastMsg = jdbcTemplate.queryForList(lastMsgSql, userId, otherUserId, otherUserId, userId);
            if (!lastMsg.isEmpty()) {
                dialog.put("lastMessage", lastMsg.get(0).get("body"));
                dialog.put("lastMessageDate", lastMsg.get(0).get("created_at").toString());
            } else {
                dialog.put("lastMessage", "");
                dialog.put("lastMessageDate", "");
            }

            String unreadSql = "SELECT COUNT(*) FROM messages WHERE to_user_id = ? AND from_user_id = ? AND is_read = false";
            Integer unread = jdbcTemplate.queryForObject(unreadSql, Integer.class, userId, otherUserId);
            dialog.put("unreadCount", unread != null ? unread : 0);

            result.add(dialog);
        }

        result.sort((a, b) -> {
            String dateA = (String) a.get("lastMessageDate");
            String dateB = (String) b.get("lastMessageDate");
            if (dateA == null || dateA.isEmpty()) return 1;
            if (dateB == null || dateB.isEmpty()) return -1;
            return dateB.compareTo(dateA);
        });

        return result;
    }

    public List<Map<String, Object>> getDialog(int userId, int otherUserId) {
        String sql = "SELECT * FROM messages WHERE (from_user_id = ? AND to_user_id = ?) OR (from_user_id = ? AND to_user_id = ?) ORDER BY created_at ASC";
        List<Message> messages = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Message msg = new Message();
            msg.setId(rs.getInt("id"));
            msg.setFromUserId(rs.getInt("from_user_id"));
            msg.setToUserId(rs.getInt("to_user_id"));
            msg.setBody(rs.getString("body"));
            msg.setRead(rs.getBoolean("is_read"));
            msg.setCreatedAt(rs.getString("created_at"));
            return msg;
        }, userId, otherUserId, otherUserId, userId);

        for (Message msg : messages) {
            if (msg.getToUserId() == userId && !msg.isRead()) {
                messageRepository.markAsRead(msg.getId());
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Message msg : messages) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", msg.getId());
            item.put("body", msg.getBody());
            item.put("createdAt", msg.getCreatedAt());
            item.put("isMine", msg.getFromUserId() == userId);
            result.add(item);
        }
        return result;
    }
}