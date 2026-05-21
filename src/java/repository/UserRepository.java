package repository;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(User user) {
        String sql = "INSERT INTO users (email, password_hash, role, active, activation_token) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getPasswordHash(), user.getRole(), user.isActive(), user.getActivationToken());
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
        } catch (Exception e) {
            return null;
        }
    }

    public User findByActivationToken(String token) {
        String sql = "SELECT * FROM users WHERE activation_token = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), token);
        } catch (Exception e) {
            return null;
        }
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public void update(User user) {
        String sql = "UPDATE users SET email = ?, password_hash = ?, role = ?, active = ?, activation_token = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getPasswordHash(), user.getRole(), user.isActive(), user.getActivationToken(), user.getId());
    }

    public void updateRole(int userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        jdbcTemplate.update(sql, role, userId);
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setRole(rs.getString("role"));
            user.setActive(rs.getBoolean("active"));
            user.setActivationToken(rs.getString("activation_token"));
            user.setRegistrationDate(rs.getString("registration_date"));
            return user;
        }
    }
}