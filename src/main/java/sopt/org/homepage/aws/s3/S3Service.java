package sopt.org.homepage.aws.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${aws.bucket.image}")
    private String bucket;

    @Value("${aws.bucket.dir}")
    private String baseDir;

    public String generatePresignedUrl(String fileName, String contentType, String path) {
        try {
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

    public String getFileUrl(String fileName) {
        try {
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            return s3Client.utilities().getUrl(request).toString();
        } catch (Exception e) {
            log.error("Error getting file URL: {}", fileName, e);
            throw new RuntimeException("Failed to get file URL", e);
        }
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
}