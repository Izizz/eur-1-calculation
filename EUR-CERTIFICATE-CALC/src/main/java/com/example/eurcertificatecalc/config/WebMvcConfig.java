package com.example.eurcertificatecalc.config;


import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final long MAX_AGE_SECS = 3600;
    private final String[] allowedOrigins;
    private final List<String> allowedEndpoints;

    public WebMvcConfig(
            @Value("${cors.allowed-origins}") final String[] allowedOrigins,
            @Value("${cors.allowed-endpoints}") final List<String> allowedEndpoints) {
        this.allowedOrigins = allowedOrigins;
        this.allowedEndpoints = allowedEndpoints;
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        for (String allowedEndpoint : allowedEndpoints) {
            registry
                    .addMapping(allowedEndpoint)
                    .allowedOrigins(allowedOrigins)
                    .allowedMethods(
                            GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name())
                    .allowCredentials(true)
                    .allowedHeaders("*")
                    .maxAge(MAX_AGE_SECS);
        }
    }
}