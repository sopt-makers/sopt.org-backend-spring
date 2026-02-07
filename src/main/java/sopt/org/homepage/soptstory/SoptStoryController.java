package sopt.org.homepage.soptstory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.global.common.util.IpAddressUtil;
import sopt.org.homepage.global.config.AuthConfig;
import sopt.org.homepage.global.exception.BusinessLogicException;
import sopt.org.homepage.infrastructure.external.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.infrastructure.external.scrap.dto.ScrapArticleDto;
import sopt.org.homepage.infrastructure.external.scrap.service.ScraperService;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.dto.*;

/**
 * SoptStory Controller
 */
@Tag(name = "SoptStory", description = "SoptStory 생성, 좋아요, 조회 API")
@RestController
@RequestMapping("/soptstory")
@RequiredArgsConstructor
public class SoptStoryController {

    private final SoptStoryService soptStoryService;
    private final ScraperService scraperService;
    private final AuthConfig authConfig;

    @PostMapping
    @Operation(summary = "솝트스토리 생성", description = "솝트스토리를 생성합니다.")
    public ResponseEntity<CreateSoptStoryResponse> createSoptStory(
            @Valid @RequestBody CreateSoptStoryRequest request,
            @RequestHeader("api-key") String apiKey
    ) {
        validateApiKey(apiKey);

        CreateScraperResponseDto scrapResult = scraperService.scrap(
                new ScrapArticleDto(request.link())
        );

        soptStoryService.createSoptStory(
                scrapResult.getTitle(),
                scrapResult.getDescription(),
                scrapResult.getThumbnailUrl(),
                scrapResult.getArticleUrl()
        );

        CreateSoptStoryResponse response = new CreateSoptStoryResponse(
                scrapResult.getThumbnailUrl(),
                scrapResult.getTitle(),
                scrapResult.getDescription(),
                scrapResult.getArticleUrl()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "솝트스토리 좋아요 누르기")
    public ResponseEntity<LikeSoptStoryResponse> likeSoptStory(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String ip = extractAndValidateIp(request);
        Long likeId = soptStoryService.like(id, ip);

        return ResponseEntity.ok(new LikeSoptStoryResponse(likeId, id, ip));
    }

    @PostMapping("/{id}/unlike")
    @Operation(summary = "솝트스토리 좋아요 취소하기")
    public ResponseEntity<LikeSoptStoryResponse> unlikeSoptStory(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String ip = extractAndValidateIp(request);
        Long likeId = soptStoryService.unlike(id, ip);

        return ResponseEntity.ok(new LikeSoptStoryResponse(likeId, id, ip));
    }

    @GetMapping
    @Operation(summary = "솝트스토리 리스트 조회(정렬)")
    public ResponseEntity<PaginatedSoptStoryResponse> getSoptStoryList(
            @ParameterObject @Valid @ModelAttribute GetSoptStoryListRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = extractAndValidateIp(httpRequest);

        Page<SoptStory> page = soptStoryService.getSoptStoryList(
                request.sort(), request.pageNo(), request.limit()
        );

        Set<Long> likedIds = soptStoryService.getLikedSoptStoryIds(ip, page.getContent());

        List<SoptStoryResponse> content = page.getContent().stream()
                .map(story -> SoptStoryResponse.from(story, likedIds.contains(story.getId())))
                .toList();

        PaginatedSoptStoryResponse response = PaginatedSoptStoryResponse.of(
                content, (int) page.getTotalElements(), request.limit(), request.pageNo()
        );

        return ResponseEntity.ok(response);
    }

    // ===== Private =====

    private void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessLogicException("api-key is required");
        }
        if (!apiKey.equals(authConfig.getApiKey())) {
            throw new BusinessLogicException("api-key is invalid");
        }
    }

    private String extractAndValidateIp(HttpServletRequest request) {
        String ip = IpAddressUtil.getClientIpAddress(request);
        if (!IpAddressUtil.isValidIpAddress(ip)) {
            throw new BusinessLogicException("Unable to determine client IP address");
        }
        return ip;
    }
}
