package sopt.org.homepage.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sopt.org.homepage.admin.dto.request.AddMainNewsRequestDto;
import sopt.org.homepage.admin.dto.request.AddMainRequestDto;
import sopt.org.homepage.admin.dto.request.DeleteMainNewsRequestDto;
import sopt.org.homepage.admin.dto.request.GetMainNewsRequestDto;
import sopt.org.homepage.admin.dto.response.GetMainNewsResponseDto;
import sopt.org.homepage.aws.s3.S3Service;
import sopt.org.homepage.cache.CacheService;
import sopt.org.homepage.exception.ClientBadRequestException;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminService {
    private final MainRepository mainRepository;
    private final MainNewsRepository mainNewsRepository;

    private final S3Service s3Service;
    private final CacheService cacheService;

    @Transactional
    public String addMainData(AddMainRequestDto addMainRequestDto) {
        return null;
    }

    public void addMainNews(AddMainNewsRequestDto addMainNewsRequestDto) {
        String imageUrl = s3Service.uploadFile(addMainNewsRequestDto.getImage(), "news/");
        MainNewsEntity newsEntity = new MainNewsEntity();
        newsEntity.setTitle(addMainNewsRequestDto.getTitle());
        newsEntity.setLink(addMainNewsRequestDto.getLink());
        newsEntity.setImage(imageUrl);

        mainNewsRepository.save(newsEntity);
    }

    public void deleteMainNews(DeleteMainNewsRequestDto deleteMainNewsRequestDto) {
        MainNewsEntity newsEntity = mainNewsRepository.findById(deleteMainNewsRequestDto.getId());
        if (newsEntity == null) {
            throw new ClientBadRequestException("News not found with id: " + deleteMainNewsRequestDto.getId());
        }

        s3Service.deleteFile(newsEntity.getImage());

        mainNewsRepository.delete(newsEntity);
    }

    public GetMainNewsResponseDto getMainNews(GetMainNewsRequestDto getMainNewsRequestDto) {
        MainNewsEntity newsEntity = mainNewsRepository.findById(getMainNewsRequestDto.getId());
        if (newsEntity == null) {
            throw new ClientBadRequestException("News not found with id: " + getMainNewsRequestDto.getId());
        }

        return GetMainNewsResponseDto.builder()
                .id(newsEntity.getId())
                .title(newsEntity.getTitle())
                .link(newsEntity.getLink())
                .image(newsEntity.getImage())
                .build();
    }

//    public GetNotificationListResponseDto getNotificationEmailList(int generation) {
//        List<String> emailList = notificationRepository.findByGeneration(generation).stream()
//                .map(MainEntity::getEmail)
//                .collect(Collectors.toList());
//
//        return new GetNotificationListResponseDto(generation, emailList);
//    }


}
