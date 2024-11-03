package sopt.org.homepage.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.admin.dto.*;

import java.util.List;

@Validated
@Schema(description = "어드민 배포하기")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddMainRequestDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    @NotEmpty(message = "generation must not be empty")
    @Positive(message = "generation must be a positive number")
    private int generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    @NotEmpty(message = "name must not be empty")
    private String name;

    private List<RecruitScheduleDto> recruitSchedule;
    private BrandingColorDto brandingColor;
    private MainButtonDto mainButton;
    private List<PartIntroductionDto> partIntroduction;
    private List<CoreValueDto> coreValue;
    private List<PartCurriculumDto> partCurriculum;
    private List<MemberDto> member;
    private List<RecruitPartCurriculumDto> recruitPartCurriculum;
    private List<RecruitQuestionDto> recruitQuestion;
}


