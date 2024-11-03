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
import sopt.org.homepage.admin.dto.response.AddMainResponseDto;
import sopt.org.homepage.admin.dto.response.GetMainNewsResponseDto;
import sopt.org.homepage.common.constants.SecurityConstants;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
@Tag(name = "Admin")
@SecurityRequirement(name = SecurityConstants.SCHEME_NAME)
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "어드민 메인 데이터 배포", description = "어드민 메인 데이터를 배포합니다")
    @PostMapping("")
    public ResponseEntity<AddMainResponseDto> addMain (
            @RequestBody @Valid AddMainRequestDto addMainRequestDto
    ) {
        AddMainResponseDto result = adminService.addMainData(addMainRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "어드민 메인 데이터 배포 확인", description = "어드민 메인 데이터 배포를 확인합니다")
    @PostMapping("confirm")
    public ResponseEntity<String> addMainConfirm (
            @RequestBody @Valid AddMainConfirmRequestDto addMainRequestDto
    ) {
        adminService.addMainDataConfirm(addMainRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
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
        adminService.addMainNews(addMainNewsRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("최신소식을 추가 성공");
    }

    @Operation(summary = "최신소식 삭제", description = "최신소식을 삭제합니다")
    @DeleteMapping(value = "/news")
    public ResponseEntity<String> deleteMainNews(
            @RequestBody @Valid DeleteMainNewsRequestDto deleteMainNewsRequestDto
    ) {
        adminService.deleteMainNews(deleteMainNewsRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @Operation(summary = "최신소식 조회", description = "최신소식을 조회합니다")
    @GetMapping(value = "/news")
    public ResponseEntity<GetMainNewsResponseDto> getMainNews(
            @ParameterObject @ModelAttribute GetMainNewsRequestDto getMainNewsRequestDto
    ) {
        GetMainNewsResponseDto result = adminService.getMainNews(getMainNewsRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // TODO. 각 API 서비스 로직 + 이미지 업로드 확인 API + S3 이미지 업로드 + S3 PresignedUrl 생성



}


