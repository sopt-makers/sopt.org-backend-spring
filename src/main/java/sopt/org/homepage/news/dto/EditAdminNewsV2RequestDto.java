package sopt.org.homepage.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "최신소식 수정하기 (Presigned URL 방식)")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditAdminNewsV2RequestDto {

    @Schema(
            description = "S3에 업로드된 이미지 URL",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://s3.ap-northeast-2.amazonaws.com/sopt.org/develop/news/uuid_image.jpg"
    )
    @NotBlank(message = "imageUrl must not be blank")
    private String imageUrl;

    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT 36기 모집 안내")
    @NotBlank(message = "title must not be blank")
    private String title;

    @Schema(description = "링크", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://sopt.org/recruit")
    @NotBlank(message = "link must not be blank")
    private String link;
}
