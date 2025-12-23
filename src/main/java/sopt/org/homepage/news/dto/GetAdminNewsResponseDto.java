package sopt.org.homepage.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "최신소식 조회하기")
@Getter
@Builder
@RequiredArgsConstructor
public class GetAdminNewsResponseDto {
    @Schema(description = "이미지 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private final int id;

    @Schema(description = "이미지 링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://image.url")
    private final String image;

    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "MIND 23")
    private final String title;

    @Schema(description = "링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://disquiet.io/product/mind-23-%EC%98%A4%EB%8A%98%EB%8F%84-%EB%A9%88%EC%B6%94%EC%A7%80-%EC%95%8A%EB%8A%94-it%EC%9D%B8%EB%93%A4")
    private final String link;
}


