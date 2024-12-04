package sopt.org.homepage.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import sopt.org.homepage.common.type.Part;

@Schema(description = "리뷰 응답")
@Getter
@Builder
public class ReviewsResponseDto {

    @Schema(description = "리뷰 ID(item의 기본 키)")
    private final Long id;

    @Schema(description = "활동 리뷰 타이틀")
    private final String title;

    @Schema(description = "작성자")
    private final String author;

    @Schema(description = "작성자 프로필 이미지")
    private final String authorProfileImageUrl;

    @Schema(description = "활동 기수")
    private final Integer generation;

    @Schema(description = "활동후기 설명")
    private final String description;

    @Schema(description = "파트(활동 기수)") //
    private final Part part;

    @Schema(description = "주제")
    private final String subject;

    @Schema(description = "썸네일 URL(활동후기 타이틀)")//
    private final String thumbnailUrl;

    @Schema(description = "후기 작성 플랫폼")
    private final String platform;

    @Schema(description = "Redirect Link")
    private final String url;
}
