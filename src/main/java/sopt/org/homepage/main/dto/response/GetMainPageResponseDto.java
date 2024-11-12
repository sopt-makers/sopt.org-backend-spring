package sopt.org.homepage.main.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.main.dto.response.main.branding.GetMainBrandingColorResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.button.GetMainMainButtonResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.introduction.GetMainPartIntroductionResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.news.GetMainLatestNewsResponseRecordDto;

import java.util.List;

@Validated
@Schema(description = "메인 페이지 데이터 조회하기")
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetMainPageResponseDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    private int generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    private String name;

    private GetMainBrandingColorResponseRecordDto brandingColor;
    private GetMainMainButtonResponseRecordDto mainButton;
    private List<GetMainPartIntroductionResponseRecordDto> partIntroduction;
    private List<GetMainLatestNewsResponseRecordDto> latestNews;
}


