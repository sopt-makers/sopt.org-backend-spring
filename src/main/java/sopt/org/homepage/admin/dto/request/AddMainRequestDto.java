package sopt.org.homepage.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.admin.dto.*;

import java.util.List;

@Validated
@Schema(description = "어드민 배포하기")
@Getter
@RequiredArgsConstructor
public class AddMainRequestDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    @NotEmpty(message = "generation must not be empty")
    @Positive(message = "generation must be a positive number")
    private final int generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    @NotEmpty(message = "name must not be empty")
    private final String name;

    private final List<RecruitScheduleDto> recruitSchedule;
    private final BrandingColorDto brandingColor;
    private final MainButtonDto mainButton;
    private final List<PartIntroductionDto> partIntroduction;
    private final List<CoreValueDto> coreValue;
    private final List<PartCurriculumDto> partCurriculum;
    private final List<MemberDto> member;
    private final List<RecruitPartCurriculumDto> recruitPartCurriculum;
    private final List<RecruitQuestionDto> recruitQuestion;
}


