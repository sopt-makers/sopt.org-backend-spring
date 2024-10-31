package sopt.org.homepage.admin;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.common.constants.SecurityConstants;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
@Tag(name = "Admin")
@SecurityRequirement(name = SecurityConstants.SCHEME_NAME)
public class AdminController {
    private final AdminService adminService;

    @PostMapping("")
    public ResponseEntity<String> addMain (
//            @ParameterObject @ModelAttribute String registerNotificationRequestDto
    ) {
//        RegisterNotificationResponseDto result = notificationService.registerNotification(registerNotificationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("result");
    }

//    @GetMapping("")
//    public ResponseEntity<GetNotificationListResponseDto> getMain (
//            @ParameterObject @ModelAttribute GetNotificationListRequestDto getNotificationListRequestDto
//    ) {
//        GetNotificationListResponseDto result = notificationService.getNotificationEmailList(getNotificationListRequestDto.getGeneration());
//        return ResponseEntity.status(HttpStatus.OK).body(result);
//    }
}


