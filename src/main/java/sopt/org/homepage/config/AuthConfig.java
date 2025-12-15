package sopt.org.homepage.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AuthConfig {

    @Value("${official.apikey}")
    private String apiKey;

    @Value("${internal.playground.token}")
    private String playgroundToken;

    @Value("${internal.crew.token}")
    private String crewApiToken;

    @Value("${internal.auth.api-key}")
    private String authApiKey;

    @Value("${internal.auth.service-name}")
    private String authServiceName;


}
