package com.user.management.config;
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
          registry.addMapping("/**") // Allows all endpoints
              .allowedOriginPatterns("http://localhost:3000", "http://yourfrontend.com")              .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Allowed HTTP methods
              .allowedHeaders("*") // Allow all headers
              .allowCredentials(true); // Allow credentials (use only if needed)
        }
      };
    }
  }
