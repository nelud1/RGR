package repository;

import model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class NewsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(News news) {
        String sql = "INSERT INTO news (author_id, title, content, image_path, external_link, visibility) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, news.getAuthorId(), news.getTitle(), news.getContent(), news.getImagePath(), news.getExternalLink(), news.getVisibility());
    }

    public News findById(int id) {
        String sql = "SELECT * FROM news WHERE id = ?";
        List<News> news = jdbcTemplate.query(sql, new NewsRowMapper(), id);
        return news.isEmpty() ? null : news.get(0);
    }

    public News findLastByAuthorId(int authorId) {
        String sql = "SELECT * FROM news WHERE author_id = ? ORDER BY id DESC LIMIT 1";
        List<News> news = jdbcTemplate.query(sql, new NewsRowMapper(), authorId);
        return news.isEmpty() ? null : news.get(0);
    }

    public void updateImage(int newsId, String imagePath) {
        String sql = "UPDATE news SET image_path = ? WHERE id = ?";
        jdbcTemplate.update(sql, imagePath, newsId);
    }

    public List<News> findFeedForUser(int userId, List<Integer> friendIds, boolean isModerator) {
        String sql;
        if (isModerator) {
            sql = "SELECT * FROM news ORDER BY created_at DESC";
            return jdbcTemplate.query(sql, new NewsRowMapper());
        } else {
            if (friendIds.isEmpty()) {
                sql = "SELECT * FROM news WHERE visibility = 'PUBLIC' OR author_id = ? ORDER BY created_at DESC";
                return jdbcTemplate.query(sql, new NewsRowMapper(), userId);
            } else {
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < friendIds.size(); i++) {
                    if (i > 0) placeholders.append(",");
                    placeholders.append("?");
                }
                sql = "SELECT * FROM news WHERE (visibility = 'PUBLIC' OR author_id = ? OR (visibility = 'FRIENDS_ONLY' AND author_id IN (" + placeholders.toString() + "))) ORDER BY created_at DESC";
                Object[] params = new Object[1 + friendIds.size()];
                params[0] = userId;
                for (int i = 0; i < friendIds.size(); i++) {
                    params[i + 1] = friendIds.get(i);
                }
                return jdbcTemplate.query(sql, new NewsRowMapper(), params);
            }
        }
    }

    public List<News> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM news WHERE LOWER(title) LIKE LOWER(?) OR LOWER(content) LIKE LOWER(?) ORDER BY created_at DESC";
        String searchPattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, new NewsRowMapper(), searchPattern, searchPattern);
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM news WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class NewsRowMapper implements RowMapper<News> {
        @Override
        public News mapRow(ResultSet rs, int rowNum) throws SQLException {
            News news = new News();
            news.setId(rs.getInt("id"));
            news.setAuthorId(rs.getInt("author_id"));
            news.setTitle(rs.getString("title"));
            news.setContent(rs.getString("content"));
            news.setImagePath(rs.getString("image_path"));
            news.setExternalLink(rs.getString("external_link"));
            news.setVisibility(rs.getString("visibility"));
            news.setCreatedAt(rs.getString("created_at"));
            return news;
        }
    }
}