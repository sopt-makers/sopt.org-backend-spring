package sopt.org.homepage.news.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "최신 소식 추가")
@Getter
@Builder
@RequiredArgsConstructor
public class AddAdminNewsResponseDto {
    @Schema(description = "성공 메세지", requiredMode = Schema.RequiredMode.REQUIRED, example = "success")
    private final String message;
}


