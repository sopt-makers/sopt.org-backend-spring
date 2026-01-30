package sopt.org.homepage.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "sopt.org.homepage.infrastructure.external")  // ✅ 새 경로
@Configuration
public class FeignConfig {
}
