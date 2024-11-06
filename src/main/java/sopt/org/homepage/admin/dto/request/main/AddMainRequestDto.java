package sopt.org.homepage.admin.dto.request.main;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.admin.dto.request.main.branding.AddMainBrandingColorDto;
import sopt.org.homepage.admin.dto.request.main.button.AddMainMainButtonDto;
import sopt.org.homepage.admin.dto.request.main.core.AddMainCoreValueDto;
import sopt.org.homepage.admin.dto.request.main.curriculum.AddMainPartCurriculumDto;
import sopt.org.homepage.admin.dto.request.main.introduction.AddMainPartIntroductionDto;
import sopt.org.homepage.admin.dto.request.main.member.AddMainMemberDto;
import sopt.org.homepage.admin.dto.request.main.recruit.curriculum.AddMainRecruitPartCurriculumDto;
import sopt.org.homepage.admin.dto.request.main.recruit.question.AddMainRecruitQuestionDto;
import sopt.org.homepage.admin.dto.request.main.recruit.schedule.AddMainRecruitScheduleDto;

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

    private List<AddMainRecruitScheduleDto> recruitSchedule;
    private AddMainBrandingColorDto brandingColor;
    private AddMainMainButtonDto mainButton;
    private List<AddMainPartIntroductionDto> partIntroduction;

    @Schema(description = "헤더 이미지 파일명", requiredMode = Schema.RequiredMode.REQUIRED, example = "header.png")
    @NotEmpty(message = "headerImageFileName must not be empty")
    private String headerImageFileName;

    private List<AddMainCoreValueDto> coreValue;
    private List<AddMainPartCurriculumDto> partCurriculum;
    private List<AddMainMemberDto> member;

    @Schema(description = "지원하기 헤더 이미지 파일명", requiredMode = Schema.RequiredMode.REQUIRED, example = "recruit_header.png")
    @NotEmpty(message = "recruitHeaderImageFileName must not be empty")
    private String recruitHeaderImageFileName;

    private List<AddMainRecruitPartCurriculumDto> recruitPartCurriculum;
    private List<AddMainRecruitQuestionDto> recruitQuestion;
}


