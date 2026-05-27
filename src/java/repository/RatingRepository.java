package repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RatingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer getUserRating(int newsId, int userId) {
        String sql = "SELECT rating FROM news_ratings WHERE news_id = ? AND user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, newsId, userId);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveOrUpdate(int newsId, int userId, int rating) {
        Integer existing = getUserRating(newsId, userId);
        if (existing == null) {
            String sql = "INSERT INTO news_ratings (news_id, user_id, rating) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, newsId, userId, rating);
        } else if (existing == rating) {
            String sql = "DELETE FROM news_ratings WHERE news_id = ? AND user_id = ?";
            jdbcTemplate.update(sql, newsId, userId);
        } else {
            String sql = "UPDATE news_ratings SET rating = ? WHERE news_id = ? AND user_id = ?";
            jdbcTemplate.update(sql, rating, newsId, userId);
        }
    }

    public int getLikesCount(int newsId) {
        String sql = "SELECT COUNT(*) FROM news_ratings WHERE news_id = ? AND rating = 1";
        return jdbcTemplate.queryForObject(sql, Integer.class, newsId);
    }

    public int getDislikesCount(int newsId) {
        String sql = "SELECT COUNT(*) FROM news_ratings WHERE news_id = ? AND rating = -1";
        return jdbcTemplate.queryForObject(sql, Integer.class, newsId);
    }

    public void deleteByNewsId(int newsId) {
        String sql = "DELETE FROM news_ratings WHERE news_id = ?";
        jdbcTemplate.update(sql, newsId);
    }
}