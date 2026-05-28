package repository;

import model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        List<Profile> profiles = jdbcTemplate.query(sql, new ProfileRowMapper(), userId);
        return profiles.isEmpty() ? null : profiles.get(0);
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