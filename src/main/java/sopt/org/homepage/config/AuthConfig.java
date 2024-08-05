package sopt.org.homepage.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AuthConfig {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${official.apikey}")
    private String apiKey;

    @Value("${internal.playground.token}")
    private String playgroundToken;


}
