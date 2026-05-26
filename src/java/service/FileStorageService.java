package service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            Path avatarsPath = Paths.get(uploadPath, "avatars");
            Path newsPath = Paths.get(uploadPath, "news");

            if (!Files.exists(avatarsPath)) {
                Files.createDirectories(avatarsPath);
            }
            if (!Files.exists(newsPath)) {
                Files.createDirectories(newsPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папки для загрузок", e);
        }
    }

    private void validateFileName(String filename) {
        if (filename == null || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new RuntimeException("Недопустимое имя файла");
        }
    }

    private void validateFilePath(Path filePath) {
        if (!filePath.toAbsolutePath().startsWith(Paths.get(uploadPath).toAbsolutePath())) {
            throw new RuntimeException("Попытка выхода за пределы разрешенной директории");
        }
    }

    public String saveAvatar(int userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        validateFileName(originalFilename);

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new RuntimeException("Только JPG и PNG");
        }

        try {
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = "user_" + userId + "_" + UUID.randomUUID().toString() + extension;
            Path filePath = Paths.get(uploadPath, "avatars", filename);
            validateFilePath(filePath);

            Files.write(filePath, file.getBytes());
            return "/uploads/avatars/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить аватар", e);
        }
    }

    public String saveNewsImage(int newsId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        validateFileName(originalFilename);

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new RuntimeException("Только JPG и PNG");
        }

        try {
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = "news_" + newsId + "_" + UUID.randomUUID().toString() + extension;
            Path filePath = Paths.get(uploadPath, "news", filename);
            validateFilePath(filePath);

            Files.write(filePath, file.getBytes());
            return "/uploads/news/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить изображение новости", e);
        }
    }
}