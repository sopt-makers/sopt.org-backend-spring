package sopt.org.homepage.admin.dto.request.news;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "최신소식 조회하기")
@Getter
@RequiredArgsConstructor
public class GetAdminNewsRequestDto {
    @Schema(description = "최신소식 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "id must not be null")
    private final int id;
}


