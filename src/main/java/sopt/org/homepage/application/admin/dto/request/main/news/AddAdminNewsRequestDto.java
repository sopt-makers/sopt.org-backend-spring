package sopt.org.homepage.application.admin.dto.request.main.news;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class AddAdminNewsRequestDto {
    @Schema(description = "이미지 파일명", requiredMode = Schema.RequiredMode.REQUIRED, example = "news.png")
    @NotNull(message = "imageFileName must not be null")
    private final String imageFileName;

    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "MIND 23")
    @NotEmpty(message = "title must not be empty")
    private final String title;

    @Schema(description = "링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://disquiet.io/product/mind-23-%EC%98%A4%EB%8A%98%EB%8F%84-%EB%A9%88%EC%B6%94%EC%A7%80-%EC%95%8A%EB%8A%94-it%EC%9D%B8%EB%93%A4")
    @NotEmpty(message = "link must not be empty")
    private final String link;
}
