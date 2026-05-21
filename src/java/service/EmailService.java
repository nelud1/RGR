package service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    public void sendActivationEmail(String to, String token) {
        String activationLink = "http://localhost:8080/api/auth/activate?token=" + token;
        System.out.println("========== ПИСЬМО АКТИВАЦИИ ==========");
        System.out.println("Кому: " + to);
        System.out.println("Ссылка: " + activationLink);
        System.out.println("=====================================");
    }
}