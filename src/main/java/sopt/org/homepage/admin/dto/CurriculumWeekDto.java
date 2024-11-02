package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "주차별 커리큘럼 정보")
@Getter
@NoArgsConstructor
public class CurriculumWeekDto {
    @Schema(description = "주차", example = "1", minimum = "1", maximum = "8", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "주차는 1 이상이어야 합니다")
    @Max(value = 8, message = "주차는 8 이하여야 합니다")
    private Integer week;

    @Schema(description = "커리큘럼 설명", example = "Android 기초 학습", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "커리큘럼 설명을 입력해주세요")
    private String description;
}
