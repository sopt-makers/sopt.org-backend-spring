package sopt.org.homepage.health;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("health")
@Tag(name = "HealthCheck")
public class HealthCheckController {
    @GetMapping("")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("HealthCheck Complete");
    }

    @GetMapping("/sentryerror")
    public void testSentryError() {
        throw new RuntimeException("Sentry 테스트 에러입니다!");
    }

    @GetMapping("/sentryerror2")
    public void testSentryError2() {
        throw new RuntimeException("Sentry 테스트 에러입니다222222!");
    }

    @GetMapping("/sentryerror3")
    public void testSentryError3() {
        throw new RuntimeException("Sentry 테스트 에러입니다3333333");
    }


}


