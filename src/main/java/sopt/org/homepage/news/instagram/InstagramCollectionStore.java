package sopt.org.homepage.news.instagram;

import java.time.Instant;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class InstagramCollectionStore {
    private final InstagramCollectionRepository repository;
    private final TransactionTemplate transaction;
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public InstagramCollectionStore(InstagramCollectionRepository repository, PlatformTransactionManager manager) {
        this.repository = repository;
        this.transaction = new TransactionTemplate(manager);
    }

    private <T> T update(Function<InstagramCollectionState, T> action) {
        return transaction.execute(status -> {
            repository.initialize();
            var state = repository.lockState();
            // OSIV가 켜진 관리자 요청에서도 이전 트랜잭션의 1차 캐시를 사용하지 않는다.
            entityManager.refresh(state, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
            return action.apply(state);
        });
    }

    public InstagramCollectionState read() {
        return update(s -> s);
    }

    /** 외부 I/O 전에 짧은 DB 트랜잭션으로 작업권만 확보한다. */
    public boolean claim(String owner, boolean starting) {
        return update(s -> {
            Instant now = Instant.now();
            if ((s.runId == null) != starting) return false;
            if (s.leaseUntil != null && s.leaseUntil.isAfter(now)) return false;
            s.leaseOwner = owner;
            s.leaseUntil = now.plus(Duration.ofMinutes(15));
            return true;
        });
    }

    public boolean renew(String owner) {
        return update(s -> {
            if (!Objects.equals(owner, s.leaseOwner) || s.leaseUntil == null || s.leaseUntil.isBefore(Instant.now())) return false;
            s.leaseUntil = Instant.now().plus(Duration.ofMinutes(15));
            return true;
        });
    }

    public void started(String owner, String runId) {
        update(s -> {
            if (Objects.equals(owner, s.leaseOwner)) {
                s.runId = runId;
                s.startedAt = Instant.now();
                s.itemOffset = 0;
                s.failures = 0;
                s.hadPostFailure = false;
                s.leaseOwner = null;
                s.leaseUntil = null;
            }
            return null;
        });
    }

    public void advance(String owner, int count, boolean postFailure, boolean complete, boolean succeeded) {
        update(s -> {
            if (Objects.equals(owner, s.leaseOwner)) {
                s.itemOffset += count;
                s.hadPostFailure |= postFailure;
                s.failures = 0;
                if (complete) {
                    if (succeeded && !s.hadPostFailure) s.checkpoint = s.startedAt;
                    clear(s);
                }
            }
            return null;
        });
    }

    public void failed(String owner, int maxFailures) {
        update(s -> {
            if (Objects.equals(owner, s.leaseOwner) && ++s.failures >= maxFailures) clear(s);
            return null;
        });
    }

    public void abort(String owner) {
        update(s -> { if (Objects.equals(owner, s.leaseOwner)) clear(s); return null; });
    }

    public void release(String owner) {
        update(s -> {
            if (Objects.equals(owner, s.leaseOwner)) { s.leaseOwner = null; s.leaseUntil = null; }
            return null;
        });
    }

    private void clear(InstagramCollectionState s) {
        s.runId = null;
        s.startedAt = null;
        s.itemOffset = 0;
        s.failures = 0;
        s.hadPostFailure = false;
    }
}
