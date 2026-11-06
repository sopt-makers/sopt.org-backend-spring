package sopt.org.homepage.news.instagram;

import java.time.Instant;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableConfigurationProperties(InstagramNewsConfig.Properties.class)
public class InstagramNewsConfig {
    @Bean(name = "instagramTaskScheduler")
    public ThreadPoolTaskScheduler instagramTaskScheduler() {
        return scheduler("instagram-news-");
    }

    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        return scheduler("scheduled-");
    }

    private ThreadPoolTaskScheduler scheduler(String prefix) {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix(prefix);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }

    @ConfigurationProperties("news.instagram")
    public record Properties(
            String apifyToken,
            @DefaultValue("false") boolean enabled,
            @DefaultValue("") String taskId,
            @DefaultValue("2025-12-31T15:00:00Z") Instant since,
            @DefaultValue("7") int overlapDays,
            @DefaultValue("1000") int resultsLimit,
            @DefaultValue("") java.util.List<String> additionalSeries) {
        @org.springframework.boot.context.properties.bind.ConstructorBinding
        public Properties {
            additionalSeries = additionalSeries == null ? java.util.List.of() : java.util.List.copyOf(additionalSeries);
            if (enabled && (apifyToken == null || apifyToken.isBlank())) {
                throw new IllegalArgumentException("news.instagram.apify-token is required when enabled");
            }
            if (enabled && (taskId == null || taskId.isBlank())) {
                throw new IllegalArgumentException("news.instagram.task-id is required when enabled");
            }
            if (since == null || overlapDays < 1 || resultsLimit < 1 || resultsLimit == Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Instagram collection limits must be positive and since must be configured");
            }
        }

        public Properties(String token) {
            this(token, "");
        }

        public Properties(String token, String taskId) {
            this(token, false, taskId, Instant.parse("2025-12-31T15:00:00Z"), 7, 1000, java.util.List.of());
        }

        public Instant collectionSince(Instant checkpoint) {
            if (checkpoint == null) return since;
            Instant overlap = checkpoint.minus(Duration.ofDays(overlapDays));
            return overlap.isAfter(since) ? overlap : since;
        }
    }
}
