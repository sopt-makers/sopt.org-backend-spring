package sopt.org.homepage.soptstory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.common.util.IpAddressUtil;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.scrap.dto.ScrapArticleDto;
import sopt.org.homepage.scrap.service.ScraperService;
import sopt.org.homepage.soptstory.controller.dto.CreateSoptStoryRequest;
import sopt.org.homepage.soptstory.controller.dto.CreateSoptStoryResponse;
import sopt.org.homepage.soptstory.controller.dto.LikeSoptStoryResponse;
import sopt.org.homepage.soptstory.service.command.SoptStoryCommandService;
import sopt.org.homepage.soptstory.service.command.dto.*;

/**
 * SoptStory Command Controller
 *
 * 책임:
 * - SoptStory 생성 API
 * - 좋아요 추가/취소 API
 * - Request/Response 변환
 */
@Tag(name = "SoptStory Command", description = "SoptStory 생성 및 좋아요 API")
@RestController
@RequestMapping("/soptstory/v2")
@RequiredArgsConstructor
public class SoptStoryCommandController {

    private final SoptStoryCommandService soptStoryCommandService;
    private final ScraperService scraperService;
    private final AuthConfig authConfig;

    /**
     * SoptStory 생성
     *
     * 프로세스:
     * 1. API Key 검증
     * 2. URL 스크래핑 (외부 서비스)
     * 3. Command 생성 및 Service 호출
     *
     * @param request 생성 요청 (link)
     * @param apiKey API 키 (헤더)
     * @return 생성된 SoptStory 정보
     */
    @PostMapping
    @Operation(summary = "솝트스토리 생성", description = "솥트스토리를 생성합니다.")
    public ResponseEntity<CreateSoptStoryResponse> createSoptStory(
            @Valid @RequestBody CreateSoptStoryRequest request,
            @RequestHeader("api-key") String apiKey
    ) {
        // 1. API Key 검증
        validateApiKey(apiKey);

        // 2. URL 스크래핑
        CreateScraperResponseDto scrapResult = scraperService.scrap(
                new ScrapArticleDto(request.link())
        );

        // 3. Command 생성
        CreateSoptStoryCommand command = new CreateSoptStoryCommand(
                scrapResult.getTitle(),
                scrapResult.getDescription(),
                scrapResult.getThumbnailUrl(),
                scrapResult.getArticleUrl()
        );

        // 4. Service 호출
        soptStoryCommandService.createSoptStory(command);

        // 5. Response 변환
        CreateSoptStoryResponse response = new CreateSoptStoryResponse(
                scrapResult.getThumbnailUrl(),
                scrapResult.getTitle(),
                scrapResult.getDescription(),
                scrapResult.getArticleUrl()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 좋아요 추가
     *
     * @param id SoptStory ID
     * @param request HTTP 요청 (IP 추출용)
     * @return 생성된 좋아요 정보
     */
    @PostMapping("/{id}/like")
    @Operation(summary = "솝트스토리 좋아요 누르기")
    public ResponseEntity<LikeSoptStoryResponse> likeSoptStory(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        // 1. IP 주소 추출 및 검증
        String ip = extractAndValidateIp(request);

        // 2. Command 생성 및 Service 호출
        LikeSoptStoryCommand command = new LikeSoptStoryCommand(id, ip);
        SoptStoryLikeId likeId = soptStoryCommandService.like(command);

        // 3. Response 변환
        LikeSoptStoryResponse response = new LikeSoptStoryResponse(
                likeId.value(),
                id,
                ip
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 좋아요 취소
     *
     * @param id SoptStory ID
     * @param request HTTP 요청 (IP 추출용)
     * @return 삭제된 좋아요 정보
     */
    @PostMapping("/{id}/unlike")
    @Operation(summary = "솝트스토리 좋아요 취소하기")
    public ResponseEntity<LikeSoptStoryResponse> unlikeSoptStory(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        // 1. IP 주소 추출 및 검증
        String ip = extractAndValidateIp(request);

        // 2. Command 생성 및 Service 호출
        UnlikeSoptStoryCommand command = new UnlikeSoptStoryCommand(id, ip);
        SoptStoryLikeId likeId = soptStoryCommandService.unlike(command);

        // 3. Response 변환
        LikeSoptStoryResponse response = new LikeSoptStoryResponse(
                likeId.value(),
                id,
                ip
        );

        return ResponseEntity.ok(response);
    }

    // ===== Private Helper Methods =====

    /**
     * API Key 검증
     */
    private void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessLogicException("api-key is required");
        }

        if (!apiKey.equals(authConfig.getApiKey())) {
            throw new BusinessLogicException("api-key is invalid");
        }
    }

    /**
     * IP 주소 추출 및 검증
     */
    private String extractAndValidateIp(HttpServletRequest request) {
        String ip = IpAddressUtil.getClientIpAddress(request);

        if (!IpAddressUtil.isValidIpAddress(ip)) {
            throw new BusinessLogicException("Unable to determine client IP address");
        }

        return ip;
    }
}