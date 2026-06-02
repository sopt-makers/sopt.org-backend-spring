package sopt.org.homepage.application.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.application.admin.dto.AddAdminAboutRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminAboutResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminCommonRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminCommonResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminConfirmRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminConfirmResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminHomeRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminHomeResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminRecruitRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminRecruitResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminResponseDto;
import sopt.org.homepage.application.admin.dto.GetAdminRequestDto;
import sopt.org.homepage.application.admin.dto.GetAdminResponseDto;
import sopt.org.homepage.application.admin.service.AdminService;
import sopt.org.homepage.global.common.constants.SecurityConstants;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
@Tag(name = "Admin")
@SecurityRequirement(name = SecurityConstants.SCHEME_NAME)
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "어드민 메인 데이터 조회", description = "어드민 메인 데이터를 조회합니다")
    @GetMapping("")
    public ResponseEntity<GetAdminResponseDto> getMain(
            @ParameterObject @ModelAttribute GetAdminRequestDto getAdminRequestDto
    ) {
        GetAdminResponseDto result = adminService.getMain(getAdminRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // ===== 탭별 배포 - 공통 =====

    @Operation(summary = "공통 탭 배포", description = "기수 정보, 브랜딩 컬러, 메인 버튼, 모집 일정을 캐시에 저장합니다. 이미지가 없으므로 응답 즉시 2단계(confirm)를 호출해도 됩니다.")
    @PostMapping("common")
    public ResponseEntity<AddAdminCommonResponseDto> addCommon(
            @RequestBody @Valid AddAdminCommonRequestDto request
    ) {
        AddAdminCommonResponseDto result = adminService.addCommonData(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "공통 탭 배포 확정", description = "캐시에 저장된 공통 탭 데이터를 DB에 반영합니다. 공통 탭은 반드시 다른 탭보다 먼저 배포해야 합니다.")
    @PostMapping("common/confirm")
    public ResponseEntity<AddAdminConfirmResponseDto> addCommonConfirm(
            @RequestBody @Valid AddAdminConfirmRequestDto request
    ) {
        AddAdminConfirmResponseDto result = adminService.addCommonDataConfirm(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ===== 탭별 배포 - 홈 =====

    @Operation(summary = "홈 탭 배포", description = "홈 헤더 이미지 Presigned URL을 발급하고 리뷰/최신소식을 캐시에 저장합니다. 응답의 Presigned URL로 이미지 업로드 후 2단계를 호출하세요.")
    @PostMapping("home")
    public ResponseEntity<AddAdminHomeResponseDto> addHome(
            @RequestBody @Valid AddAdminHomeRequestDto request
    ) {
        AddAdminHomeResponseDto result = adminService.addHomeData(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "홈 탭 배포 확정", description = "이미지 업로드 완료 후 캐시 데이터를 DB에 반영합니다. 공통 탭 배포 이후에 호출해야 합니다.")
    @PostMapping("home/confirm")
    public ResponseEntity<AddAdminConfirmResponseDto> addHomeConfirm(
            @RequestBody @Valid AddAdminConfirmRequestDto request
    ) {
        AddAdminConfirmResponseDto result = adminService.addHomeDataConfirm(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ===== 탭별 배포 - 소개 =====

    @Operation(summary = "소개 탭 배포", description = "소개 헤더/핵심가치/임원진 이미지 Presigned URL을 발급하고 전체 일정을 캐시에 저장합니다. 응답의 Presigned URL로 이미지 업로드 후 2단계를 호출하세요.")
    @PostMapping("about")
    public ResponseEntity<AddAdminAboutResponseDto> addAbout(
            @RequestBody @Valid AddAdminAboutRequestDto request
    ) {
        AddAdminAboutResponseDto result = adminService.addAboutData(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "소개 탭 배포 확정", description = "이미지 업로드 완료 후 캐시 데이터를 DB에 반영합니다. 공통 탭 배포 이후에 호출해야 합니다.")
    @PostMapping("about/confirm")
    public ResponseEntity<AddAdminConfirmResponseDto> addAboutConfirm(
            @RequestBody @Valid AddAdminConfirmRequestDto request
    ) {
        AddAdminConfirmResponseDto result = adminService.addAboutDataConfirm(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ===== 탭별 배포 - 모집안내 =====

    @Operation(summary = "모집안내 탭 배포", description = "모집 헤더 이미지 Presigned URL을 발급하고 파트별 소개/커리큘럼/FAQ를 캐시에 저장합니다. 응답의 Presigned URL로 이미지 업로드 후 2단계를 호출하세요.")
    @PostMapping("recruit")
    public ResponseEntity<AddAdminRecruitResponseDto> addRecruit(
            @RequestBody @Valid AddAdminRecruitRequestDto request
    ) {
        AddAdminRecruitResponseDto result = adminService.addRecruitData(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "모집안내 탭 배포 확정", description = "이미지 업로드 완료 후 캐시 데이터를 DB에 반영합니다. 공통 탭 배포 이후에 호출해야 합니다.")
    @PostMapping("recruit/confirm")
    public ResponseEntity<AddAdminConfirmResponseDto> addRecruitConfirm(
            @RequestBody @Valid AddAdminConfirmRequestDto request
    ) {
        AddAdminConfirmResponseDto result = adminService.addRecruitDataConfirm(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}


