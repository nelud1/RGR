package service;

import exception.EmailSendingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendActivationEmail(String to, String token) {
        String activationLink = "http://localhost:8080/api/auth/activate?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Подтверждение регистрации в Социальной сети");
        message.setText("Здравствуйте!\n\nДля активации вашего аккаунта перейдите по ссылке:\n" + activationLink +
                "\n\nЕсли вы не регистрировались, проигнорируйте это письмо.\n\nС уважением,\nКоманда Социальной сети");

        try {
            mailSender.send(message);
            System.out.println("Email активации отправлен на: " + to);
        } catch (Exception e) {
            throw new EmailSendingException("Не удалось отправить email активации: " + e.getMessage(), e);
        }
    }

    @Async
    public void sendMessageNotification(String toEmail, String fromName, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Новое сообщение от " + fromName + " в Социальной сети");
        message.setText("Здравствуйте!\n\nВы получили новое сообщение в Социальной сети.\n\n" +
                "Отправитель: " + fromName + "\n" +
                "Тема: " + subject + "\n\n" +
                "Текст сообщения:\n" + content + "\n\n" +
                "Ответить на сообщение можно в Социальной сети.\n\nС уважением,\nКоманда Социальной сети");

        try {
            mailSender.send(message);
            System.out.println("Email уведомление отправлено на: " + toEmail);
        } catch (Exception e) {
            throw new EmailSendingException("Не удалось отправить email уведомление: " + e.getMessage(), e);
        }
    }
}