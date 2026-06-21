package sopt.org.homepage.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Validated
@Schema(description = "최신소식 수정하기")
@Getter
@RequiredArgsConstructor
public class EditAdminNewsRequestDto {

    @Schema(description = "이미지 (변경 시에만 업로드)", type = "string", format = "binary")
    private final MultipartFile image;

    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "MIND 24")
    @NotBlank(message = "title must not be blank")
    private final String title;

    @Schema(description = "링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com")
    @NotBlank(message = "link must not be blank")
    private final String link;
}
