package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.application.admin.dto.response.main.branding.GetAdminBrandingColorResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.button.GetAdminMainButtonResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.core.GetAdminCoreValueResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.curriculum.GetAdminPartCurriculumResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.introduction.GetAdminPartIntroductionResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.member.GetAdminMemberResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.news.GetAdminLatestNewsResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.curriculum.GetAdminRecruitPartCurriculumResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.question.GetAdminRecruitQuestionResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.schedule.GetAdminRecruitScheduleResponseRecordDto;

@Validated
@Schema(description = "어드민 데이터 조회하기")
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetAdminResponseDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    private int generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    private String name;

    private List<GetAdminRecruitScheduleResponseRecordDto> recruitSchedule;
    private GetAdminBrandingColorResponseRecordDto brandingColor;
    private GetAdminMainButtonResponseRecordDto mainButton;
    private List<GetAdminPartIntroductionResponseRecordDto> partIntroduction;
    private List<GetAdminLatestNewsResponseRecordDto> latestNews;

    @Schema(description = "헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://header.png")
    private String headerImage;

    private List<GetAdminCoreValueResponseRecordDto> coreValue;
    private List<GetAdminPartCurriculumResponseRecordDto> partCurriculum;
    private List<GetAdminMemberResponseRecordDto> member;

    @Schema(description = "지원하기 헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://recruit_header.png")
    private String recruitHeaderImage;

    private List<GetAdminRecruitPartCurriculumResponseRecordDto> recruitPartCurriculum;
    private List<GetAdminRecruitQuestionResponseRecordDto> recruitQuestion;
}


