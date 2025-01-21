package sopt.org.homepage.main.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.main.dto.response.main.branding.GetMainBrandingColorResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.curriculum.GetMainRecruitPartCurriculumResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.question.GetMainRecruitQuestionResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.schedule.GetMainRecruitScheduleResponseRecordDto;

import java.util.List;

@Validated
@Schema(description = "지원하기 페이지 데이터 조회하기")
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetRecruitingPageResponseDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    private int generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    private String name;

    @Schema(description = "지원하기 헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://recruit_header.png")
    private String recruitHeaderImage;

    private GetMainBrandingColorResponseRecordDto brandingColor;
    private List<GetMainRecruitScheduleResponseRecordDto> recruitSchedule;
    private List<GetMainRecruitPartCurriculumResponseRecordDto> recruitPartCurriculum;
    private List<GetMainRecruitQuestionResponseRecordDto> recruitQuestion;
}


