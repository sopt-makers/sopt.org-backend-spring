package sopt.org.homepage.aws.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import sopt.org.homepage.aws.s3.dto.PresignedUrlRequest;
import sopt.org.homepage.aws.s3.dto.PresignedUrlResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private static final long PRESIGNED_URL_EXPIRATION_MINUTES_V2 = 10;

    // 허용된 Content-Type 목록 - 람다 전용
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    @Value("${aws.bucket.image}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucket.dir}")
    private String baseDir;


    public String generatePresignedUrl(String fileName, String path) {

        if (isCompleteS3Url(fileName)) {
            log.debug("Input is already a complete S3 URL, returning as-is: {}", fileName);
            return fileName;
        }

        try {
            String contentType = getContentTypeFromFileName(fileName);
            String key = baseDir + path + fileName;

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL for file: {}", fileName, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    public String getOriginalUrl(String presignedUrl) {
        try {
            // 1. 이미 완전한 S3 URL인지 확인 (쿼리 파라미터 없음)
            if (isCompleteS3Url(presignedUrl) && !presignedUrl.contains("?")) {
                log.debug("URL is already a complete S3 URL: {}", presignedUrl);
                return presignedUrl;
            }

            URL url = new URL(presignedUrl);
            System.out.println(url);
            String key = url.getPath().substring(1);
            if (key.startsWith(this.bucket + "/")) {
                key = key.substring(this.bucket.length() + 1); // 버킷 제거
            }

            return getFileUrl(key);
        } catch (MalformedURLException e) {
            log.error("Error parsing presigned URL: {}", presignedUrl, e);
            throw new RuntimeException("Failed to parse presigned URL", e);
        }
    }

    public String uploadFile(MultipartFile file, String path) {
        String fileName = createFileName(file.getOriginalFilename());

        String key = baseDir + path + fileName;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return getFileUrl(key);
        } catch (IOException e) {
            log.error("Error uploading file: {}", fileName, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public String getFileUrl(String fileKey) {
        return String.format("https://s3.%s.amazonaws.com/%s/%s",
                region,
                bucket,
                fileKey);
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractKeyFromUrl(fileUrl);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * 완전한 S3 URL인지 확인
     *
     * @param url 확인할 URL
     * @return S3 URL이면 true
     */
    private boolean isCompleteS3Url(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        // S3 URL 패턴 확인
        // 1. https://s3.{region}.amazonaws.com/{bucket}/{key}
        // 2. https://{bucket}.s3.{region}.amazonaws.com/{key}
        return url.startsWith("https://s3.") && url.contains(".amazonaws.com/") ||
                url.startsWith("https://" + bucket + ".s3.");
    }

    private String extractKeyFromUrl(String fileUrl) {
        try {
            String[] parts = fileUrl.split("/");
            return String.join("/", Arrays.copyOfRange(parts, 4, parts.length));
        } catch (Exception e) {
            throw new RuntimeException("Invalid S3 URL format", e);
        }
    }

    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    private String getContentTypeFromFileName(String fileName) {
        Map<String, String> contentTypeMap = new HashMap<>();
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("gif", "image/gif");
        contentTypeMap.put("pdf", "application/pdf");
        contentTypeMap.put("txt", "text/plain");
        contentTypeMap.put("html", "text/html");
        contentTypeMap.put("json", "application/json");
        // 필요한 파일타입 추가

        String extension = getFileExtension(fileName);
        return contentTypeMap.getOrDefault(extension, "application/octet-stream");
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        return (lastIndexOfDot == -1) ? "" : fileName.substring(lastIndexOfDot + 1).toLowerCase();
    }

    /**
     * Presigned URL 생성 (Lambda 환경용)
     * <p>
     * 클라이언트에서 Content-Type을 직접 전달받아 처리. 기존 generatePresignedUrl과 달리 DTO 기반으로 더 상세한 정보를 반환.
     */
    @Override
    public PresignedUrlResponse generatePresignedUrlV2(PresignedUrlRequest request) {
        // 1. Content-Type 검증
        if (!isAllowedContentType(request.getContentType())) {
            throw new IllegalArgumentException(
                    "허용되지 않는 파일 형식입니다: " + request.getContentType() +
                            ". 허용된 형식: " + ALLOWED_CONTENT_TYPES
            );
        }

        // 2. 파일 키 생성 (UUID + 원본 파일명)
        String fileName = createFileName(request.getFileName());
        String directory = resolveDirectory(request.getDirectory());
        String fileKey = baseDir + directory + fileName;

        // 3. Presigned URL 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(request.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES_V2))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();

        // 4. 파일 접근 URL 생성
        String fileUrl = getFileUrl(fileKey);

        log.info("Presigned URL 생성 완료 (V2 API) - fileKey: {}, expiresIn: {}분",
                fileKey, PRESIGNED_URL_EXPIRATION_MINUTES_V2);

        // 5. 응답 반환
        return PresignedUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .fileUrl(fileUrl)
                .expiresIn(PRESIGNED_URL_EXPIRATION_MINUTES_V2 * 60)
                .fileKey(fileKey)
                .build();
    }

    /**
     * Content-Type 허용 여부 확인
     */
    @Override
    public boolean isAllowedContentType(String contentType) {
        return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * 디렉토리 경로 정규화 (람다용)
     */
    private String resolveDirectory(String directory) {
        if (directory == null || directory.trim().isEmpty()) {
            return "";
        }
        String resolved = directory.trim();
        // 시작의 / 제거
        if (resolved.startsWith("/")) {
            resolved = resolved.substring(1);
        }
        // 끝에 / 추가
        if (!resolved.endsWith("/")) {
            resolved += "/";
        }
        return resolved;
    }

}
