package sopt.org.homepage.admin.dto.request.main.recruit.curriculum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.main.entity.sub.RecruitPartCurriculumEntity;

import java.util.List;

@Schema(description = "모집 파트 커리큘럼")
@Getter
@NoArgsConstructor
public class AddAdminRecruitPartCurriculumRequestDto {
    @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트명은 필수입니다")
    private String part;

    @Schema(description = "소개글 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "소개글은 필수입니다")
    @Valid
    private AddAdminIntroductionRequestDto introduction;

    public RecruitPartCurriculumEntity toEntity() {
        return RecruitPartCurriculumEntity.builder()
                .part(this.part)
                .introduction(this.introduction.toEntity())
                .build();
    }

    public static List<RecruitPartCurriculumEntity> toEntityList(List<AddAdminRecruitPartCurriculumRequestDto> dtos) {
        return dtos.stream()
                .map(AddAdminRecruitPartCurriculumRequestDto::toEntity)
                .toList();
    }
}

