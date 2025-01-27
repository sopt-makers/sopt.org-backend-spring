package sopt.org.homepage.aws.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
	private final S3Presigner s3Presigner;
	private final S3Client s3Client;

	@Value("${aws.bucket.image}")
	private String bucket;

	@Value("${aws.region}")
	private String region;

	@Value("${aws.bucket.dir}")
	private String baseDir;

	public String generatePresignedUrl(String fileName, String path) {
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
}