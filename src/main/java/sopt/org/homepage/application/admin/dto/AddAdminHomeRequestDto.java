package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.application.admin.dto.request.main.news.AddAdminNewsPresignedRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.review.AddAdminReviewRequestDto;

@Schema(description = "어드민 홈 탭 배포")
@Getter
@NoArgsConstructor
public class AddAdminHomeRequestDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "39")
    @NotNull(message = "generation must not be null")
    @Positive(message = "generation must be a positive number")
    private Integer generation;

    @Schema(description = "홈 헤더 이미지 파일명 (생략 시 기존 이미지 유지)", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "home_header.png")
    private String homeHeaderImageFileName;

    @Schema(description = "리뷰 목록")
    @Valid
    private List<AddAdminReviewRequestDto> review;

    @Schema(description = "최신소식 목록")
    @Valid
    private List<AddAdminNewsPresignedRequestDto> news;
}
