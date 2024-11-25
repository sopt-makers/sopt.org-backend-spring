package sopt.org.homepage.admin.dto.response.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "최신소식 삭제")
@Getter
@Builder
@RequiredArgsConstructor
public class DeleteAdminNewsResponseDto {
    @Schema(description = "성공 메세지", requiredMode = Schema.RequiredMode.REQUIRED, example = "success")
    private final String message;
}


