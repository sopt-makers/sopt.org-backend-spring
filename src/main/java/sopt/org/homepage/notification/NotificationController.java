package sopt.org.homepage.notification;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sopt.org.homepage.notification.dto.GetNotificationListRequestDto;
import sopt.org.homepage.notification.dto.GetNotificationListResponseDto;
import sopt.org.homepage.notification.dto.RegisterNotificationRequestDto;
import sopt.org.homepage.notification.dto.RegisterNotificationResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("notification")
@Tag(name = "Notification")
public class NotificationController {
	private final NotificationService notificationService;

	@PostMapping("register")
	public ResponseEntity<RegisterNotificationResponseDto> registerNotification(
		@Valid @RequestBody @ModelAttribute RegisterNotificationRequestDto registerNotificationRequestDto
	) {
		RegisterNotificationResponseDto result = notificationService.registerNotification(
			registerNotificationRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@GetMapping("/list")
	public ResponseEntity<GetNotificationListResponseDto> getAllProject(
		@ParameterObject @ModelAttribute GetNotificationListRequestDto getNotificationListRequestDto
	) {
		GetNotificationListResponseDto result = notificationService.getNotificationEmailList(
			getNotificationListRequestDto.getGeneration());
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}


