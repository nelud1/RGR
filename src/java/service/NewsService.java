package service;

import model.News;
import model.Comment;
import model.Profile;
import repository.NewsRepository;
import repository.CommentRepository;
import repository.RatingRepository;
import repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserService userService;

    public void createNews(News news) {
        newsRepository.save(news);
    }

    public News getLastNewsByUser(int userId) {
        return newsRepository.findLastByAuthorId(userId);
    }

    public void updateNewsImage(int newsId, String imagePath) {
        newsRepository.updateImage(newsId, imagePath);
    }

    

    public List<Map<String, Object>> getFeed(int userId, String userRole) {
        boolean isModerator = "MODERATOR".equals(userRole) || "ADMIN".equals(userRole);
        List<Integer> friendIds = friendshipRepository.getFriendIds(userId);
        List<News> newsList = newsRepository.findFeedForUser(userId, friendIds, isModerator);

        List<Map<String, Object>> result = new ArrayList<>();
        for (News news : newsList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", news.getId());
            item.put("title", news.getTitle());
            item.put("content", news.getContent());
            item.put("imagePath", news.getImagePath());
            item.put("externalLink", news.getExternalLink());
            item.put("visibility", news.getVisibility());
            item.put("createdAt", news.getCreatedAt());

            Profile author = userService.getProfile(news.getAuthorId());
            String authorName = "Пользователь";
            if (author != null) {
                authorName = author.getFirstName() + " " + author.getLastName();
            }
            item.put("authorName", authorName);
            item.put("authorId", news.getAuthorId());

            item.put("likesCount", ratingRepository.getLikesCount(news.getId()));
            item.put("dislikesCount", ratingRepository.getDislikesCount(news.getId()));

            boolean canDelete = "ADMIN".equals(userRole) || "MODERATOR".equals(userRole) || news.getAuthorId() == userId;
            item.put("canDelete", canDelete);

            result.add(item);
        }
        return result;
    }

    public void addComment(int newsId, int authorId, String content) {
        Comment comment = new Comment();
        comment.setNewsId(newsId);
        comment.setAuthorId(authorId);
        comment.setContent(content);
        commentRepository.save(comment);
    }

    public List<Map<String, Object>> getComments(int newsId) {
        List<Comment> comments = commentRepository.findByNewsId(newsId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", comment.getId());
            item.put("content", comment.getContent());
            item.put("createdAt", comment.getCreatedAt());

            Profile author = userService.getProfile(comment.getAuthorId());
            String authorName = "Пользователь";
            if (author != null) {
                authorName = author.getFirstName() + " " + author.getLastName();
            }
            item.put("authorName", authorName);
            item.put("authorId", comment.getAuthorId());

            result.add(item);
        }
        return result;
    }

    public void rateNews(int newsId, int userId, int rating) {
        ratingRepository.saveOrUpdate(newsId, userId, rating);
    }

    public Map<String, Integer> getRatings(int newsId) {
        Map<String, Integer> result = new HashMap<>();
        result.put("likes", ratingRepository.getLikesCount(newsId));
        result.put("dislikes", ratingRepository.getDislikesCount(newsId));
        return result;
    }

    public Integer getUserRating(int newsId, int userId) {
        return ratingRepository.getUserRating(newsId, userId);
    }

    public void deleteNews(int newsId, String userRole, int userId) {
        News news = newsRepository.findById(newsId);
        if (news == null) {
            return;
        }
        if ("ADMIN".equals(userRole) || "MODERATOR".equals(userRole) || news.getAuthorId() == userId) {
            ratingRepository.deleteByNewsId(newsId);
            commentRepository.deleteByNewsId(newsId);
            newsRepository.deleteById(newsId);
        }
    }

    public List<Map<String, Object>> searchNews(String keyword) {
        List<News> newsList = newsRepository.searchByKeyword(keyword);
        List<Map<String, Object>> result = new ArrayList<>();
        for (News news : newsList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", news.getId());
            item.put("title", news.getTitle());
            item.put("content", news.getContent());
            item.put("createdAt", news.getCreatedAt());

            Profile author = userService.getProfile(news.getAuthorId());
            String authorName = "Пользователь";
            if (author != null) {
                authorName = author.getFirstName() + " " + author.getLastName();
            }
            item.put("authorName", authorName);

            result.add(item);
        }
        return result;
    }
}