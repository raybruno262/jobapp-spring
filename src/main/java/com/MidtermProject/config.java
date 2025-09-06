package com.MidtermProject;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;
@Configuration
public class config implements WebMvcConfigurer {
    @SuppressWarnings("null")
   

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // .allowedOrigins("http://localhost:3000") 
            .allowedOrigins("https://jobapp-react-sljy.onrender.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true)
            .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "Accept");
    }
}

