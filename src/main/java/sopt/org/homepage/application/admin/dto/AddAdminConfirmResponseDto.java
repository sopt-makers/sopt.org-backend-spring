package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "어드민 메인정보 파일 업로드 확인")
@Getter
@Builder
@RequiredArgsConstructor
public class AddAdminConfirmResponseDto {
    @Schema(description = "성공 메세지", requiredMode = Schema.RequiredMode.REQUIRED, example = "success")
    private final String message;
}


