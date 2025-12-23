package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.application.admin.dto.request.main.branding.AddAdminBrandingColorRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.button.AddAdminMainButtonRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.core.AddAdminCoreValueRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.curriculum.AddAdminPartCurriculumRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.introduction.AddAdminPartIntroductionRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.member.AddAdminMemberRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.curriculum.AddAdminRecruitPartCurriculumRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.question.AddAdminRecruitQuestionRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.schedule.AddAdminRecruitScheduleRequestDto;

@Validated
@Schema(description = "어드민 배포하기")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddAdminRequestDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    @NotEmpty(message = "generation must not be empty")
    @Positive(message = "generation must be a positive number")
    private int generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    @NotEmpty(message = "name must not be empty")
    private String name;

    private List<AddAdminRecruitScheduleRequestDto> recruitSchedule;
    private AddAdminBrandingColorRequestDto brandingColor;
    private AddAdminMainButtonRequestDto mainButton;
    private List<AddAdminPartIntroductionRequestDto> partIntroduction;

    @Schema(description = "헤더 이미지 파일명", requiredMode = Schema.RequiredMode.REQUIRED, example = "header.png")
    @NotEmpty(message = "headerImageFileName must not be empty")
    private String headerImageFileName;

    private List<AddAdminCoreValueRequestDto> coreValue;
    private List<AddAdminPartCurriculumRequestDto> partCurriculum;
    private List<AddAdminMemberRequestDto> member;

    @Schema(description = "지원하기 헤더 이미지 파일명", requiredMode = Schema.RequiredMode.REQUIRED, example = "recruit_header.png")
    @NotEmpty(message = "recruitHeaderImageFileName must not be empty")
    private String recruitHeaderImageFileName;

    private List<AddAdminRecruitPartCurriculumRequestDto> recruitPartCurriculum;
    private List<AddAdminRecruitQuestionRequestDto> recruitQuestion;
}


