package sopt.org.homepage.review.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 작성자별 리뷰 조회 요청 DTO
 */
@Schema(description = "작성자별 리뷰 조회 요청")
public record ReviewsByAuthorReq(

        @Schema(description = "작성자명", example = "홍길동")
        @NotBlank(message = "작성자명은 필수입니다.")
        String name
) {
}