package sopt.org.homepage.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sopt.org.homepage.news.controller.dto.request.AddAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.request.DeleteAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.request.GetAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminNewsResponseDto;
import sopt.org.homepage.news.controller.dto.response.DeleteAdminNewsResponseDto;
import sopt.org.homepage.news.controller.dto.response.GetAdminNewsResponseDto;
import sopt.org.homepage.aws.s3.S3Service;
import sopt.org.homepage.news.MainNewsEntity;
import sopt.org.homepage.news.repository.MainNewsRepository;


@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {
    private final S3Service s3Service;
    private final MainNewsRepository mainNewsRepository;

    // MainNews 관련 메서드들은 기존 유지
    public AddAdminNewsResponseDto addMainNews(AddAdminNewsRequestDto request) {
        log.info("Adding main news: {}", request.getTitle());

        // 1. S3에 이미지 업로드
        String imageUrl = s3Service.uploadFile(request.getImage(), "news/");

        // 2. MainNews 엔티티 생성 및 저장
        MainNewsEntity newsEntity = new MainNewsEntity();
        newsEntity.setTitle(request.getTitle());
        newsEntity.setLink(request.getLink());
        newsEntity.setImage(imageUrl);

        mainNewsRepository.save(newsEntity);

        log.info("Main news added successfully: {}", newsEntity.getId());

        return AddAdminNewsResponseDto.builder()
                .message("최신소식 추가 성공")
                .build();
    }

    public DeleteAdminNewsResponseDto deleteMainNews(DeleteAdminNewsRequestDto request) {
        log.info("Deleting main news: {}", request.getId());

        // 1. MainNews 조회
        MainNewsEntity newsEntity = mainNewsRepository.findById(request.getId());
        if (newsEntity == null) {
            throw new sopt.org.homepage.exception.ClientBadRequestException(
                    "News not found with id: " + request.getId());
        }

        // 2. S3에서 이미지 삭제
        s3Service.deleteFile(newsEntity.getImage());

        // 3. DB에서 삭제
        mainNewsRepository.delete(newsEntity);

        log.info("Main news deleted successfully: {}", request.getId());

        return DeleteAdminNewsResponseDto.builder()
                .message("최신소식 삭제 성공")
                .build();
    }

    public GetAdminNewsResponseDto getMainNews(GetAdminNewsRequestDto request) {
        log.info("Getting main news: {}", request.getId());

        // 1. MainNews 조회
        MainNewsEntity newsEntity = mainNewsRepository.findById(request.getId());
        if (newsEntity == null) {
            throw new sopt.org.homepage.exception.ClientBadRequestException(
                    "News not found with id: " + request.getId());
        }

        // 2. Response 생성
        return GetAdminNewsResponseDto.builder()
                .id(newsEntity.getId())
                .title(newsEntity.getTitle())
                .link(newsEntity.getLink())
                .image(newsEntity.getImage())
                .build();
    }

}
