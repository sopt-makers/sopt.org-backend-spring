package sopt.org.homepage.application.admin.dto.request.main.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EditAdminReviewRequestDto {

    @Schema(description = "리뷰 제목 (공백 포함 최대 10자)", example = "후회없는 활동")
    @NotBlank
    @Size(max = 10, message = "제목은 공백 포함 최대 10자까지 입력 가능합니다")
    private final String title;

    @Schema(description = "리뷰 내용 (최대 200자)", example = "후회없는 활동이었습니다")
    @NotBlank
    @Size(max = 200, message = "리뷰 내용은 최대 200자까지 입력 가능합니다")
    private final String content;

    @Schema(description = "작성자 정보", example = "김솝트 | 36,37기 활동 | 서버")
    @NotBlank
    private final String authorInfo;
}
