package sopt.org.homepage.aws.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String generatePresignedUrl(String fileName, String path);
    String getOriginalUrl(String presignedUrl);
    String uploadFile(MultipartFile file, String path);
    String getFileUrl(String fileName);
    void deleteFile(String fileUrl);
}