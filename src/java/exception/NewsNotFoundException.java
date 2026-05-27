package exception;

public class NewsNotFoundException extends RuntimeException {
    public NewsNotFoundException(String message) {
        super(message);
    }

    public NewsNotFoundException(int newsId) {
        super("Новость с ID " + newsId + " не найдена");
    }
}