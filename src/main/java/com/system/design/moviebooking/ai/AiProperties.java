package com.system.design.moviebooking.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "ai")
@Validated
@Getter
@Setter
public class AiProperties {
    @NotBlank
    private String baseUrl;
    @NotBlank
    private String apiKey;
    @NotBlank
    private String model;
    @Positive
    private Integer maxTokens;
    private Double temperature;
    @Positive
    private Integer connectTimeoutMs;
    @Positive
    private Integer readTimeoutMs;
    @PositiveOrZero
    private int maxRetries=2;
    @PositiveOrZero
    private Integer backoffMs;
}
