package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.admin.dao.RecruitPartCurriculumDao;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "모집 파트 커리큘럼")
@Getter
@NoArgsConstructor
public class RecruitPartCurriculumDto {
    @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트명은 필수입니다")
    private String part;

    @Schema(description = "소개글 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "소개글은 필수입니다")
    @Valid
    private IntroductionDto introduction;

    public RecruitPartCurriculumDao toDao() {
        return RecruitPartCurriculumDao.builder()
                .part(this.part)
                .introduction(this.introduction.toDao())
                .build();
    }

    public static List<RecruitPartCurriculumDao> toDaoList(List<RecruitPartCurriculumDto> dtos) {
        return dtos.stream()
                .map(RecruitPartCurriculumDto::toDao)
                .collect(Collectors.toList());
    }
}

