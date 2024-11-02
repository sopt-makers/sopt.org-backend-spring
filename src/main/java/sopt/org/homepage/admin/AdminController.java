package sopt.org.homepage.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.admin.dto.request.*;
import sopt.org.homepage.admin.dto.response.GetMainNewsResponseDto;
import sopt.org.homepage.common.constants.SecurityConstants;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
@Tag(name = "Admin")
@SecurityRequirement(name = SecurityConstants.SCHEME_NAME)
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "어드민 메인 정보 추가", description = "어드민 메인 정보를 추가합니다")
    @PostMapping("")
    public ResponseEntity<String> addMain (
            @RequestBody @Valid AddMainRequestDto addMainRequestDto
    ) {
        String result = adminService.addMainData(addMainRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @Operation(summary = "어드민 메인 정보 조회", description = "어드민 메인 정보를 조회합니다")
    @GetMapping("")
    public ResponseEntity<String> getMain ( // TODO. GetMainResponseDto 생성
            @ParameterObject @ModelAttribute GetMainRequestDto getMainRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @Operation(summary = "최신소식 추가", description = "최신소식을 추가합니다")
    @PostMapping(value = "/news", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addMainNews(
            @ModelAttribute @Valid AddMainNewsRequestDto addMainNewsRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @Operation(summary = "최신소식 삭제", description = "최신소식을 삭제합니다")
    @DeleteMapping(value = "/news")
    public ResponseEntity<String> deleteMainNews(
            @RequestBody @Valid DeleteMainNewsRequestDto deleteMainNewsRequestDto
    ) {

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @Operation(summary = "최신소식 조회", description = "최신소식을 조회합니다")
    @GetMapping(value = "/news")
    public ResponseEntity<GetMainNewsResponseDto> getMainNews(
            @ParameterObject @ModelAttribute GetMainNewsRequestDto getMainNewsRequestDto
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // TODO. 각 API 서비스 로직 + 이미지 업로드 확인 API + S3 이미지 업로드 + S3 PresignedUrl 생성



}


