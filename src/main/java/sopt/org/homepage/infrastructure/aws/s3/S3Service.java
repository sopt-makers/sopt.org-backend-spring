package sopt.org.homepage.infrastructure.aws.s3;

import org.springframework.web.multipart.MultipartFile;
import sopt.org.homepage.infrastructure.aws.s3.dto.PresignedUrlRequest;
import sopt.org.homepage.infrastructure.aws.s3.dto.PresignedUrlResponse;

public interface S3Service {

    //Presigned URL 생성
    String generatePresignedUrl(String fileName, String path);


    // Presigned URL에서 원본 URL 추출
    String getOriginalUrl(String presignedUrl);


    // 파일 업로드 - Multipart
    String uploadFile(MultipartFile file, String path);

    // 파일 URL 생성
    String getFileUrl(String fileName);

    // 파일 삭제 (NewsService에서 사용 중)
    void deleteFile(String fileUrl);


    /**
     * Presigned URL 생성 (새 API 방식 - Lambda 환경용)
     * <p>
     * 클라이언트에서 Content-Type을 직접 전달받아 처리합니다. Lambda 환경에서 10MB 페이로드 제한을 우회하기 위해 사용됩니다.
     *
     * @param request Presigned URL 요청 정보 (파일명, Content-Type, 디렉토리)
     * @return Presigned URL 응답 (업로드 URL, 파일 URL, 만료 시간 등)
     */
    PresignedUrlResponse generatePresignedUrlV2(PresignedUrlRequest request);

    /**
     * Content-Type 허용 여부 확인
     *
     * @param contentType 확인할 Content-Type
     * @return 허용된 타입이면 true
     */
    boolean isAllowedContentType(String contentType);
}
