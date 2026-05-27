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
                "app.title", "app.welcome", "app.login", "app.register", "app.logout", "app.save", "app.send", "app.search", "app.cancel",
                "nav.feed", "nav.profile", "nav.friends", "nav.messages", "nav.search",
                "auth.login.title", "auth.login.email", "auth.login.password", "auth.login.button", "auth.login.no_account",
                "auth.register.title", "auth.register.firstname", "auth.register.lastname", "auth.register.email",
                "auth.register.password", "auth.register.confirm_password", "auth.register.button", "auth.register.have_account",
                "profile.title", "profile.firstname", "profile.lastname", "profile.email", "profile.birthdate", "profile.gender",
                "profile.gender.male", "profile.gender.female", "profile.gender.not_specified", "profile.city", "profile.about",
                "profile.avatar", "news.create", "news.title", "news.content", "news.visibility", "news.visibility.public",
                "news.visibility.friends", "news.image", "news.publish", "messages.title", "messages.dialogs", "messages.type_message",
                "messages.send", "search.title", "search.users", "search.news", "search.firstname", "search.lastname", "search.city",
                "search.age_from", "search.age_to", "search.keyword", "friends.title", "friends.requests", "friends.list",
                "friends.accept", "friends.message", "friends.remove", "comment.send", "comment.placeholder"
        };

        for (String key : keys) {
            messages.put(key, messageSource.getMessage(key, null, locale));
        }

        return messages;
    }
}