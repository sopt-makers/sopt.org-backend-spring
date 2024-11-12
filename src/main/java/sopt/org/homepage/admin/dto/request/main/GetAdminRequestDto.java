package sopt.org.homepage.admin.dto.request.main;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "어드민 데이터 조회하기")
@Getter
@RequiredArgsConstructor
public class GetAdminRequestDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "35")
    @NotNull(message = "generation must not be null")
    private final int generation;
}


