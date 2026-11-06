package sopt.org.homepage.news;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.exception.ClientBadRequestException;
import sopt.org.homepage.infrastructure.aws.s3.S3Service;
import sopt.org.homepage.news.dto.AddAdminNewsRequestDto;
import sopt.org.homepage.news.dto.AddAdminNewsResponseDto;
import sopt.org.homepage.news.dto.AddAdminNewsV2RequestDto;
import sopt.org.homepage.news.dto.BulkCreateNewsCommand;
import sopt.org.homepage.news.dto.DeleteAdminNewsRequestDto;
import sopt.org.homepage.news.dto.DeleteAdminNewsResponseDto;
import sopt.org.homepage.news.dto.EditAdminNewsRequestDto;
import sopt.org.homepage.news.dto.EditAdminNewsResponseDto;
import sopt.org.homepage.news.dto.EditAdminNewsV2RequestDto;
import sopt.org.homepage.news.dto.GetAdminNewsRequestDto;
import sopt.org.homepage.news.dto.GetAdminNewsResponseDto;

/**
 * NewsService
 * <p>
 * 최신소식 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final S3Service s3Service;
    private final NewsRepository newsRepository;

    // ===== Command 메서드 =====

    /**
     * 최신소식 추가 (파일 업로드 방식)
     */
    @Transactional
    public AddAdminNewsResponseDto addMainNews(AddAdminNewsRequestDto request) {
        log.info("최신소식 추가 - title={}", request.getTitle());

        // 1. S3에 이미지 업로드
        String imageUrl = s3Service.uploadFile(request.getImage(), "news/");

        // 2. News 엔티티 생성 및 저장
        News news = News.builder()
                .title(request.getTitle())
                .link(request.getLink())
                .image(imageUrl)
                .build();

        newsRepository.save(news);

        log.info("최신소식 추가 완료 - id={}", news.getId());

        return AddAdminNewsResponseDto.builder()
                .message("최신소식 추가 성공")
                .build();
    }

    /**
     * 최신소식 추가 (Presigned URL 방식)
     * <p>
     * Lambda 환경에서 10MB 페이로드 제한을 우회하기 위해 사용
     */
    @Transactional
    public AddAdminNewsResponseDto addMainNewsV2(AddAdminNewsV2RequestDto request) {
        log.info("최신소식 추가 (Presigned URL) - title={}", request.getTitle());

        // 이미지는 이미 S3에 업로드됨 → DB에만 저장
        News news = News.builder()
                .title(request.getTitle())
                .link(request.getLink())
                .image(request.getImageUrl())
                .build();

        newsRepository.save(news);

        log.info("최신소식 추가 완료 (Presigned URL) - id={}", news.getId());

        return AddAdminNewsResponseDto.builder()
                .message("최신소식 추가 성공")
                .build();
    }

    /**
     * 최신소식 수정 (파일 업로드 방식)
     */
    @Transactional
    public EditAdminNewsResponseDto editMainNews(int id, EditAdminNewsRequestDto request) {
        log.info("최신소식 수정 - id={}", id);

        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ClientBadRequestException(
                        "News not found with id: " + id
                ));

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            s3Service.deleteFile(news.getImage());
            imageUrl = s3Service.uploadFile(request.getImage(), "news/");
        }

        news.update(imageUrl, request.getTitle(), request.getLink());

        log.info("최신소식 수정 완료 - id={}", id);

        return EditAdminNewsResponseDto.builder()
                .message("최신소식 수정 성공")
                .build();
    }

    /**
     * 최신소식 수정 (Presigned URL 방식)
     * <p>
     * Lambda 환경에서 10MB 페이로드 제한을 우회하기 위해 사용
     */
    @Transactional
    public EditAdminNewsResponseDto editMainNewsV2(int id, EditAdminNewsV2RequestDto request) {
        log.info("최신소식 수정 (Presigned URL) - id={}", id);

        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ClientBadRequestException(
                        "News not found with id: " + id
                ));

        if (!request.getImageUrl().equals(news.getImage())) {
            s3Service.deleteFile(news.getImage());
        }

        news.update(request.getImageUrl(), request.getTitle(), request.getLink());

        log.info("최신소식 수정 완료 (Presigned URL) - id={}", id);

        return EditAdminNewsResponseDto.builder()
                .message("최신소식 수정 성공")
                .build();
    }

    /**
     * 최신소식 삭제
     */
    @Transactional
    public DeleteAdminNewsResponseDto deleteMainNews(DeleteAdminNewsRequestDto request) {
        log.info("최신소식 삭제 - id={}", request.getId());

        // 1. News 조회
        News news = newsRepository.findById(request.getId())
                .orElseThrow(() -> new ClientBadRequestException(
                        "News not found with id: " + request.getId()
                ));

        // 2. S3에서 이미지 삭제
        s3Service.deleteFile(news.getImage());

        // 3. DB에서 삭제
        newsRepository.delete(news);

        log.info("최신소식 삭제 완료 - id={}", request.getId());

        return DeleteAdminNewsResponseDto.builder()
                .message("최신소식 삭제 성공")
                .build();
    }

    /** 수동 뉴스를 교체하되 기존 수집 뉴스와 동일 링크는 다시 생성하지 않는다. */
    @Transactional
    public void bulkCreate(BulkCreateNewsCommand command) {
        newsRepository.deleteByInstagramPostIdIsNull();
        List<News> newsList = command.news().stream()
                .filter(n -> !isImportedInstagramLink(n.link()))
                .map(n -> News.builder()
                        .title(n.title())
                        .link(n.link())
                        .image(n.imageUrl())
                        .build())
                .toList();
        newsRepository.saveAll(newsList);
        log.info("News 일괄 생성 완료 - count={}", newsList.size());
    }

    /** 끝 슬래시 유무가 다른 동일 게시물도 기존 수집 뉴스로 취급한다. */
    private boolean isImportedInstagramLink(String link) {
        if (link == null) return false;
        String withoutTrailingSlash = link.replaceAll("/+$", "");
        return newsRepository.existsByInstagramPostIdIsNotNullAndLinkIn(
                List.of(withoutTrailingSlash, withoutTrailingSlash + "/"));
    }

    // ===== Query 메서드 =====

    /**
     * 최신소식 단건 조회
     */
    @Transactional(readOnly = true)
    public GetAdminNewsResponseDto getMainNews(GetAdminNewsRequestDto request) {
        log.debug("최신소식 조회 - id={}", request.getId());

        News news = newsRepository.findById(request.getId())
                .orElseThrow(() -> new ClientBadRequestException(
                        "News not found with id: " + request.getId()
                ));

        return GetAdminNewsResponseDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .link(news.getLink())
                .image(news.getImage())
                .build();
    }

    @Transactional(readOnly = true)
    public List<News> findAll() {
        return newsRepository.findAllByPublishedAtDesc();
    }

}
