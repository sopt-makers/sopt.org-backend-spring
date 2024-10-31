package sopt.org.homepage.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {
//    private final AdminRepository adminRepository;

//    @Transactional
//    public RegisterNotificationResponseDto registerNotification(RegisterNotificationRequestDto registerNotificationRequestDto) {
//        MainEntity existingNotification = notificationRepository.findByEmailAndGeneration(
//                registerNotificationRequestDto.getEmail(), registerNotificationRequestDto.getGeneration()
//        );
//
//        if (existingNotification != null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    "Already Registered Email: " + registerNotificationRequestDto.getEmail());
//        }
//
//        MainEntity notification = new MainEntity();
//        notification.setGeneration(registerNotificationRequestDto.getGeneration());
//        notification.setEmail(registerNotificationRequestDto.getEmail());
//        notification.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//
//        MainEntity savedNotification = notificationRepository.save(notification);
//
//        return new RegisterNotificationResponseDto(
//                savedNotification.getId(),
//                savedNotification.getGeneration(),
//                savedNotification.getEmail(),
//                savedNotification.getCreatedAt()
//        );
//    }
//
//    public GetNotificationListResponseDto getNotificationEmailList(int generation) {
//        List<String> emailList = notificationRepository.findByGeneration(generation).stream()
//                .map(MainEntity::getEmail)
//                .collect(Collectors.toList());
//
//        return new GetNotificationListResponseDto(generation, emailList);
//    }


}
