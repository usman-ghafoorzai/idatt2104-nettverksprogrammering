package edu.ntnu.npoving5backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:63342") // Frontend URL
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Viktig!
            .allowedHeaders("*") // Tillat alle headers
            .exposedHeaders("Access-Control-Allow-Origin") // Sørg for at nettleseren ser CORS-headeren
            .allowCredentials(true);
      }
    };
  }
}
