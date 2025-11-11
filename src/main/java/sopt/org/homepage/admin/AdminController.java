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
import sopt.org.homepage.news.controller.dto.request.AddAdminConfirmRequestDto;
import sopt.org.homepage.news.controller.dto.request.AddAdminRequestDto;
import sopt.org.homepage.news.controller.dto.request.GetAdminRequestDto;
import sopt.org.homepage.news.controller.dto.request.AddAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.request.DeleteAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.request.GetAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminConfirmResponseDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminResponseDto;
import sopt.org.homepage.news.controller.dto.response.GetAdminResponseDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminNewsResponseDto;
import sopt.org.homepage.news.controller.dto.response.DeleteAdminNewsResponseDto;
import sopt.org.homepage.news.controller.dto.response.GetAdminNewsResponseDto;
import sopt.org.homepage.admin.service.AdminService;
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
    public ResponseEntity<AddAdminResponseDto> addMain (
            @RequestBody @Valid AddAdminRequestDto addAdminRequestDto
    ) {
        AddAdminResponseDto result = adminService.addMainData(addAdminRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "어드민 메인 데이터 배포 확인", description = "어드민 메인 데이터 배포를 확인합니다")
    @PostMapping("confirm")
    public ResponseEntity<AddAdminConfirmResponseDto> addMainConfirm (
            @RequestBody @Valid AddAdminConfirmRequestDto addMainRequestDto
    ) {
        AddAdminConfirmResponseDto result = adminService.addMainDataConfirm(addMainRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "어드민 메인 데이터 조회", description = "어드민 메인 데이터를 조회합니다")
    @GetMapping("")
    public ResponseEntity<GetAdminResponseDto> getMain (
            @ParameterObject @ModelAttribute GetAdminRequestDto getAdminRequestDto
    ) {
        GetAdminResponseDto result = adminService.getMain(getAdminRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}


