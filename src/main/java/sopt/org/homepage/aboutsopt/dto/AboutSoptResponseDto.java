package sopt.org.homepage.aboutsopt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "AboutSopt 상세 정보")
public record AboutSoptResponseDto(
        @Schema(description = "기수", nullable = false, example = "33")
        Long id,

        @Schema(description = "배포 여부", nullable = false, example = "true")
        boolean isPublished,

        @Schema(description = "AboutTab 상단 타이틀", nullable = false, example = "SOPT 33기")
        String title,

        @Schema(description = "배너 이미지 주소", nullable = false, example = "https://example.com/banner.jpg")
        String bannerImage,

        @Schema(description = "핵심가치 설명", nullable = false, example = "SOPT는 혁신과 협업을 추구합니다.")
        String coreDescription,

        @Schema(description = "기획파트 커리큘럼", nullable = false, example = "기획 과정 내용")
        String planCurriculum,

        @Schema(description = "디자인파트 커리큘럼", nullable = false, example = "디자인 과정 내용")
        String designCurriculum,

        @Schema(description = "안드로이드 파트 커리큘럼", nullable = false, example = "안드로이드 과정 내용")
        String androidCurriculum,

        @Schema(description = "iOS 파트 커리큘럼", nullable = false, example = "iOS 과정 내용")
        String iosCurriculum,

        @Schema(description = "웹 파트 커리큘럼", nullable = false, example = "웹 과정 내용")
        String webCurriculum,

        @Schema(description = "서버 파트 커리큘럼", nullable = false, example = "서버 과정 내용")
        String serverCurriculum,

        @Schema(description = "코어밸류 리스트", nullable = false)
        List<CoreValueResponseDto> coreValues
) {
    @Builder
    public AboutSoptResponseDto {}
}