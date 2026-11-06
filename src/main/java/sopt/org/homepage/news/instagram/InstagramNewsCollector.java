package sopt.org.homepage.news.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sopt.org.homepage.infrastructure.external.apify.ApifyInstagramClient;
import sopt.org.homepage.news.NewsRepository;

@Slf4j
@Service
@Profile("!lambda-dev")
@ConditionalOnProperty(name = "news.instagram.enabled", havingValue = "true")
@RequiredArgsConstructor
public class InstagramNewsCollector {
    private static final Set<String> TERMINAL = Set.of("SUCCEEDED", "FAILED", "ABORTED", "TIMED-OUT");
    private final ApifyInstagramClient apify;
    private final InstagramNewsPostProcessor processor;
    private final InstagramNewsConfig.Properties properties;
    private final NewsRepository news;
    private final AtomicBoolean starting = new AtomicBoolean();

    @Scheduled(cron = "${news.instagram.cron:0 0 9 * * MON}", zone = "Asia/Seoul", scheduler = "instagramTaskScheduler")
    public void collect() {
        try { startCollection(); }
        catch (Exception e) { log.error("Instagram news collection failed", e); }
    }

    /** 같은 결과를 다시 읽어도 게시물 ID로 중복을 막고 실패한 저장은 재시도한다. */
    @Scheduled(fixedDelayString = "${news.instagram.poll-delay-ms:60000}", scheduler = "instagramTaskScheduler")
    public void importLatest() {
        try { importRun(apify.lastRun()); }
        catch (Exception e) { log.error("Instagram result import failed; retry next scheduled import", e); }
    }

    /** 관리자는 수집만 트리거한다. 다운로드·업로드 완료를 기다리지 않는다. */
    public String startCollection() {
        if (properties.apifyToken() == null || properties.apifyToken().isBlank()
                || properties.taskId() == null || properties.taskId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Apify API token and environment-specific task ID are required");
        }
        if (!starting.compareAndSet(false, true)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Instagram collection is starting");
        }
        try {
            JsonNode run = apify.lastRun();
            if (!run.isMissingNode() && !TERMINAL.contains(run.path("status").asText())) {
                String id = run.path("id").asText();
                if (id.isBlank()) throw new IllegalStateException("Apify run ID missing");
                return id;
            }
            // 미저장 결과의 기간을 다음 수집에도 포함해 개별 실패가 오래돼도 누락하지 않는다.
            Instant since = properties.collectionSince(news.findLatestInstagramPublishedAt());
            List<InstagramPost> previous;
            try {
                previous = results(run);
            } catch (feign.FeignException.NotFound | IllegalStateException e) {
                // 만료된 dataset이나 잘못된 완료 결과가 다음 유료 실행까지 영구 차단하지 않게 한다.
                log.warn("Previous Instagram dataset unavailable; restarting from configured lower bound", e);
                previous = List.of();
                since = properties.since();
            }
            if (previous.size() >= properties.resultsLimit()) since = properties.since();
            for (InstagramPost post : previous) {
                if (processor.isEligible(post) && !news.existsByInstagramPostId(post.id())
                        && !post.timestamp().isAfter(since)) {
                    Instant retrySince = post.timestamp().minusSeconds(1);
                    since = retrySince.isBefore(properties.since()) ? properties.since() : retrySince;
                }
            }
            return apify.start(since);
        } finally { starting.set(false); }
    }

    private void importRun(JsonNode run) {
        for (InstagramPost post : results(run)) {
            if (post == null) continue;
            try { processor.process(post); }
            catch (Exception e) { log.error("Instagram post failed; retry next import - postId={}", post.id(), e); }
        }
    }

    private List<InstagramPost> results(JsonNode run) {
        if (run.isMissingNode() || !TERMINAL.contains(run.path("status").asText())) return List.of();
        String dataset = run.path("defaultDatasetId").asText();
        if (dataset.isBlank()) {
            if ("SUCCEEDED".equals(run.path("status").asText())) throw new IllegalStateException("Apify dataset ID missing");
            return List.of();
        }
        return apify.items(dataset);
    }
}
