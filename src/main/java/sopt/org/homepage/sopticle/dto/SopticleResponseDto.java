package sopt.org.homepage.sopticle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import sopt.org.homepage.common.type.Part;

@Schema(description = "Sopticle 응답")
@Getter
@Builder
public class SopticleResponseDto {
    @Schema(description = "Sopticle Id")
    private final Long id;

    @Schema(description = "활동 파트")
    private final Part part;

    @Schema(description = "기수")
    private final Integer generation;

    @Schema(description = "솝티클 썸네일 이미지")
    private final String thumbnailUrl;

    @Schema(description = "솝티클 제목")
    private final String title;

    @Schema(description = "솝티클 설명")
    private final String description;

    @Schema(description = "작성자 이름")
    private final String author;

    @Schema(description = "작성자 프로필 이미지")
    private final String authorProfileImageUrl;

    @Schema(description = "솝티클 리다이렉트 주소")
    private final String url;

    @Schema(description = "솝티클 업로드 날짜")
    private final LocalDateTime uploadedAt;

    @Schema(description = "좋아요 수")
    private final Integer likeCount;

    @Schema(description = "좋아요 여부")
    private final Boolean liked;
}