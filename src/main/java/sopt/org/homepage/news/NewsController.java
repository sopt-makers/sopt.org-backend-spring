package sopt.org.homepage.news;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.global.common.constants.SecurityConstants;
import sopt.org.homepage.news.dto.AddAdminNewsRequestDto;
import sopt.org.homepage.news.dto.AddAdminNewsResponseDto;
import sopt.org.homepage.news.dto.AddAdminNewsV2RequestDto;
import sopt.org.homepage.news.dto.DeleteAdminNewsRequestDto;
import sopt.org.homepage.news.dto.DeleteAdminNewsResponseDto;
import sopt.org.homepage.news.dto.GetAdminNewsRequestDto;
import sopt.org.homepage.news.dto.GetAdminNewsResponseDto;

/**
 * NewsController
 * <p>
 * 최신소식 관리 API
 * <p>
 * 엔드포인트: /admin/news (기존과 동일)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/news")
@Tag(name = "Admin - News", description = "뉴스 관리 API")
@SecurityRequirement(name = SecurityConstants.SCHEME_NAME)
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "최신소식 추가", description = "최신소식을 추가합니다")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AddAdminNewsResponseDto> addMainNews(
            @ModelAttribute @Valid AddAdminNewsRequestDto request
    ) {
        AddAdminNewsResponseDto result = newsService.addMainNews(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "최신소식 추가 (Presigned URL)",
            description = "람다 전용"
    )
    @PostMapping("/v2")
    public ResponseEntity<AddAdminNewsResponseDto> addMainNewsV2(
            @RequestBody @Valid AddAdminNewsV2RequestDto request
    ) {
        AddAdminNewsResponseDto result = newsService.addMainNewsV2(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "최신소식 삭제", description = "최신소식을 삭제합니다")
    @PostMapping(value = "/delete")
    public ResponseEntity<DeleteAdminNewsResponseDto> deleteMainNews(
            @RequestBody @Valid DeleteAdminNewsRequestDto request
    ) {
        DeleteAdminNewsResponseDto result = newsService.deleteMainNews(request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "최신소식 조회", description = "최신소식을 조회합니다")
    @GetMapping(value = "/news")
    public ResponseEntity<GetAdminNewsResponseDto> getMainNews(
            @ParameterObject @ModelAttribute GetAdminNewsRequestDto request
    ) {
        GetAdminNewsResponseDto result = newsService.getMainNews(request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
