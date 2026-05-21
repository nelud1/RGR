package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "service", "repository", "config", "util"})
public class SocialNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
        System.out.println("=========================================");
        System.out.println("Социальная сеть запущена!");
        System.out.println("http://localhost:8080");
        System.out.println("=========================================");
    }
}