package sopt.org.homepage.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sopt.org.homepage.admin.dto.request.AddMainRequestDto;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminService {
    private final MainRepository mainRepository;
    private final MainNewsRepository mainNewsRepository;

    @Transactional
    public String addMainData(AddMainRequestDto addMainRequestDto) {
        return null;
    }

//    public GetNotificationListResponseDto getNotificationEmailList(int generation) {
//        List<String> emailList = notificationRepository.findByGeneration(generation).stream()
//                .map(MainEntity::getEmail)
//                .collect(Collectors.toList());
//
//        return new GetNotificationListResponseDto(generation, emailList);
//    }


}
