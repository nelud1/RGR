package repository;

import model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ProfileRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, birth_date, gender, city, about_me, avatar_path) VALUES (?, ?, ?, CAST(? AS DATE), ?, ?, ?, ?)";
        jdbcTemplate.update(sql, profile.getUserId(), profile.getFirstName(), profile.getLastName(), profile.getBirthDate(), profile.getGender(), profile.getCity(), profile.getAboutMe(), profile.getAvatarPath());
    }

    public Profile findByUserId(int userId) {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ProfileRowMapper(), userId);
        } catch (Exception e) {
            return null;
        }
    }

    public void update(Profile profile) {
        String sql = "UPDATE profiles SET first_name = ?, last_name = ?, birth_date = CAST(? AS DATE), gender = ?, city = ?, about_me = ?, avatar_path = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, profile.getFirstName(), profile.getLastName(), profile.getBirthDate(), profile.getGender(), profile.getCity(), profile.getAboutMe(), profile.getAvatarPath(), profile.getUserId());
    }

    private static class ProfileRowMapper implements RowMapper<Profile> {
        @Override
        public Profile mapRow(ResultSet rs, int rowNum) throws SQLException {
            Profile profile = new Profile();
            profile.setUserId(rs.getInt("user_id"));
            profile.setFirstName(rs.getString("first_name"));
            profile.setLastName(rs.getString("last_name"));
            profile.setBirthDate(rs.getString("birth_date"));
            profile.setGender(rs.getString("gender"));
            profile.setCity(rs.getString("city"));
            profile.setAboutMe(rs.getString("about_me"));
            profile.setAvatarPath(rs.getString("avatar_path"));
            return profile;
        }
    }
}