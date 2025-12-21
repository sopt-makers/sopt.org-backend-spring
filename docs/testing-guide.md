# 📝 테스트 작성 가이드

> 이 문서는 SOPT 공식 홈페이지 API 서버의 테스트 작성 기준을 정의합니다.    
> **목표: 6개월마다 팀원이 바뀌는 환경에서 "살아있는 인수인계 문서" 역할**

---

## 1. 테스트 전략 개요

### 1.1 테스트 피라미드

```
                    ┌───────────┐
                    │   E2E     │  ← 최소한 (CI/CD에서 API 호출)
                   ─┼───────────┼─
                  / │  통합     │ \  ← 핵심 (모든 도메인)
                 /  │  테스트   │  \
               ─┼───┼───────────┼───┼─
              / │   │  단위     │   │ \  ← Full DDD 도메인만
             /  │   │  테스트   │   │  \
            ────┴───┴───────────┴───┴────
```

### 1.2 도메인별 테스트 전략

| 도메인 유형                           | 단위 테스트 | 통합 테스트 | 이유                  |
|----------------------------------|--------|--------|---------------------|
| **Full DDD** (Review, SoptStory) | ✅ 필수   | ✅ 필수   | 복잡한 비즈니스 규칙 검증      |
| **Light** (나머지)                  | ❌ 불필요  | ✅ 필수   | 단순 CRUD, 통합 테스트로 충분 |

---

## 2. 통합 테스트 작성법

### 2.1 기본 구조

```java
package sopt.org.homepage.notification.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;

/**
 * 알림 서비스 통합 테스트
 *
 * 검증 범위:
 * - 알림 등록/조회 전체 흐름
 * - 중복 등록 방지
 * - 기수별 조회
 */
@DisplayName("알림 서비스 통합 테스트")
class NotificationServiceTest extends IntegrationTestBase {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    // ===== 정상 케이스 =====

    @Test
    @DisplayName("✅ 정상: 유효한 이메일과 기수로 알림 등록")
    void register_WithValidEmailAndGeneration_Success() {
        // given - 준비
        String email = "test@sopt.org";
        Integer generation = 35;

        // when - 실행
        Notification result = notificationService.register(email, generation);

        // then - 검증
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getGeneration()).isEqualTo(generation);

        // DB 검증 (선택적)
        assertThat(notificationRepository.count()).isEqualTo(1);
    }

    // ===== 실패 케이스 =====

    @Test
    @DisplayName("❌ 실패: 동일 이메일+기수 중복 등록 불가")
    void register_WithDuplicateEmailAndGeneration_ThrowsException() {
        // given - 이미 등록된 상태
        String email = "test@sopt.org";
        Integer generation = 35;
        notificationService.register(email, generation);

        // when & then - 중복 등록 시도
        assertThatThrownBy(() -> notificationService.register(email, generation))
                .isInstanceOf(DuplicateNotificationException.class)
                .hasMessageContaining("이미 등록된 알림");
    }

    // ===== 조회 케이스 =====

    @Test
    @DisplayName("✅ 조회: 특정 기수의 알림 목록 조회")
    void findByGeneration_ReturnsMatchingNotifications() {
        // given - 35기 2건, 36기 1건
        notificationService.register("user1@sopt.org", 35);
        notificationService.register("user2@sopt.org", 35);
        notificationService.register("user3@sopt.org", 36);

        // when
        List<Notification> result = notificationService.findByGeneration(35);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(n -> n.getGeneration().equals(35));
    }
}
```

### 2.2 테스트 명명 규칙

#### 클래스명

```
{도메인}ServiceTest
{도메인}ControllerTest (필요 시)
```

#### 메서드명 (한글 권장)

```java
// 패턴: {행위}_{조건}_{결과}

// 정상 케이스
@DisplayName("✅ 정상: 유효한 요청으로 리뷰 생성")
void createReview_WithValidRequest_Success() {
}

// 실패 케이스
@DisplayName("❌ 실패: 중복 URL로 리뷰 생성 불가")
void createReview_WithDuplicateUrl_ThrowsException() {
}

// 조회 케이스
@DisplayName("✅ 조회: 카테고리로 리뷰 필터링")
void searchReviews_FilterByCategory_ReturnsFiltered() {
}
```

### 2.3 DisplayName 이모지 규칙

| 이모지 | 용도         | 예시                               |
|-----|------------|----------------------------------|
| ✅   | 정상/성공 케이스  | `@DisplayName("✅ 정상: 알림 등록 성공")` |
| ❌   | 실패/예외 케이스  | `@DisplayName("❌ 실패: 중복 등록 불가")` |
| 🔍  | 조회/검색 케이스  | `@DisplayName("🔍 조회: 기수별 필터링")` |
| ⚡   | 성능/경계값 테스트 | `@DisplayName("⚡ 대량 데이터 조회")`    |

---

## 3. 단위 테스트 작성법 (Full DDD만)

### 3.1 언제 단위 테스트를 작성하나?

**Full DDD 도메인 (Review, SoptStory)만 단위 테스트 작성**

- VO의 검증 로직
- Entity의 비즈니스 규칙
- 도메인 불변식

### 3.2 VO 단위 테스트

```java

@DisplayName("LikeCount VO 단위 테스트")
class LikeCountTest {

    @Test
    @DisplayName("좋아요 증가 시 새로운 객체 반환 (불변)")
    void increment_ReturnsNewInstance() {
        // given
        LikeCount count = new LikeCount(5);

        // when
        LikeCount incremented = count.increment();

        // then
        assertThat(incremented.getValue()).isEqualTo(6);
        assertThat(count.getValue()).isEqualTo(5);  // 원본 불변
    }

    @Test
    @DisplayName("0에서 감소 시도하면 예외 발생")
    void decrement_AtZero_ThrowsException() {
        // given
        LikeCount count = LikeCount.initial();

        // when & then
        assertThatThrownBy(() -> count.decrement())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("좋아요 개수는 음수가 될 수 없습니다.");
    }
}
```

### 3.3 Entity 단위 테스트

```java

@DisplayName("Review 엔티티 단위 테스트")
class ReviewTest {

    @Test
    @DisplayName("전체활동 카테고리는 세부활동이 필수")
    void create_ActivityWithoutSubjects_ThrowsException() {
        // given
        ReviewCategory category = new ReviewCategory(CategoryType.ACTIVITY);
        ReviewSubjects emptySubjects = new ReviewSubjects(List.of());

        // when & then
        assertThatThrownBy(() ->
                Review.create(content, author, 34, PartType.SERVER, category, emptySubjects, url)
        )
                .isInstanceOf(InvalidReviewSubjectException.class)
                .hasMessageContaining("세부 활동이 필수");
    }

    @Test
    @DisplayName("세미나 카테고리는 세부활동 없어도 생성 가능")
    void create_SeminarWithoutSubjects_Success() {
        // given
        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);
        ReviewSubjects emptySubjects = new ReviewSubjects(List.of());

        // when
        Review review = Review.create(content, author, 34, PartType.SERVER, category, emptySubjects, url);

        // then
        assertThat(review).isNotNull();
        assertThat(review.getSubjectValues()).isEmpty();
    }
}
```

---

## 4. 테스트 데이터 관리

### 4.1 테스트 격리

```java
// IntegrationTestBase가 @Transactional을 포함하므로
// 각 테스트 후 자동 롤백됨

@DisplayName("알림 서비스 테스트")
class NotificationServiceTest extends IntegrationTestBase {

    // @AfterEach deleteAll() 불필요!
    // @Transactional 덕분에 자동 롤백됨

    @Test
    void test1() {
        // 데이터 생성
        notificationService.register("test@sopt.org", 35);
        // 테스트 끝나면 자동 롤백
    }

    @Test
    void test2() {
        // test1의 데이터는 이미 롤백됨
        // 깨끗한 상태에서 시작
    }
}
```

### 4.2 테스트 데이터 생성 헬퍼

```java
// 테스트 클래스 내부에 헬퍼 메서드 정의

private Notification createNotification(String email, Integer generation) {
    return notificationRepository.save(
            Notification.of(email, generation)
    );
}

private Review createReview(String title, CategoryType category, List<String> subjects) {
    return reviewRepository.save(
            Review.create(
                    new ReviewContent(title, "설명", null, "Medium"),
                    new ReviewAuthor("작성자", null),
                    34,
                    PartType.SERVER,
                    new ReviewCategory(category),
                    new ReviewSubjects(subjects),
                    new ReviewUrl("https://example.com/" + System.nanoTime())
            )
    );
}
```

---

## 5. 인수인계 관점 테스트

### 5.1 테스트는 "문서"다

테스트 코드를 읽으면 비즈니스 규칙을 이해할 수 있어야 함:

```java

@DisplayName("Review 도메인 비즈니스 규칙")
class ReviewBusinessRulesTest extends IntegrationTestBase {

    @Nested
    @DisplayName("카테고리별 세부활동 규칙")
    class SubjectRules {

        @Test
        @DisplayName("✅ 전체활동: 세미나, 프로젝트 등 세부활동 필수")
        void activity_RequiresSubjects() {
        }

        @Test
        @DisplayName("✅ 서류/면접: 서류, 면접 중 하나 필수")
        void recruiting_RequiresSubjectType() {
        }

        @Test
        @DisplayName("✅ 세미나/프로젝트/기타: 세부활동 선택적")
        void others_SubjectsOptional() {
        }
    }

    @Nested
    @DisplayName("URL 중복 방지")
    class UrlUniqueness {

        @Test
        @DisplayName("❌ 동일 URL로 리뷰 생성 불가")
        void duplicateUrl_Rejected() {
        }
    }
}
```

### 5.2 시나리오 기반 테스트

```java

@DisplayName("좋아요 시나리오")
@Nested
class LikeScenarios {

    @Test
    @DisplayName("시나리오: 사용자가 좋아요를 누르면 카운트 증가")
    void scenario_UserLikes_CountIncreases() {
        // given - SoptStory 생성
        SoptStoryId storyId = createSoptStory();

        // when - 좋아요
        soptStoryService.like(storyId, "192.168.0.1");

        // then - 카운트 증가 확인
        SoptStory story = soptStoryRepository.findById(storyId).orElseThrow();
        assertThat(story.getLikeCountValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("시나리오: 같은 IP로 중복 좋아요 불가")
    void scenario_DuplicateLike_Blocked() {
        // given
        SoptStoryId storyId = createSoptStory();
        String userIp = "192.168.0.1";
        soptStoryService.like(storyId, userIp);

        // when & then
        assertThatThrownBy(() -> soptStoryService.like(storyId, userIp))
                .isInstanceOf(AlreadyLikedException.class);
    }

    @Test
    @DisplayName("시나리오: 좋아요 취소하면 카운트 감소")
    void scenario_Unlike_CountDecreases() {
        // given
        SoptStoryId storyId = createSoptStory();
        String userIp = "192.168.0.1";
        soptStoryService.like(storyId, userIp);

        // when
        soptStoryService.unlike(storyId, userIp);

        // then
        SoptStory story = soptStoryRepository.findById(storyId).orElseThrow();
        assertThat(story.getLikeCountValue()).isEqualTo(0);
    }
}
```

---

## 6. 체크리스트

### 6.1 테스트 작성 전

- [ ] 어떤 비즈니스 규칙을 검증하는가?
- [ ] Full DDD인가 Light인가? (단위 테스트 필요 여부)
- [ ] 정상/실패 케이스 모두 커버하는가?

### 6.2 테스트 작성 후

- [ ] 테스트만 읽어도 비즈니스 규칙을 이해할 수 있는가?
- [ ] DisplayName이 명확한가?
- [ ] given-when-then 구조가 명확한가?
- [ ] 다른 테스트와 독립적인가? (격리)

### 6.3 PR 전

- [ ] 모든 테스트 통과?
- [ ] 커버리지 유지/향상?
- [ ] 불필요한 테스트 코드 제거?

---

## 7. 자주 묻는 질문

### Q1: 모든 API에 테스트가 필요한가요?

**A:** 모든 도메인에 통합 테스트는 필수입니다. 단, Controller 테스트는 선택적입니다.
Service 통합 테스트로 핵심 로직을 검증하면 충분합니다.

### Q2: Mock을 써도 되나요?

**A:** 외부 API 연동(Playground, Crew 등)은 Mock 사용 가능합니다.
하지만 DB, 내부 서비스는 실제 동작을 테스트하세요.

### Q3: 테스트가 너무 느려요

**A:**

- TestContainer 재사용 (`.withReuse(true)`)
- 불필요한 @SpringBootTest 제거
- 단위 테스트 비중 높이기

### Q4: 테스트 데이터가 꼬여요

**A:**

- IntegrationTestBase의 `@Transactional`이 롤백을 보장합니다
- 만약 롤백이 안 되면 `@AfterEach`에서 정리하세요

---

## 8. 참고 자료

- [TestContainers 공식 문서](https://www.testcontainers.org/)
- [AssertJ 공식 문서](https://assertj.github.io/doc/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

---

**작성일:** 2025년 12월 21일
**마지막 수정:** Phase 1 완료 시
