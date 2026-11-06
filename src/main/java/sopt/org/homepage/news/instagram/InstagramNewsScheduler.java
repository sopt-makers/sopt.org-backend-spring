package sopt.org.homepage.news.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@Profile("!lambda-dev")
@ConditionalOnProperty(name = "news.instagram.enabled", havingValue = "true")
@RequiredArgsConstructor
public class InstagramNewsScheduler {
    private final ApifyInstagramClient apify;
    private final InstagramNewsPostProcessor processor;
    private final InstagramNewsConfig.Properties properties;
    private final InstagramCollectionStore store;

    @Scheduled(cron = "${news.instagram.cron:0 0 9 * * MON}", zone = "Asia/Seoul", scheduler = "instagramTaskScheduler")
    public void collect() {
        try { startCollection(); }
        catch (Exception e) { log.error("Instagram news collection failed", e); }
    }

    /** 재시작 후 DB의 실행 ID와 offset에서 재개하며 외부 I/O 중에는 락을 보유하지 않는다. */
    @Scheduled(fixedDelayString = "${news.instagram.poll-delay-ms:60000}", scheduler = "instagramTaskScheduler")
    public void poll() {
        String owner = UUID.randomUUID().toString();
        if (!store.claim(owner, false)) return;
        try {
            var state = store.read();
            if (state.getStartedAt().plusSeconds(properties.maxDurationSeconds()).isBefore(Instant.now())) {
                log.warn("Instagram collection exceeded deadline - runId={}", state.getRunId());
                store.abort(owner);
                return;
            }
            processBatch(owner, state);
        } catch (Exception e) {
            log.error("Instagram news collection failed; retry next poll", e);
            store.failed(owner, properties.maxFailures());
        } finally { store.release(owner); }
    }

    /** 폴링의 다운로드가 진행 중이어도 기존 run ID를 즉시 반환한다. */
    public String startCollection() {
        if (properties.apifyToken() == null || properties.apifyToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Apify API token is required");
        }
        var state = store.read();
        if (state.getRunId() != null) return state.getRunId();
        String owner = UUID.randomUUID().toString();
        if (!store.claim(owner, true)) {
            state = store.read();
            if (state.getRunId() != null) return state.getRunId();
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Instagram collection is starting");
        }
        try {
            String runId = apify.start(properties.collectionSince(state.getCheckpoint()));
            store.started(owner, runId);
            return runId;
        } finally { store.release(owner); }
    }

    private void processBatch(String owner, InstagramCollectionState state) {
        JsonNode run = apify.run(state.getRunId());
        String status = run.path("status").asText();
        if (!Set.of("SUCCEEDED", "FAILED", "ABORTED", "TIMED-OUT").contains(status)) {
            store.advance(owner, 0, false, false, false);
            return;
        }
        String dataset = run.path("defaultDatasetId").asText();
        if (dataset.isBlank()) {
            if ("SUCCEEDED".equals(status)) throw new IllegalStateException("Apify dataset ID missing");
            store.abort(owner);
            return;
        }
        List<InstagramPost> posts = apify.items(dataset, state.getItemOffset());
        boolean failed = false;
        for (InstagramPost post : posts) {
            if (!store.renew(owner)) return;
            if (post == null) continue;
            try { processor.process(post); }
            catch (Exception e) {
                failed = true;
                log.error("Instagram post failed; retry next collection - postId={}", post.id(), e);
            }
        }
        boolean reachedLimit = state.getItemOffset() + posts.size() >= properties.resultsLimit();
        if (reachedLimit) log.warn("Instagram result limit reached; preserving collection window for retry");
        store.advance(owner, posts.size(), failed || reachedLimit, posts.size() < ApifyInstagramClient.PAGE_SIZE,
                "SUCCEEDED".equals(status));
    }
}
