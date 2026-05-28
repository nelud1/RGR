package repository;

import model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MessageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(Message message) {
        String sql = "INSERT INTO messages (from_user_id, to_user_id, subject, body, is_read) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, message.getFromUserId(), message.getToUserId(), message.getSubject(), message.getBody(), message.isRead());
    }

    public List<Message> findByToUserId(int toUserId) {
        String sql = "SELECT * FROM messages WHERE to_user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new MessageRowMapper(), toUserId);
    }

    public Message findById(int id) {
        String sql = "SELECT * FROM messages WHERE id = ?";
        List<Message> messages = jdbcTemplate.query(sql, new MessageRowMapper(), id);
        return messages.isEmpty() ? null : messages.get(0);
    }

    public void markAsRead(int id) {
        String sql = "UPDATE messages SET is_read = true WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
            Message message = new Message();
            message.setId(rs.getInt("id"));
            message.setFromUserId(rs.getInt("from_user_id"));
            message.setToUserId(rs.getInt("to_user_id"));
            message.setSubject(rs.getString("subject"));
            message.setBody(rs.getString("body"));
            message.setRead(rs.getBoolean("is_read"));
            message.setCreatedAt(rs.getString("created_at"));
            return message;
        }
    }
}