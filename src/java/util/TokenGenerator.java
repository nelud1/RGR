package util;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TokenGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}