package sopt.org.homepage.news.instagram;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 재시작 시 이어받을 단일 계정 수집 상태. lease는 여러 인스턴스의 중복 처리를 막는다. */
@Entity
@Table(name = "instagram_collection_state")
@Getter
@NoArgsConstructor
public class InstagramCollectionState {
    @Id
    Integer id;
    @Column(name = "run_id")
    String runId;
    @Column(name = "item_offset")
    int itemOffset;
    int failures;
    @Column(name = "started_at")
    Instant startedAt;
    Instant checkpoint;
    @Column(name = "had_post_failure")
    boolean hadPostFailure;
    @Column(name = "lease_owner")
    String leaseOwner;
    @Column(name = "lease_until")
    Instant leaseUntil;
}
