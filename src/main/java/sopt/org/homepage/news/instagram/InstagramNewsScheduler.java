package sopt.org.homepage.news.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Component
@ConditionalOnProperty(name = "news.instagram.enabled", havingValue = "true")
@RequiredArgsConstructor
public class InstagramNewsScheduler {
    private final ApifyInstagramClient apify;
    private final InstagramNewsPostProcessor processor;
    private final InstagramNewsConfig.Properties properties;

    private String runId;
    private int itemOffset;

    /** 주간 수집 시작 실패는 기록하고 다음 예약 실행에서 재시도한다. */
    @Scheduled(cron = "${news.instagram.cron:0 0 9 * * MON}", zone = "Asia/Seoul")
    public synchronized void collect() {
        try { startCollection(); }
        catch (Exception e) {
            log.error("Instagram news collection failed - type={}", e.getClass().getSimpleName());
        }
    }

    /** 진행 중인 작업만 조회하며 조회 실패 시 같은 페이지를 다음 poll에서 재시도한다. */
    @Scheduled(fixedDelayString = "${news.instagram.poll-delay-ms:60000}")
    public synchronized void poll() {
        try { processBatch(); }
        catch (Exception e) {
            log.error("Instagram news collection failed; retry next poll - type={}", e.getClass().getSimpleName());
        }
    }

    /** 현재 작업이 있으면 재사용하고, 없으면 새 유료 수집 작업을 시작한다. */
    public synchronized String startCollection() {
        if (properties.apifyToken() == null || properties.apifyToken().isBlank()) {
            throw new IllegalStateException("Apify API token is required");
        }
        if (runId == null) {
            runId = apify.start();
            itemOffset = 0;
            log.info("Instagram news collection started - runId={}", runId);
        }
        return runId;
    }

    /** null 항목과 개별 실패를 건너뛰고 페이지 위치를 진행한다. 저장 실패는 다음 수집에서 재처리한다. */
    private void processBatch() {
        if (runId == null) return;
        JsonNode run = apify.run(runId);
        String status = run.path("status").asText();
        if (Set.of("FAILED", "ABORTED", "TIMED-OUT").contains(status)) {
            log.error("Instagram collection failed - runId={}, status={}", runId, status);
            finish();
            return;
        }
        if (!"SUCCEEDED".equals(status)) return;
        String dataset = run.path("defaultDatasetId").asText();
        if (dataset.isBlank()) throw new IllegalStateException("Apify dataset ID missing");
        List<InstagramPost> posts = apify.items(dataset, itemOffset);
        for (InstagramPost post : posts) {
            if (post == null) continue;
            try { processor.process(post); }
            catch (Exception e) {
                log.error("Instagram post failed; retry next weekly collection - postId={}, type={}",
                        post.id(), e.getClass().getSimpleName());
            }
        }
        itemOffset += posts.size();
        if (posts.size() < 10) {
            log.info("Instagram news collection finished - processed={}", itemOffset);
            finish();
        }
    }

    private void finish() {
        runId = null;
        itemOffset = 0;
    }
}
