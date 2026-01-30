package sopt.org.homepage.infrastructure.aws.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Presigned URL 발급 응답 (Lambda 환경용)")
public class PresignedUrlResponse {

    @Schema(
            description = "S3 업로드용 Presigned URL (PUT 요청에 사용, 10분간 유효)",
            example = "https://sopt.org.s3.ap-northeast-2.amazonaws.com/develop/news/uuid-image.jpg?X-Amz-Algorithm=..."
    )
    private String presignedUrl;

    @Schema(
            description = "업로드 완료 후 파일 접근 URL",
            example = "https://s3.ap-northeast-2.amazonaws.com/sopt.org/develop/news/uuid-image.jpg"
    )
    private String fileUrl;

    @Schema(description = "Presigned URL 만료 시간 (초)", example = "600")
    private long expiresIn;

    @Schema(
            description = "저장된 파일 키 (S3 객체 키)",
            example = "develop/news/550e8400-e29b-41d4-a716-446655440000_image.jpg"
    )
    private String fileKey;
}
