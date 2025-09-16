package com.example.book.config;



import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi restApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/**")
                .group("REST API")
                .build();
    }
    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .pathsToMatch("/**/*")
                .pathsToExclude("/api/**/*")
                .group("COMMON API")
                .build();
    }


}
