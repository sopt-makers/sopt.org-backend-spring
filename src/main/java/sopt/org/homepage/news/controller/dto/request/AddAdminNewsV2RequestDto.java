package sopt.org.homepage.news.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "최신소식 추가하기 (Presigned URL 방식)")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddAdminNewsV2RequestDto {

    @Schema(
            description = "S3에 업로드된 이미지 URL (Presigned URL로 업로드 후 받은 fileUrl)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://s3.ap-northeast-2.amazonaws.com/sopt.org/develop/news/uuid_image.jpg"
    )
    @NotEmpty(message = "imageUrl must not be empty")
    private String imageUrl;

    @Schema(
            description = "제목",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "SOPT 35기 모집 안내"
    )
    @NotEmpty(message = "title must not be empty")
    private String title;

    @Schema(
            description = "링크",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://sopt.org/recruit"
    )
    @NotEmpty(message = "link must not be empty")
    private String link;
}
