package sopt.org.homepage.admin.dto.request.main.curriculum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "파트별 커리큘럼")
@Getter
@NoArgsConstructor
public class AddAdminPartCurriculumRequestDto {
    @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트명을 입력해주세요")
    private String part;

    @Schema(description = "주차별 커리큘럼 (배열 순서 = 주차 순서)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "커리큘럼을 입력해주세요")
    private List<String> curriculums;

}
