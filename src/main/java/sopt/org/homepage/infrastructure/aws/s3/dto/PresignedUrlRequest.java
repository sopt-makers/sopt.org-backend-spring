package sopt.org.homepage.infrastructure.aws.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Presigned URL 발급 요청 (Lambda 환경용)")
public class PresignedUrlRequest {

    @NotBlank(message = "파일명은 필수입니다")
    @Schema(description = "업로드할 파일명", example = "banner-image.jpg")
    private String fileName;

    @NotBlank(message = "Content-Type은 필수입니다")
    @Pattern(
            regexp = "^image/(jpeg|jpg|png|gif|webp)$",
            message = "허용된 이미지 타입: jpeg, jpg, png, gif, webp"
    )
    @Schema(description = "파일의 Content-Type (클라이언트에서 전달)", example = "image/jpeg")
    private String contentType;

    @Schema(description = "저장할 디렉토리 (선택, 기본값: 빈 문자열)", example = "news/")
    private String directory;
}
