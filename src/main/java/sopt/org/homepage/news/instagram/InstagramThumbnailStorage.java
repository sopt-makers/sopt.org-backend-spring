package sopt.org.homepage.news.instagram;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sopt.org.homepage.infrastructure.aws.s3.S3Service;

@Component
public class InstagramThumbnailStorage {
    private static final int MAX_IMAGE_BYTES = 10 * 1024 * 1024;
    private final S3Service s3Service;
    private final HttpClient http;
    private final Duration downloadTimeout;

    @Autowired
    public InstagramThumbnailStorage(S3Service s3Service) {
        this(s3Service, HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(), Duration.ofSeconds(30));
    }

    InstagramThumbnailStorage(S3Service s3Service, HttpClient http, Duration downloadTimeout) {
        this.s3Service = s3Service;
        this.http = http;
        this.downloadTimeout = downloadTimeout;
    }

    /** 허용된 CDN에서 크기·시간 제한 내에 이미지를 받아 기존 S3 업로드 서비스를 사용한다. */
    public String store(String postId, String imageUrl) {
        URI uri = URI.create(imageUrl);
        String host = uri.getHost();
        if (!"https".equals(uri.getScheme()) || host == null
                || !(host.endsWith(".cdninstagram.com") || host.endsWith(".fbcdn.net"))) {
            throw new IllegalArgumentException("Invalid Instagram thumbnail host");
        }
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(downloadTimeout).GET().build();
        CompletableFuture<HttpResponse<byte[]>> download = http.sendAsync(request, ignored -> new LimitedBodySubscriber());
        try {
            // byte[] 응답은 본문 수신까지 완료되어야 완료되므로 제한시간이 전체 다운로드에 적용된다.
            HttpResponse<byte[]> response = download.get(downloadTimeout.toMillis(), TimeUnit.MILLISECONDS);
            String contentType = response.headers().firstValue("content-type").orElse("").split(";")[0];
            if (response.statusCode() != 200 || !s3Service.isAllowedContentType(contentType)) {
                throw new IllegalStateException("Thumbnail download failed");
            }
            byte[] bytes = response.body();
            if (bytes.length == 0 || bytes.length > MAX_IMAGE_BYTES) {
                throw new IllegalStateException("Invalid thumbnail size");
            }
            return s3Service.uploadFile(new ByteArrayInputStream(bytes), bytes.length, contentType, postId, "news/instagram/");
        } catch (TimeoutException e) {
            download.cancel(true);
            throw new IllegalStateException("Thumbnail download timed out", e);
        } catch (InterruptedException e) {
            download.cancel(true);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thumbnail download interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Thumbnail download failed", e.getCause());
        }
    }

    /** 상한을 넘는 응답은 메모리에 전부 적재하기 전에 HTTP 구독을 취소한다. */
    private static final class LimitedBodySubscriber implements HttpResponse.BodySubscriber<byte[]> {
        private final HttpResponse.BodySubscriber<byte[]> delegate = HttpResponse.BodySubscribers.ofByteArray();
        private Flow.Subscription subscription;
        private long size;

        @Override public CompletionStage<byte[]> getBody() { return delegate.getBody(); }
        @Override public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            delegate.onSubscribe(subscription);
        }
        @Override public void onNext(List<ByteBuffer> buffers) {
            for (ByteBuffer buffer : buffers) size += buffer.remaining();
            if (size > MAX_IMAGE_BYTES) {
                subscription.cancel();
                delegate.onError(new IOException("Invalid thumbnail size"));
                return;
            }
            delegate.onNext(buffers);
        }
        @Override public void onError(Throwable error) { delegate.onError(error); }
        @Override public void onComplete() { delegate.onComplete(); }
    }
}
