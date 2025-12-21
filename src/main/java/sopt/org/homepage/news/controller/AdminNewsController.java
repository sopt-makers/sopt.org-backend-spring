package sopt.org.homepage.news.controller;

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
import sopt.org.homepage.news.controller.dto.request.AddAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.request.AddAdminNewsV2RequestDto;
import sopt.org.homepage.news.controller.dto.request.DeleteAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.request.GetAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminNewsResponseDto;
import sopt.org.homepage.news.controller.dto.response.DeleteAdminNewsResponseDto;
import sopt.org.homepage.news.controller.dto.response.GetAdminNewsResponseDto;
import sopt.org.homepage.news.service.NewsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/news")
@Tag(name = "Admin - News", description = "뉴스 관리 API")
@SecurityRequirement(name = SecurityConstants.SCHEME_NAME)
public class AdminNewsController {
    private final NewsService newsService;

    @Operation(summary = "최신소식 추가", description = "최신소식을 추가합니다")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AddAdminNewsResponseDto> addMainNews(
            @ModelAttribute @Valid AddAdminNewsRequestDto addAdminNewsRequestDto
    ) {
        AddAdminNewsResponseDto result = newsService.addMainNews(addAdminNewsRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "최신소식 추가 (Presigned URL)",
            description = """
                    **Lambda 환경용 API입니다.**
                    
                    **사용 흐름:**
                    1. `POST /s3/presigned-url` 호출 → presignedUrl, fileUrl 수신
                    2. presignedUrl로 PUT 요청하여 이미지 직접 업로드
                    3. 이 API 호출 시 fileUrl을 imageUrl로 전달
                    """
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
            @RequestBody @Valid DeleteAdminNewsRequestDto deleteAdminNewsRequestDto
    ) {
        DeleteAdminNewsResponseDto result = newsService.deleteMainNews(deleteAdminNewsRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "최신소식 조회", description = "최신소식을 조회합니다")
    @GetMapping(value = "/news")
    public ResponseEntity<GetAdminNewsResponseDto> getMainNews(
            @ParameterObject @ModelAttribute GetAdminNewsRequestDto getAdminNewsRequestDto
    ) {
        GetAdminNewsResponseDto result = newsService.getMainNews(getAdminNewsRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
