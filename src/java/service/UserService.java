package service;

import dto.UserRegistrationDto;
import model.User;
import model.Profile;
import repository.UserRepository;
import repository.ProfileRepository;
import util.PasswordHasher;
import util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private EmailService emailService;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean register(UserRegistrationDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail().toLowerCase());
        user.setPasswordHash(passwordHasher.hash(dto.getPassword()));
        user.setRole("USER");
        user.setActive(false);
        user.setActivationToken(tokenGenerator.generate());

        userRepository.save(user);

        User savedUser = userRepository.findByEmail(dto.getEmail());

        Profile profile = new Profile();
        profile.setUserId(savedUser.getId());
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setBirthDate(null);
        profile.setGender(null);
        profile.setCity(null);
        profile.setAboutMe(null);
        profile.setAvatarPath(null);

        profileRepository.save(profile);

        emailService.sendActivationEmail(user.getEmail(), user.getActivationToken());

        return true;
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        if (!user.isActive()) {
            return null;
        }
        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            return null;
        }
        return user;
    }

    public boolean activateAccount(String token) {
        User user = userRepository.findByActivationToken(token);
        if (user == null) {
            return false;
        }
        user.setActive(true);
        user.setActivationToken(null);
        userRepository.update(user);
        return true;
    }

    public User findById(int id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Profile getProfile(int userId) {
        return profileRepository.findByUserId(userId);
    }

    public void updateProfile(Profile profile) {
        Profile existing = profileRepository.findByUserId(profile.getUserId());
        if (existing != null) {
            profileRepository.update(profile);
        } else {
            profileRepository.save(profile);
        }
    }

    public void updateRole(int userId, String role) {
        userRepository.updateRole(userId, role);
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }
}