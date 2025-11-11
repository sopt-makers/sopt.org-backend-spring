package sopt.org.homepage.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.notification.controller.dto.NotificationListResponse;
import sopt.org.homepage.notification.controller.dto.RegisterNotificationRequest;
import sopt.org.homepage.notification.controller.dto.RegisterNotificationResponse;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.service.command.NotificationCommandService;
import sopt.org.homepage.notification.service.command.dto.NotificationResult;
import sopt.org.homepage.notification.service.command.dto.RegisterNotificationCommand;
import sopt.org.homepage.notification.service.query.NotificationQueryService;
import sopt.org.homepage.notification.service.query.dto.NotificationListView;

/**
 * Notification Controller
 * - Command/Query 분리
 * - 얇은 Controller: DTO 변환만 담당
 */
@Tag(name = "Notification", description = "모집 알림 API")
@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    /**
     * 모집 알림 신청 등록
     */
    @Operation(
            summary = "모집 알림 신청",
            description = "특정 기수의 모집 알림을 신청합니다. 이메일과 기수 조합은 유일해야 합니다."
    )
    @PostMapping("register")
    public ResponseEntity<RegisterNotificationResponse> registerNotification(
            @Valid @RequestBody RegisterNotificationRequest request
    ) {
        Notification result = notificationCommandService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RegisterNotificationResponse.from(result));
    }

    /**
     * 특정 기수의 알림 신청 목록 조회
     */
    @Operation(
            summary = "알림 신청 목록 조회",
            description = "특정 기수에 신청된 모든 알림 이메일을 조회합니다."
    )
    @GetMapping("list")
    public ResponseEntity<NotificationListResponse> getNotificationList(
            @RequestParam("generation") Integer generation
    ) {
        // 1. Query 실행
        NotificationListView view = notificationQueryService.getNotificationList(generation);

        // 2. View → Response 변환
        NotificationListResponse response = NotificationListResponse.from(view);

        return ResponseEntity.ok(response);
    }
}
