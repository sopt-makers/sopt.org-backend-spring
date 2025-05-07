package sopt.org.homepage.soptstory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "솝트 스토리 응답")
@Getter
@Builder
public class SoptStoryResponseDto {
    @Schema(description = "SoptStory Id")
    private final Long id;

    @Schema(description = "솝트스토리 썸네일 이미지")
    private final String thumbnailUrl;

    @Schema(description = "솝트스토리 제목")
    private final String title;

    @Schema(description = "솝트스토리 설명")
    private final String description;


    @Schema(description = "솝트스토리 리다이렉트 주소")
    private final String url;

    @Schema(description = "솝트스토리 업로드 날짜")
    private final LocalDateTime uploadedAt;

    @Schema(description = "좋아요 수")
    private final Integer likeCount;

}