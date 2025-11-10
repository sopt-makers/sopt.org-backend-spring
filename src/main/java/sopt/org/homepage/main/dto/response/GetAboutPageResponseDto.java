package sopt.org.homepage.main.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.main.dto.response.main.branding.GetMainBrandingColorResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.core.GetMainCoreValueResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.curriculum.GetMainPartCurriculumResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.member.GetMainMemberResponseRecordDto;

import java.util.List;

@Validated
@Schema(description = "소개 페이지 데이터 조회하기")
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetAboutPageResponseDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    private int generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    private String name;

    @Schema(description = "헤더 이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://header.png")
    private String headerImage;

    private GetMainBrandingColorResponseRecordDto brandingColor;
    private List<GetMainCoreValueResponseRecordDto> coreValue;
    private List<GetMainPartCurriculumResponseRecordDto> partCurriculum;
    private List<GetMainMemberResponseRecordDto> member;

    private GetAboutSoptResponseDto.ActivitiesRecords activitiesRecords;
}


