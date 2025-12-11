package sopt.org.homepage.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.notification.controller.dto.NotificationListResponse;
import sopt.org.homepage.notification.controller.dto.RegisterNotificationRequest;
import sopt.org.homepage.notification.controller.dto.RegisterNotificationResponse;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.service.NotificationCommandService;
import sopt.org.homepage.notification.service.NotificationQueryService;

@Slf4j
@Tag(name = "Notification", description = "모집 알림 API")
@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    @Operation(
            summary = "모집 알림 신청",
            description = "특정 기수의 모집 알림을 신청합니다. 이메일과 기수 조합은 유일해야 합니다."
    )
    @PostMapping("register")
    public ResponseEntity<RegisterNotificationResponse> registerNotification(
            @Valid @RequestBody RegisterNotificationRequest request
    ) {
        log.info("[API] POST /notifications - email={}, generation={}",
                request.email(), request.generation());
        Notification result = notificationCommandService.register(request);
        log.info("[API] POST /notifications - SUCCESS id={}", result.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RegisterNotificationResponse.from(result));
    }

    @Operation(
            summary = "알림 신청 목록 조회",
            description = "특정 기수에 신청된 모든 알림 이메일을 조회합니다."
    )
    @GetMapping("list")
    public ResponseEntity<NotificationListResponse> getNotificationList(
            @RequestParam("generation") Integer generation
    ) {
        List<Notification> notifications = notificationQueryService.getNotificationList(generation);

        return ResponseEntity.ok(
                NotificationListResponse.from(notifications)
        );
    }
}
