package sopt.org.homepage.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.notification.dto.NotificationListResponse;
import sopt.org.homepage.notification.dto.RegisterNotificationRequest;
import sopt.org.homepage.notification.dto.RegisterNotificationResponse;

@Tag(name = "Notification", description = "모집 알림 API")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "모집 알림 신청")
    @PostMapping("/register")
    public ResponseEntity<RegisterNotificationResponse> register(
            @Valid @RequestBody RegisterNotificationRequest request
    ) {
        Notification notification = notificationService.register(
                request.email(),
                request.generation()
        );
        return ResponseEntity.ok(RegisterNotificationResponse.from(notification));
    }

    @Operation(summary = "모집 알림 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<NotificationListResponse> getList(
            @RequestParam("generation") Integer generation
    ) {
        List<Notification> notifications = notificationService.findByGeneration(generation);
        return ResponseEntity.ok(NotificationListResponse.from(notifications));
    }
}
