package sopt.org.homepage.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "sopt.org.homepage.internal")
@Configuration
public class FeignConfig {
}
