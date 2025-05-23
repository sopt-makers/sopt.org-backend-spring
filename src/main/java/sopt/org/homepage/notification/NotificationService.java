package sopt.org.homepage.notification;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sopt.org.homepage.notification.dto.GetNotificationListResponseDto;
import sopt.org.homepage.notification.dto.RegisterNotificationRequestDto;
import sopt.org.homepage.notification.dto.RegisterNotificationResponseDto;

@RequiredArgsConstructor
@Service
public class NotificationService {
	private final NotificationRepository notificationRepository;

	@Transactional
	public RegisterNotificationResponseDto registerNotification(
		RegisterNotificationRequestDto registerNotificationRequestDto) {
		NotificationEntity existingNotification = notificationRepository.findByEmailAndGeneration(
			registerNotificationRequestDto.getEmail(), registerNotificationRequestDto.getGeneration()
		);

		if (existingNotification != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				"Already Registered Email: " + registerNotificationRequestDto.getEmail());
		}

		NotificationEntity notification = new NotificationEntity();
		notification.setGeneration(registerNotificationRequestDto.getGeneration());
		notification.setEmail(registerNotificationRequestDto.getEmail());
		notification.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		NotificationEntity savedNotification = notificationRepository.save(notification);

		return new RegisterNotificationResponseDto(
			savedNotification.getId(),
			savedNotification.getGeneration(),
			savedNotification.getEmail(),
			savedNotification.getCreatedAt()
		);
	}

	public GetNotificationListResponseDto getNotificationEmailList(int generation) {
		List<String> emailList = notificationRepository.findByGeneration(generation).stream()
			.map(NotificationEntity::getEmail)
			.toList();

		return new GetNotificationListResponseDto(generation, emailList);
	}

}
