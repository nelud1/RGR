package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = "file:C:/Users/Lehah/IdeaProjects/RGR/uploads/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);

        registry.addResourceHandler("/**")
                .addResourceLocations("file:./src/main/webapp/")
                .addResourceLocations("classpath:/static/");
    }
}