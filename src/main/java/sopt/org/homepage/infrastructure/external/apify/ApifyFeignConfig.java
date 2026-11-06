package sopt.org.homepage.infrastructure.external.apify;

import feign.Request;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;

/** Apify 전용 설정이며 다른 Feign 클라이언트에는 적용하지 않는다. */
public class ApifyFeignConfig {
    @Bean
    public Request.Options apifyRequestOptions() {
        return new Request.Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true);
    }

    @Bean
    public Retryer apifyRetryer() {
        // 응답 유실 시 유료 실행을 자동으로 중복 생성하지 않는다.
        return Retryer.NEVER_RETRY;
    }
}
