package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.application.admin.dto.request.main.news.AddAdminNewsRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.review.AddAdminReviewRequestDto;

@Validated
@Schema(description = "어드민 홈 탭 배포")
@Getter
@NoArgsConstructor
public class AddAdminHomeRequestDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "36")
    @NotNull(message = "generation must not be null")
    @Positive(message = "generation must be a positive number")
    private int generation;

    @Schema(description = "홈 헤더 이미지 파일명", requiredMode = Schema.RequiredMode.REQUIRED, example = "home_header.png")
    @NotEmpty(message = "homeHeaderImageFileName must not be empty")
    private String homeHeaderImageFileName;

    @Schema(description = "리뷰 목록")
    @Valid
    private List<AddAdminReviewRequestDto> review;

    @Schema(description = "최신소식 목록")
    @Valid
    private List<AddAdminNewsRequestDto> news;
}
