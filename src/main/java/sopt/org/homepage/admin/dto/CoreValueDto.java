package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "핵심 가치")
@Getter
@NoArgsConstructor
public class CoreValueDto {
    @Schema(description = "핵심 가치", example = "용기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핵심 가치를 입력해주세요")
    private String value;

    @Schema(description = "핵심 가치 설명", example = "새로운 도전을 위해 과감히 용기내는 사람", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핵심 가치 설명을 입력해주세요")
    private String description;
}
