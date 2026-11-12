package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.application.admin.dto.response.main.branding.GetAdminBrandingColorResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.core.GetAdminCoreValueResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.curriculum.GetAdminPartCurriculumResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.introduction.GetAdminPartIntroductionResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.member.GetAdminMemberResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.news.GetAdminLatestNewsResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.curriculum.GetAdminRecruitPartCurriculumResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.activityschedule.GetAdminActivityScheduleResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.review.GetAdminReviewResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.question.GetAdminFAQResponseRecordDto;
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
    private List<GetAdminPartIntroductionResponseRecordDto> partIntroduction;
    private List<GetAdminLatestNewsResponseRecordDto> latestNews;

    @Schema(description = "헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://header.png")
    private String headerImage;

    @Schema(description = "홈 헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://header.png")
    private String homeHeaderImage;

    private List<GetAdminCoreValueResponseRecordDto> coreValue;
    private List<GetAdminPartCurriculumResponseRecordDto> partCurriculum;
    private List<GetAdminMemberResponseRecordDto> member;

    @Schema(description = "지원하기 헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://recruit_header.png")
    private String recruitHeaderImage;

    private List<GetAdminRecruitPartCurriculumResponseRecordDto> recruitPartCurriculum;

    @Schema(description = "파트별 자주 묻는 질문 목록 (레거시). 리브랜딩 이후에는 카테고리별 자주 묻는 질문 목록(faq)을 사용해주세요")
    private List<GetAdminRecruitQuestionResponseRecordDto> recruitQuestion;

    @Schema(description = "카테고리별 자주 묻는 질문 목록")
    private List<GetAdminFAQResponseRecordDto> faq;

    private List<GetAdminReviewResponseRecordDto> review;
    private List<GetAdminActivityScheduleResponseRecordDto> activitySchedule;
}


