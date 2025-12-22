# 🔄 Phase 3: Notification 도메인 단순화 (파일럿)

> #5 [REFACTOR] Notification 도메인 단순화
>
> ⭐ **이 이슈가 파일럿** - Light 도메인 패턴 확립

---

## 1. 현재 vs 목표 구조

### 1.1 현재 구조 (과잉 엔지니어링)

```
notification/
├── domain/
│   ├── Notification.java           # Entity + VO 의존
│   └── vo/
│       ├── Email.java              # ❌ 삭제 대상
│       └── Generation.java         # ❌ 삭제 대상
├── repository/
│   └── NotificationCommandRepository.java
├── service/
│   ├── NotificationCommandService.java
│   └── NotificationQueryService.java
├── controller/
│   ├── NotificationController.java
│   └── dto/
│       ├── RegisterNotificationRequest.java
│       ├── RegisterNotificationResponse.java
│       └── NotificationListResponse.java
└── exception/
    ├── NotificationDomainException.java   # ❌ 삭제 대상
    └── NotificationErrorCode.java         # ❌ 삭제 대상

# 파일 수: 약 12개
```

### 1.2 목표 구조 (실용적)

```
notification/
├── Notification.java               # Entity + @Valid
├── NotificationRepository.java     # 단일 Repository
├── NotificationService.java        # 단일 Service
├── NotificationController.java
├── dto/
│   ├── RegisterNotificationRequest.java
│   ├── RegisterNotificationResponse.java
│   └── NotificationListResponse.java
└── exception/
    └── DuplicateNotificationException.java  # 필요한 예외만

# 파일 수: 약 7개 (42% 감소)
```

---

## 2. 변경 상세

### 2.1 Entity 변경

#### Before (현재)

```java
package sopt.org.homepage.notification.domain;

import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.exception.NotificationDomainException;

@Entity
@Table(name = "\"Notification\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;

    @Embedded
    private Email email;              // ❌ VO

    @Embedded
    private Generation generation;    // ❌ VO

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Notification create(Email email, Generation generation) {
        validateCreation(email, generation);
        return new Notification(email, generation);
    }

    private static void validateCreation(Email email, Generation generation) {
        if (email == null) {
            throw NotificationDomainException.emailRequired();
        }
        if (generation == null) {
            throw NotificationDomainException.generationRequired();
        }
    }

    // getter에서 VO.getValue() 호출 필요
}
```

#### After (변경 후)

```java
package sopt.org.homepage.notification;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 모집 알림 신청 엔티티
 *
 * 비즈니스 규칙:
 * - 이메일 형식 검증 (@Email)
 * - 기수는 1 이상의 양수 (@Min)
 * - 동일 이메일+기수 조합 중복 불가 (Service에서 검증)
 */
@Entity
@Table(name = "\"Notification\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;

    @Email(message = "유효한 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수입니다")
    @Column(name = "\"email\"", nullable = false)
    private String email;

    @Min(value = 1, message = "기수는 1 이상이어야 합니다")
    @NotNull(message = "기수는 필수입니다")
    @Column(name = "\"generation\"", nullable = false)
    private Integer generation;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 팩토리 메서드 - 테스트 용이성을 위해 유지
     */
    public static Notification of(String email, Integer generation) {
        Notification notification = new Notification();
        notification.email = email;
        notification.generation = generation;
        return notification;
    }

    // private 생성자 (JPA + 팩토리 메서드용)
    private Notification(String email, Integer generation) {
        this.email = email;
        this.generation = generation;
    }
}
```

### 2.2 Repository 변경

#### Before (현재)

```java
// NotificationCommandRepository.java
package sopt.org.homepage.notification.repository;

@Repository
public interface NotificationCommandRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByEmailAndGeneration(Email email, Generation generation);

    boolean existsByEmailAndGeneration(Email email, Generation generation);
}
```

#### After (변경 후)

```java
package sopt.org.homepage.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Notification Repository
 *
 * Command + Query 통합
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 이메일과 기수로 알림 조회
     */
    Optional<Notification> findByEmailAndGeneration(String email, Integer generation);

    /**
     * 이메일과 기수 조합 존재 여부 (중복 체크용)
     */
    boolean existsByEmailAndGeneration(String email, Integer generation);

    /**
     * 특정 기수의 모든 알림 조회
     */
    List<Notification> findByGeneration(Integer generation);
}
```

### 2.3 Service 통합

#### Before (현재 - 2개 파일)

```java
// NotificationCommandService.java
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService {

    private final NotificationCommandRepository repository;

    public Notification register(RegisterNotificationRequest request) {
        Email email = new Email(request.email());
        Generation generation = new Generation(request.generation());

        validateNotDuplicate(email, generation);

        Notification notification = Notification.create(email, generation);
        return repository.save(notification);
    }

    private void validateNotDuplicate(Email email, Generation generation) {
        if (repository.existsByEmailAndGeneration(email, generation)) {
            throw NotificationDomainException.duplicateNotification(...);
        }
    }
}

// NotificationQueryService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationCommandRepository repository;

    public List<Notification> getNotificationList(Integer generation) {
        return repository.findByGeneration(new Generation(generation));
    }
}
```

#### After (변경 후 - 1개 파일)

```java
package sopt.org.homepage.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.notification.exception.DuplicateNotificationException;

import java.util.List;

/**
 * Notification Service
 *
 * Command + Query 통합
 *
 * 비즈니스 규칙:
 * - 동일 이메일+기수 조합 중복 등록 불가
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ===== Command =====

    /**
     * 알림 등록
     *
     * @param email 이메일
     * @param generation 기수
     * @return 등록된 알림
     * @throws DuplicateNotificationException 중복 등록 시
     */
    @Transactional
    public Notification register(String email, Integer generation) {
        log.info("알림 등록 요청 - email={}, generation={}", email, generation);

        // 중복 검사
        if (notificationRepository.existsByEmailAndGeneration(email, generation)) {
            log.warn("중복 알림 등록 시도 차단 - email={}, generation={}", email, generation);
            throw new DuplicateNotificationException(email, generation);
        }

        // 저장
        Notification notification = Notification.of(email, generation);
        Notification saved = notificationRepository.save(notification);

        log.info("알림 등록 완료 - id={}", saved.getId());
        return saved;
    }

    // ===== Query =====

    /**
     * 특정 기수의 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Notification> findByGeneration(Integer generation) {
        log.debug("알림 목록 조회 - generation={}", generation);
        return notificationRepository.findByGeneration(generation);
    }
}
```

### 2.4 Exception 단순화

#### Before (현재 - 2개 파일)

```java
// NotificationErrorCode.java (10개 이상의 에러 코드)
public enum NotificationErrorCode {
    INVALID_EMAIL_FORMAT(...),

    EMAIL_REQUIRED(...),

    INVALID_GENERATION_NOT_POSITIVE(...),

    GENERATION_REQUIRED(...),

    DUPLICATE_NOTIFICATION(...);
    // ...
}

// NotificationDomainException.java
public class NotificationDomainException extends RuntimeException {
    private final NotificationErrorCode errorCode;

    public static NotificationDomainException emailInvalidFormat(String email) { ...}

    public static NotificationDomainException emailRequired() { ...}

    public static NotificationDomainException duplicateNotification(...) { ...}
    // ...
}
```

#### After (변경 후 - 1개 파일)

```java
package sopt.org.homepage.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 중복 알림 등록 시 발생하는 예외
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateNotificationException extends RuntimeException {

    public DuplicateNotificationException(String email, Integer generation) {
        super(String.format(
                "이미 등록된 알림입니다. (이메일: %s, 기수: %d)",
                email, generation
        ));
    }
}
```

**이메일/기수 검증은?**

- `@Email`, `@Min(1)` 어노테이션 → `MethodArgumentNotValidException`
- 전역 예외 핸들러에서 처리 (이미 존재)

### 2.5 Controller 수정

#### After (변경 후)

```java
package sopt.org.homepage.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.notification.dto.NotificationListResponse;
import sopt.org.homepage.notification.dto.RegisterNotificationRequest;
import sopt.org.homepage.notification.dto.RegisterNotificationResponse;

import java.util.List;

@Tag(name = "Notification", description = "모집 알림 API")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "모집 알림 신청")
    @PostMapping("/register")
    public ResponseEntity<RegisterNotificationResponse> register(
            @Valid @RequestBody RegisterNotificationRequest request
    ) {
        Notification notification = notificationService.register(
                request.email(),
                request.generation()
        );
        return ResponseEntity.ok(RegisterNotificationResponse.from(notification));
    }

    @Operation(summary = "모집 알림 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<NotificationListResponse> getList(
            @RequestParam Integer generation
    ) {
        List<Notification> notifications = notificationService.findByGeneration(generation);
        return ResponseEntity.ok(NotificationListResponse.from(notifications));
    }
}
```

### 2.6 DTO 수정

```java
// RegisterNotificationResponse.java
package sopt.org.homepage.notification.dto;

import sopt.org.homepage.notification.Notification;
import java.time.LocalDateTime;

public record RegisterNotificationResponse(
        Long id,
        String email,
        Integer generation,
        LocalDateTime createdAt
) {
    public static RegisterNotificationResponse from(Notification notification) {
        return new RegisterNotificationResponse(
                notification.getId(),
                notification.getEmail(),           // ✅ 직접 접근 (VO 없음)
                notification.getGeneration(),      // ✅ 직접 접근 (VO 없음)
                notification.getCreatedAt()
        );
    }
}

// NotificationListResponse.java
package sopt.org.homepage.notification.dto;

import sopt.org.homepage.notification.Notification;
import java.util.List;

public record NotificationListResponse(
        Integer generation,
        List<String> emailList
) {
    public static NotificationListResponse from(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            return new NotificationListResponse(null, List.of());
        }

        Integer generation = notifications.get(0).getGeneration();  // ✅ 직접 접근
        List<String> emailList = notifications.stream()
                .map(Notification::getEmail)                          // ✅ 직접 접근
                .toList();

        return new NotificationListResponse(generation, emailList);
    }
}
```

---

## 3. 테스트 재작성

### 3.1 통합 테스트 (단위 테스트 삭제)

```java
package sopt.org.homepage.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.notification.exception.DuplicateNotificationException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Notification 통합 테스트
 *
 * 인수인계 목적:
 * - 테스트를 읽으면 비즈니스 규칙을 이해할 수 있음
 * - 시나리오 기반 테스트
 */
@DisplayName("알림 서비스 통합 테스트")
class NotificationServiceTest extends IntegrationTestBase {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    // ===== 등록 시나리오 =====

    @Nested
    @DisplayName("알림 등록")
    class Register {

        @Test
        @DisplayName("✅ 정상: 유효한 이메일과 기수로 알림 등록")
        void register_WithValidInput_Success() {
            // given
            String email = "test@sopt.org";
            Integer generation = 35;

            // when
            Notification result = notificationService.register(email, generation);

            // then
            assertThat(result.getId()).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getGeneration()).isEqualTo(generation);
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("❌ 실패: 동일 이메일+기수 중복 등록 불가")
        void register_WithDuplicate_ThrowsException() {
            // given
            String email = "duplicate@sopt.org";
            Integer generation = 35;
            notificationService.register(email, generation);

            // when & then
            assertThatThrownBy(() -> notificationService.register(email, generation))
                    .isInstanceOf(DuplicateNotificationException.class)
                    .hasMessageContaining("이미 등록된 알림")
                    .hasMessageContaining(email);
        }

        @Test
        @DisplayName("✅ 정상: 같은 이메일이지만 다른 기수는 등록 가능")
        void register_SameEmailDifferentGeneration_Success() {
            // given
            String email = "test@sopt.org";
            notificationService.register(email, 35);

            // when
            Notification result = notificationService.register(email, 36);

            // then
            assertThat(result.getGeneration()).isEqualTo(36);
            assertThat(notificationRepository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("✅ 정상: 다른 이메일이지만 같은 기수는 등록 가능")
        void register_DifferentEmailSameGeneration_Success() {
            // given
            Integer generation = 35;
            notificationService.register("user1@sopt.org", generation);

            // when
            Notification result = notificationService.register("user2@sopt.org", generation);

            // then
            assertThat(result.getEmail()).isEqualTo("user2@sopt.org");
            assertThat(notificationRepository.count()).isEqualTo(2);
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("알림 조회")
    class FindByGeneration {

        @Test
        @DisplayName("✅ 조회: 특정 기수의 알림 목록")
        void findByGeneration_ReturnsMatchingNotifications() {
            // given - 35기 2건, 36기 1건
            notificationService.register("user1@sopt.org", 35);
            notificationService.register("user2@sopt.org", 35);
            notificationService.register("user3@sopt.org", 36);

            // when
            List<Notification> result = notificationService.findByGeneration(35);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(n -> n.getGeneration().equals(35));
        }

        @Test
        @DisplayName("✅ 조회: 등록된 알림이 없으면 빈 목록")
        void findByGeneration_WhenEmpty_ReturnsEmptyList() {
            // given - 아무것도 등록 안 함

            // when
            List<Notification> result = notificationService.findByGeneration(99);

            // then
            assertThat(result).isEmpty();
        }
    }
}
```

---

## 4. 삭제 대상 파일

```
❌ 삭제 대상:
├── domain/vo/Email.java
├── domain/vo/Generation.java
├── exception/NotificationDomainException.java
├── exception/NotificationErrorCode.java
├── service/NotificationCommandService.java
├── service/NotificationQueryService.java (통합)

❌ 삭제 대상 테스트:
├── domain/NotificationTest.java (통합 테스트로 대체)
├── domain/vo/EmailTest.java
├── domain/vo/GenerationTest.java
├── service/command/NotificationCommandServiceTest.java (통합)
├── service/query/NotificationQueryServiceTest.java (통합)
```

---

## 5. 체크리스트

### 5.1 작업 전

- [ ] feature 브랜치 생성 (feature/refactor-notification-light)
- [ ] 현재 테스트 모두 통과 확인

### 5.2 작업 중

- [ ] Entity 수정 (VO → @Valid)
- [ ] Repository 통합
- [ ] Service 통합
- [ ] Exception 단순화
- [ ] Controller 수정
- [ ] DTO 수정
- [ ] 테스트 재작성
- [ ] 불필요한 파일 삭제
- [ ] 각 단계마다 테스트 실행

### 5.3 작업 후

- [ ] 모든 테스트 통과
- [ ] API 정상 동작 확인
    - [ ] POST /notification/register
    - [ ] GET /notification/list
- [ ] PR 리뷰 요청

### 5.4 파일럿 리뷰 포인트

- [ ] 통합 테스트만으로 충분한 커버리지인가?
- [ ] 단순화된 구조가 이해하기 쉬운가?
- [ ] 다른 Light 도메인에 동일 패턴 적용 가능한가?

---

## 6. 예상 효과

| 항목          | Before | After | 개선            |
|-------------|--------|-------|---------------|
| 파일 수        | 12개    | 7개    | **42% 감소**    |
| VO 클래스      | 2개     | 0개    | **100% 제거**   |
| 에러코드        | 6개     | 0개    | **전역 핸들러 사용** |
| Service 클래스 | 2개     | 1개    | **50% 감소**    |
| 테스트 파일      | 5개     | 1개    | **80% 감소**    |

---

## 7. 다음 단계

Notification 파일럿 완료 후:

1. **패턴 리뷰** - 팀원들과 Light 패턴 확정
2. **다른 도메인 적용** - CoreValue, FAQ, Generation 순으로
3. **Phase 4** - Full DDD 도메인(Review, SoptStory) 테스트 보강

---

**작성일:** 2024년
**담당:** 리팩토링 v2 진행자
