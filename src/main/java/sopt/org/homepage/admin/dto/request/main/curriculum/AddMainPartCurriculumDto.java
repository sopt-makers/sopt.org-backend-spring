package sopt.org.homepage.admin.dto.request.main.curriculum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.admin.entity.sub.PartCurriculumEntity;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "파트별 커리큘럼")
@Getter
@NoArgsConstructor
public class AddMainPartCurriculumDto {
    @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트명을 입력해주세요")
    private String part;

    @Schema(description = "주차별 커리큘럼", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "커리큘럼을 입력해주세요")
    private List<AddMainCurriculumWeekDto> weeks;

    public PartCurriculumEntity toEntity() {
        return PartCurriculumEntity.builder()
                .part(this.part)
                .weeks(AddMainCurriculumWeekDto.toEntityList(this.weeks))
                .build();
    }

    public static List<PartCurriculumEntity> toEntityList(List<AddMainPartCurriculumDto> dtos) {
        return dtos.stream()
                .map(AddMainPartCurriculumDto::toEntity)
                .collect(Collectors.toList());
    }
}
