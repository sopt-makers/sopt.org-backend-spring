package sopt.org.homepage.aboutsopt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "AboutSopt 상세 정보")
public record AboutSoptResponseDto(
        Long id,
        boolean isPublished,
        String title,
        String bannerImage,
        String coreDescription,
        String planCurriculum,
        String designCurriculum,
        String androidCurriculum,
        String iosCurriculum,
        String webCurriculum,
        String serverCurriculum,
        List<CoreValueResponseDto> coreValues
) {
    @Builder
    public AboutSoptResponseDto {}
}