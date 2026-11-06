package sopt.org.homepage.news;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${news.display-limit:0}")
    private int newsDisplayLimit;

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

        if (news.getInstagramPostId() != null) {
            news.hide();
        } else {
            s3Service.deleteFile(news.getImage());
            newsRepository.delete(news);
        }

        log.info("최신소식 삭제 완료 - id={}", request.getId());

        return DeleteAdminNewsResponseDto.builder()
                .message("최신소식 삭제 성공")
                .build();
    }

    /** 동일 링크의 기존 뉴스를 갱신해 게시일을 보존하고 빠진 수동 뉴스만 삭제한다. */
    @Transactional
    public void bulkCreate(BulkCreateNewsCommand command) {
        List<News> existing = newsRepository.findAll();
        java.util.Set<Integer> retained = new java.util.HashSet<>();
        for (var item : command.news()) {
            News match = existing.stream()
                    .filter(n -> canonicalLink(n.getLink()).equals(canonicalLink(item.link())))
                    .findFirst().orElse(null);
            if (match != null) {
                retained.add(match.getId());
                if (!match.isHidden()) match.update(item.imageUrl(), item.title(), item.link());
            } else {
                newsRepository.save(News.builder().title(item.title()).link(item.link()).image(item.imageUrl()).build());
            }
        }
        existing.stream().filter(n -> n.getInstagramPostId() == null && !retained.contains(n.getId()))
                .forEach(newsRepository::delete);
    }

    private String canonicalLink(String link) {
        if (link == null) return "";
        var matcher = java.util.regex.Pattern.compile(
                "https?://(?:www[.])?instagram[.]com/(?:p|reel|tv)/([A-Za-z0-9_-]+)(?:[/?#].*)?")
                .matcher(link);
        return matcher.matches() ? "instagram:" + matcher.group(1) : link.replaceAll("/+$", "");
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

    /** 홈페이지에는 원본 게시일 최신순으로 설정된 개수만 노출한다. */
    @Transactional(readOnly = true)
    public List<News> findLatestForHomepage() {
        return newsDisplayLimit > 0 ? newsRepository.findLatest(newsDisplayLimit)
                : newsRepository.findAllByPublishedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<News> findAll() {
        return newsRepository.findAllByPublishedAtDesc();
    }

}
