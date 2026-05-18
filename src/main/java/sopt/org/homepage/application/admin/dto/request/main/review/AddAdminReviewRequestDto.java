package sopt.org.homepage.application.admin.dto.request.main.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddAdminReviewRequestDto {

    @Schema(description = "리뷰 제목", example = "후회없는 활동")
    @NotBlank
    private final String title;

    @Schema(description = "리뷰 내용", example = "후회없는 활동")
    @NotBlank
    private final String content;

    @Schema(description = "김솝트 | 36,37기 활동 | 서버")
    private final String authorInfo;
}
