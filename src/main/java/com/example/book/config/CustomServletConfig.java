package com.example.book.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CustomServletConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/js/**")
        .addResourceLocations("classpath:/static/js/");

    registry.addResourceHandler("/fonts/**")
        .addResourceLocations("classpath:/static/fonts/");

    registry.addResourceHandler("/css/**")
        .addResourceLocations("classpath:/static/css/");

    registry.addResourceHandler("/assets/**").
        addResourceLocations("classpath:/static/assets/");

    registry.addResourceHandler("/images/**") // logo image용 추가 0918 석준영
            .addResourceLocations("classpath:/static/images/");

    // 업로드된 파일 URL 매핑
    registry.addResourceHandler("/uploads/**")
      .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
  }
}