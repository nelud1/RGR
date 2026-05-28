package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/i18n")
public class I18nController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping(value = "/messages", produces = "application/json;charset=UTF-8")
    public Map<String, String> getMessages(@RequestParam(required = false, defaultValue = "ru") String lang) {
        Locale locale = new Locale(lang);
        Map<String, String> messages = new HashMap<>();

        String[] keys = {
                "app.title", "app.welcome", "app.login", "app.register", "app.logout", "app.save", "app.send", "app.search", "app.cancel", "app.success",
                "nav.feed", "nav.profile", "nav.friends", "nav.messages", "nav.search",
                "auth.login.title", "auth.login.email", "auth.login.password", "auth.login.button", "auth.login.no_account",
                "auth.register.title", "auth.register.firstname", "auth.register.lastname", "auth.register.email",
                "auth.register.password", "auth.register.confirm_password", "auth.register.button", "auth.register.have_account",
                "auth.register.success", "auth.register.password_mismatch", "auth.login.error", "auth.login.locked", "auth.login.attempts_left",
                "profile.title", "profile.firstname", "profile.lastname", "profile.email", "profile.birthdate", "profile.gender",
                "profile.gender.male", "profile.gender.female", "profile.gender.not_specified", "profile.city", "profile.about",
                "profile.avatar", "profile.avatar.success", "profile.avatar.error", "profile.save.success", "profile.age", "profile.role", "profile.city.not_specified",
                "news.create", "news.title", "news.content", "news.visibility", "news.visibility.public", "news.visibility.friends",
                "news.image", "news.publish", "news.published", "news.no_news", "news.author", "news.delete", "news.comment", "news.likes", "news.dislikes",
                "comment.send", "comment.placeholder",
                "friends.title", "friends.requests", "friends.no_requests", "friends.list", "friends.no_friends", "friends.accept",
                "friends.message", "friends.remove", "friends.request_sent", "friends.request_accepted", "friends.request_error",
                "friends.already_friends", "friends.already_requested", "friends.add",
                "messages.title", "messages.dialogs", "messages.no_dialogs", "messages.type_message", "messages.send",
                "messages.empty", "messages.no_messages", "messages.load_error",
                "search.title", "search.users", "search.news", "search.firstname", "search.lastname", "search.city",
                "search.age_from", "search.age_to", "search.keyword", "search.no_users", "search.no_news", "search.error",
                "role.admin", "role.moderator", "role.user", "role.make_moderator", "role.remove_moderator", "role.delete_user",
                "role.delete_confirm", "role.user_deleted",
                "error.server", "error.unauthorized", "error.access_denied", "error.file.invalid", "error.file.type",
                "error.file.size", "error.file.not_selected", "error.title_required", "error.title_too_long",
                "error.content_required", "error.content_too_long", "error.comment_empty", "error.comment_too_long",
                "error.message_empty", "error.message_too_long", "error.email_required", "error.email_invalid",
                "error.firstname_required", "error.lastname_required", "error.password_too_short", "error.email_exists",
                "news.delete_confirm", "friends.accept_confirm", "friends.remove_confirm", "friends.remove_success",
                "messages.send_error"
        };

        for (String key : keys) {
            messages.put(key, messageSource.getMessage(key, null, locale));
        }

        return messages;
    }
}