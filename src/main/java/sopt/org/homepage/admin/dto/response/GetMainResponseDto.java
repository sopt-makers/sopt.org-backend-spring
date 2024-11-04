package sopt.org.homepage.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.admin.dto.*;
import sopt.org.homepage.admin.dto.response.record.*;

import java.util.List;

@Validated
@Schema(description = "어드민 데이터 조회하기")
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetMainResponseDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    private int generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    private String name;

    private List<GetMainRecruitScheduleResponseRecordDto> recruitSchedule;
    private GetMainBrandingColorResponseRecordDto brandingColor;
    private GetMainMainButtonResponseRecordDto mainButton;
    private List<GetMainPartIntroductionResponseRecordDto> partIntroduction;
    private List<GetMainLatestNewsResponseRecordDto> latestNews;

    @Schema(description = "헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://header.png")
    private String headerImage;

    private List<GetMainCoreValueResponseRecordDto> coreValue;
    private List<GetMainPartCurriculumResponseRecordDto> partCurriculum;
    private List<GetMainMemberResponseRecordDto> member;

    @Schema(description = "지원하기 헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://recruit_header.png")
    private String recruitHeaderImage;

    private List<GetMainRecruitPartCurriculumResponseRecordDto> recruitPartCurriculum;
    private List<GetMainRecruitQuestionResponseRecordDto> recruitQuestion;
}


