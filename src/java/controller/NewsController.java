package controller;

import model.News;
import service.NewsService;
import service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/create")
    public Map<String, Object> createNews(@RequestParam String title,
                                          @RequestParam String content,
                                          @RequestParam String accessLevel,
                                          @RequestParam(required = false) MultipartFile image,
                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        News news = new News();
        news.setAuthorId(userId);
        news.setTitle(title);
        news.setContent(content);
        news.setVisibility(accessLevel);

        newsService.createNews(news);

        News savedNews = newsService.getLastNewsByUser(userId);

        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if ("image/jpeg".equals(contentType) || "image/png".equals(contentType)) {
                String imagePath = fileStorageService.saveNewsImage(savedNews.getId(), image);
                newsService.updateNewsImage(savedNews.getId(), imagePath);
            }
        }

        response.put("success", true);
        return response;
    }

    @GetMapping("/feed")
    public List<Map<String, Object>> getFeed(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("role");

        if (userId == null) {
            return List.of();
        }

        return newsService.getFeed(userId, userRole);
    }

    @PostMapping("/{newsId}/comment")
    public Map<String, Object> addComment(@PathVariable int newsId,
                                          @RequestParam String content,
                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        if (content.length() > 100) {
            response.put("success", false);
            response.put("error", "Комментарий не должен превышать 100 символов");
            return response;
        }

        newsService.addComment(newsId, userId, content);
        response.put("success", true);
        return response;
    }

    @GetMapping("/{newsId}/comments")
    public List<Map<String, Object>> getComments(@PathVariable int newsId) {
        return newsService.getComments(newsId);
    }

    @PostMapping("/{newsId}/like")
    public Map<String, Object> likeNews(@PathVariable int newsId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        newsService.rateNews(newsId, userId, 1);
        response.put("success", true);
        return response;
    }

    @PostMapping("/{newsId}/dislike")
    public Map<String, Object> dislikeNews(@PathVariable int newsId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        newsService.rateNews(newsId, userId, -1);
        response.put("success", true);
        return response;
    }

    @DeleteMapping("/{newsId}")
    public Map<String, Object> deleteNews(@PathVariable int newsId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("role");

        if (userId == null) {
            response.put("success", false);
            response.put("error", "Не авторизован");
            return response;
        }

        newsService.deleteNews(newsId, userRole, userId);
        response.put("success", true);
        return response;
    }
}