package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "service", "repository", "config", "util"})
@EnableAsync
public class SocialNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
        System.out.println("=========================================");
        System.out.println("Социальная сеть запущена!");
        System.out.println("http://localhost:8080");
        System.out.println("=========================================");
    }
}