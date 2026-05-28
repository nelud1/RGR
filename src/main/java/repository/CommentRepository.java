package repository;

import model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(Comment comment) {
        String sql = "INSERT INTO comments (news_id, author_id, content) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, comment.getNewsId(), comment.getAuthorId(), comment.getContent());
    }

    public List<Comment> findByNewsId(int newsId) {
        String sql = "SELECT * FROM comments WHERE news_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, new CommentRowMapper(), newsId);
    }

    public void deleteByNewsId(int newsId) {
        String sql = "DELETE FROM comments WHERE news_id = ?";
        jdbcTemplate.update(sql, newsId);
    }

    public void deleteByAuthorId(int authorId) {
        String sql = "DELETE FROM comments WHERE author_id = ?";
        jdbcTemplate.update(sql, authorId);
    }

    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment();
            comment.setId(rs.getInt("id"));
            comment.setNewsId(rs.getInt("news_id"));
            comment.setAuthorId(rs.getInt("author_id"));
            comment.setContent(rs.getString("content"));
            comment.setCreatedAt(rs.getString("created_at"));
            return comment;
        }
    }
}