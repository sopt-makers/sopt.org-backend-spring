package sopt.org.homepage.news.instagram;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InstagramNewsConfig.Properties.class)
public class InstagramNewsConfig {
    @ConfigurationProperties("news.instagram")
    public record Properties(String apifyToken) {}
}
