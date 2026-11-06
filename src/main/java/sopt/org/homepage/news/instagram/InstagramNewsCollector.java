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
            Instant since = properties.collectionSince(news.findLatestInstagramPublishedAt());
            if (!run.isMissingNode()) since = collectionSince(run, since);
            return apify.start(since);
        } finally { starting.set(false); }
    }

    /** 실패 실행은 원격 INPUT의 하한을 이어받으므로 연속 실패해도 재시도 범위가 사라지지 않는다. */
    private Instant collectionSince(JsonNode run, Instant since) {
        JsonNode input = apify.input(run.path("defaultKeyValueStoreId").asText());
        java.util.Optional<Instant> inputSince = previousSince(input);
        Instant previousSince = inputSince.orElse(properties.since());
        if (!"SUCCEEDED".equals(run.path("status").asText()) || inputSince.isEmpty()) {
            since = earlier(since, previousSince);
        }
        List<InstagramPost> previous;
        try {
            previous = results(run);
        } catch (feign.FeignException.NotFound | IllegalStateException e) {
            log.warn("Previous Instagram dataset unavailable; preserving its input window", e);
            return earlier(since, previousSince);
        }
        int previousLimit = input.path("resultsLimit").asInt(properties.resultsLimit());
        if (previousLimit < 1) previousLimit = properties.resultsLimit();
        if (previous.size() >= previousLimit) {
            if (properties.resultsLimit() <= previousLimit) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Previous collection reached its result limit; increase news.instagram.results-limit before retrying");
            }
            // 한도를 올린 재시도는 최초 기간이 아니라 해당 실행의 원래 기간만 다시 읽는다.
            since = earlier(since, previousSince);
        }
        for (InstagramPost post : previous) {
            if (processor.isEligible(post) && !news.existsByInstagramPostId(post.id())) {
                since = earlier(since, post.timestamp().minusSeconds(1));
            }
        }
        return since.isBefore(properties.since()) ? properties.since() : since;
    }

    private java.util.Optional<Instant> previousSince(JsonNode input) {
        try {
            Instant since = Instant.parse(input.path("onlyPostsNewerThan").asText());
            return java.util.Optional.of(since.isBefore(properties.since()) ? properties.since() : since);
        } catch (java.time.format.DateTimeParseException e) {
            return java.util.Optional.empty();
        }
    }

    private Instant earlier(Instant first, Instant second) {
        return first.isBefore(second) ? first : second;
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
