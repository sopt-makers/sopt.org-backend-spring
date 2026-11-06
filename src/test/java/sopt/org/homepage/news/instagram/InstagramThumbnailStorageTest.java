package sopt.org.homepage.news.instagram;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.infrastructure.aws.s3.S3Service;

class InstagramThumbnailStorageTest extends IntegrationTestBase {
    private static final String URL = "https://scontent.cdninstagram.com/cover.jpg";
    @MockBean S3Service s3;
    private HttpClient http;
    private InstagramThumbnailStorage storage;

    @BeforeEach
    void setUp() {
        http = mock(HttpClient.class);
        storage = new InstagramThumbnailStorage(s3, http, Duration.ofMillis(100));
        when(s3.isAllowedContentType("image/jpeg")).thenReturn(true);
    }

    @Test
    @DisplayName("HTTPS 또는 허용 CDN이 아니면 네트워크 호출 전에 거부")
    void rejectsInvalidHosts() {
        for (String url : List.of("http://scontent.cdninstagram.com/cover.jpg", "https://example.com/cover.jpg",
                "https://cdninstagram.com.evil.example/cover.jpg")) {
            assertThatThrownBy(() -> storage.store("1", url)).isInstanceOf(IllegalArgumentException.class);
        }
        verifyNoInteractions(http);
        verifyNoInteractions(s3);
    }

    @Test
    @DisplayName("유효한 이미지 데이터는 기존 S3 서비스로 업로드")
    void uploadsDownloadedImage() throws Exception {
        respond(200, "image/jpeg; charset=binary", new byte[]{1, 2, 3});
        when(s3.uploadFile(any(InputStream.class), eq(3L), eq("image/jpeg"), eq("1"), eq("news/instagram/")))
                .thenReturn("https://s3.example.com/1");
        assertThat(storage.store("1", URL)).isEqualTo("https://s3.example.com/1");
        verify(s3).uploadFile(any(InputStream.class), eq(3L), eq("image/jpeg"), eq("1"), eq("news/instagram/"));
    }

    @Test
    @DisplayName("오류 응답·이미지가 아닌 타입·빈 데이터는 업로드하지 않음")
    void rejectsInvalidResponses() throws Exception {
        respond(404, "image/jpeg", new byte[]{1});
        assertThatThrownBy(() -> storage.store("1", URL)).isInstanceOf(IllegalStateException.class);
        respond(200, "text/html", new byte[]{1});
        assertThatThrownBy(() -> storage.store("1", URL)).isInstanceOf(IllegalStateException.class);
        respond(200, "image/jpeg", new byte[0]);
        assertThatThrownBy(() -> storage.store("1", URL)).isInstanceOf(IllegalStateException.class);
        verify(s3, never()).uploadFile(any(InputStream.class), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("본문 수신이 완료되지 않으면 전체 제한시간 뒤 HTTP 작업 취소")
    void cancelsIncompleteDownload() {
        var pending = new CompletableFuture<HttpResponse<byte[]>>();
        when(http.sendAsync(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<byte[]>>any()))
                .thenReturn(pending);
        assertThatThrownBy(() -> storage.store("1", URL)).isInstanceOf(IllegalStateException.class)
                .hasMessage("Thumbnail download timed out");
        assertThat(pending.isCancelled()).isTrue();
        verify(s3, never()).uploadFile(any(InputStream.class), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("10MB를 넘는 본문은 수신 중 구독을 취소하고 업로드하지 않음")
    void cancelsOversizedBodyWhileReceiving() {
        Flow.Subscription subscription = mock(Flow.Subscription.class);
        when(http.sendAsync(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<byte[]>>any()))
                .thenAnswer(invocation -> {
                    HttpResponse.BodyHandler<byte[]> handler = invocation.getArgument(1);
                    var subscriber = handler.apply(mock(HttpResponse.ResponseInfo.class));
                    subscriber.onSubscribe(subscription);
                    subscriber.onNext(List.of(ByteBuffer.wrap(new byte[10 * 1024 * 1024 + 1])));
                    return subscriber.getBody().toCompletableFuture().thenApply(bytes -> response(200, "image/jpeg", bytes));
                });
        assertThatThrownBy(() -> storage.store("1", URL)).isInstanceOf(IllegalStateException.class);
        verify(subscription).cancel();
        verify(s3, never()).uploadFile(any(InputStream.class), anyLong(), anyString(), anyString(), anyString());
    }

    private void respond(int status, String contentType, byte[] bytes) {
        HttpResponse<byte[]> reply = response(status, contentType, bytes);
        when(http.sendAsync(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<byte[]>>any()))
                .thenReturn(CompletableFuture.completedFuture(reply));
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<byte[]> response(int status, String contentType, byte[] bytes) {
        HttpResponse<byte[]> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(status);
        when(response.headers()).thenReturn(HttpHeaders.of(Map.of("content-type", List.of(contentType)), (a, b) -> true));
        when(response.body()).thenReturn(bytes);
        return response;
    }
}
