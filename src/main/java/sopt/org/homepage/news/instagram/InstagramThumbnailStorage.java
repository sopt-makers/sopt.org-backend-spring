package sopt.org.homepage.news.instagram;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sopt.org.homepage.infrastructure.aws.s3.S3Service;

@Component
@RequiredArgsConstructor
public class InstagramThumbnailStorage {
    private final S3Service s3Service;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public String store(String postId, String imageUrl) {
        URI uri = URI.create(imageUrl);
        String host = uri.getHost();
        if (!"https".equals(uri.getScheme()) || host == null
                || !(host.endsWith(".cdninstagram.com") || host.endsWith(".fbcdn.net"))) {
            throw new IllegalArgumentException("Invalid Instagram thumbnail host");
        }
        try {
            HttpResponse<java.io.InputStream> response = http.send(HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(30)).GET().build(), HttpResponse.BodyHandlers.ofInputStream());
            try (var stream = response.body()) {
                String contentType = response.headers().firstValue("content-type").orElse("").split(";")[0];
                if (response.statusCode() != 200 || !s3Service.isAllowedContentType(contentType)) {
                    throw new IllegalStateException("Thumbnail download failed");
                }
                byte[] bytes = stream.readNBytes(10 * 1024 * 1024 + 1);
                if (bytes.length == 0 || bytes.length > 10 * 1024 * 1024) {
                    throw new IllegalStateException("Invalid thumbnail size");
                }
                return s3Service.uploadFile(new java.io.ByteArrayInputStream(bytes), bytes.length,
                        contentType, postId, "news/instagram/");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thumbnail download interrupted", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Thumbnail download failed", e);
        }
    }
}
