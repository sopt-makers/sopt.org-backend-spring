package sopt.org.homepage.infrastructure.external.playground;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.global.common.response.RawResponse;
import sopt.org.homepage.global.config.AuthConfig;
import sopt.org.homepage.global.exception.BusinessLogicException;
import sopt.org.homepage.infrastructure.external.playground.dto.request.ScrapLinkRequestDto;
import sopt.org.homepage.infrastructure.external.playground.dto.response.ScrapLinkResponseDto;
import sopt.org.homepage.infrastructure.external.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.infrastructure.external.scrap.dto.ScrapArticleDto;
import sopt.org.homepage.infrastructure.external.scrap.service.ScraperService;

// Playground 서버가 호출하는 API라 응답 형식을 바꾸지 않는다
@RawResponse
@RestController
@RequiredArgsConstructor
@RequestMapping("internal")
@Tag(name = "Internal")
public class InternalController {

    private final ScraperService scraperService;
    private final AuthConfig authConfig;

    @PostMapping("scrap")
    @Operation(summary = "블로그 링크 정보 스크랩하기", description = "Playground 에서 솝티클 정보 스크래핑에 필요한 API 입니다.")
    public ResponseEntity<ScrapLinkResponseDto> scrapLink(
            @Valid @RequestBody ScrapLinkRequestDto dto,
            @RequestHeader("api-key") String apiKey
    ) {
        if (apiKey == null) {
            throw new BusinessLogicException("api-key is required");
        }

        if (!apiKey.equals(authConfig.getApiKey())) {
            throw new BusinessLogicException("api-key is invalid");
        }

        CreateScraperResponseDto scrapResult = scraperService.scrap(
                new ScrapArticleDto(dto.getLink())
        );

        return ResponseEntity.ok(
                ScrapLinkResponseDto.builder()
                        .thumbnailUrl(scrapResult.getThumbnailUrl())
                        .title(scrapResult.getTitle())
                        .description(scrapResult.getDescription())
                        .url(scrapResult.getArticleUrl())
                        .build()
        );
    }
}

