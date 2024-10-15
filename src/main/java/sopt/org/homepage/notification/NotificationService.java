package sopt.org.homepage.notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sopt.org.homepage.entity.NotificationEntity;
import sopt.org.homepage.notification.dto.GetNotificationListResponseDto;
import sopt.org.homepage.notification.dto.RegisterNotificationRequestDto;
import sopt.org.homepage.notification.dto.RegisterNotificationResponseDto;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.Timestamp;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public RegisterNotificationResponseDto registerNotification(RegisterNotificationRequestDto registerNotificationRequestDto) {
        NotificationEntity existingNotification = notificationRepository.findByEmailAndGeneration(
                registerNotificationRequestDto.getEmail(), registerNotificationRequestDto.getGeneration()
        );

        if (existingNotification != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Already Registered Email: " + registerNotificationRequestDto.getEmail());
        }

        // Create a new Notification entity from the DTO
        NotificationEntity notification = new NotificationEntity();
        notification.setGeneration(registerNotificationRequestDto.getGeneration());
        notification.setEmail(registerNotificationRequestDto.getEmail());
        notification.setCreatedAt(new Timestamp(System.currentTimeMillis())); // Assuming createdAt is of type Timestamp

        // Save the new notification and return the response DTO
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
                .collect(Collectors.toList());

        return new GetNotificationListResponseDto(generation, emailList);
    }


}
