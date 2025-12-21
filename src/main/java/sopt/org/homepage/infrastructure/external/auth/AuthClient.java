package sopt.org.homepage.infrastructure.external.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import sopt.org.homepage.infrastructure.external.auth.dto.AuthUserCountResponse;

@FeignClient(value = "AuthClient", url = "https://auth.api.sopt.org")
public interface AuthClient {

    @GetMapping("/api/v1/users/count")
    AuthUserCountResponse getUserCount(
            @RequestParam("generation") Integer generation,
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Service-Name") String serviceName
    );
}
