package repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class FriendshipRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean sendRequest(int fromUserId, int toUserId) {
        if (fromUserId == toUserId) {
            return false;
        }

        int user1 = Math.min(fromUserId, toUserId);
        int user2 = Math.max(fromUserId, toUserId);

        String checkSql = "SELECT COUNT(*) FROM friendships WHERE user_id_1 = ? AND user_id_2 = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, user1, user2);

        if (count != null && count > 0) {
            return false;
        }

        String sql = "INSERT INTO friendships (user_id_1, user_id_2, status) VALUES (?, ?, 'PENDING')";
        jdbcTemplate.update(sql, user1, user2);
        return true;
    }

    public boolean acceptRequest(int fromUserId, int toUserId) {
        String sql = "UPDATE friendships SET status = 'ACCEPTED' WHERE user_id_1 = ? AND user_id_2 = ? AND status = 'PENDING'";
        int updated = jdbcTemplate.update(sql, fromUserId, toUserId);
        return updated > 0;
    }

    public boolean removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendships WHERE (user_id_1 = ? AND user_id_2 = ?) OR (user_id_1 = ? AND user_id_2 = ?)";
        int deleted = jdbcTemplate.update(sql, userId, friendId, friendId, userId);
        return deleted > 0;
    }

    public boolean areFriends(int userId1, int userId2) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE ((user_id_1 = ? AND user_id_2 = ?) OR (user_id_1 = ? AND user_id_2 = ?)) AND status = 'ACCEPTED'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId1, userId2, userId2, userId1);
        return count != null && count > 0;
    }

    public List<Integer> getFriendIds(int userId) {
        String sql = "SELECT user_id_2 FROM friendships WHERE user_id_1 = ? AND status = 'ACCEPTED' " +
                "UNION SELECT user_id_1 FROM friendships WHERE user_id_2 = ? AND status = 'ACCEPTED'";
        return jdbcTemplate.queryForList(sql, Integer.class, userId, userId);
    }

    public String getFriendshipStatus(int userId1, int userId2) {
        String sql = "SELECT status FROM friendships WHERE (user_id_1 = ? AND user_id_2 = ?) OR (user_id_1 = ? AND user_id_2 = ?)";
        List<String> statuses = jdbcTemplate.queryForList(sql, String.class, userId1, userId2, userId2, userId1);
        return statuses.isEmpty() ? null : statuses.get(0);
    }

    public List<Map<String, Object>> getPendingRequestsForUser(int userId) {
        String sql = "SELECT f.user_id_1 as from_user_id, u.email, p.first_name, p.last_name " +
                "FROM friendships f " +
                "JOIN users u ON f.user_id_1 = u.id " +
                "LEFT JOIN profiles p ON f.user_id_1 = p.user_id " +
                "WHERE f.user_id_2 = ? AND f.status = 'PENDING'";
        return jdbcTemplate.queryForList(sql, userId);
    }
}