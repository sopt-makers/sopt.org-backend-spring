# 📊 리팩토링 v2 - 코드베이스 분석 결과

> Phase 0: [ANALYSIS] 현재 코드베이스 분석 및 복잡도 분류

---

## 1. 도메인별 현재 상태

### 1.1 전체 도메인 목록

| 도메인                         | v1 리팩토링   | VO 사용                              | Command/Query 분리 | 테스트   | 복잡도 판정       |
|-----------------------------|-----------|------------------------------------|------------------|-------|--------------|
| **Notification**            | ✅ Full 적용 | Email, Generation                  | ✅ 분리됨            | 단위+통합 | 🟡 **Light** |
| **Review**                  | ✅ Full 적용 | 5개 VO                              | ✅ 분리됨            | 단위+통합 | 🟢 **Full**  |
| **SoptStory**               | ✅ Full 적용 | LikeCount, Content, Url, IpAddress | ✅ 분리됨            | 단위+통합 | 🟢 **Full**  |
| **CoreValue**               | ✅ 분리됨     | ❌ 없음                               | ✅ 분리됨            | ❌ 없음  | 🟡 **Light** |
| **Member**                  | ✅ 분리됨     | SnsLinks, MemberRole               | ✅ 분리됨            | ❌ 없음  | 🟡 **Light** |
| **Part**                    | ✅ 분리됨     | ❌ 없음                               | ✅ 분리됨            | ❌ 없음  | 🟡 **Light** |
| **Generation**              | ✅ 분리됨     | BrandingColor                      | ✅ 분리됨            | ❌ 없음  | 🟡 **Light** |
| **Recruitment**             | ✅ 분리됨     | Schedule, RecruitType              | ✅ 분리됨            | ❌ 없음  | 🟡 **Light** |
| **RecruitPartIntroduction** | ✅ 분리됨     | PartIntroduction                   | ✅ 분리됨            | ❌ 없음  | 🟡 **Light** |
| **FAQ**                     | ✅ 분리됨     | QuestionAnswer (embedded)          | ✅ 분리됨            | ❌ 없음  | 🟡 **Light** |
| **News**                    | ❌ 레거시     | ❌ 없음                               | ❌ 단일 Service     | ❌ 없음  | 🟡 **Light** |
| **Homepage**                | -         | -                                  | Query만           | ❌ 없음  | 🔵 **조합**    |
| **Admin**                   | -         | -                                  | Command 위주       | ❌ 없음  | 🔵 **조합**    |

---

## 2. 비즈니스 규칙 분석

### 2.1 Full DDD 유지 대상 (복잡한 비즈니스 규칙)

#### ✅ Review 도메인

**비즈니스 규칙:**

```java
// 카테고리-세부주제 조건부 검증 (핵심!)
subjects.validateForCategory(category);

// ReviewSubjects.java
public void validateForCategory(ReviewCategory category) {
    if (category.requiresSubActivities() && isEmpty()) {
        throw new InvalidReviewSubjectException(
                "전체활동 카테고리는 세부 활동이 필수입니다."
        );
    }
    if (category.isRecruitingCategory() && isEmpty()) {
        throw new InvalidReviewSubjectException(
                "서류/면접 카테고리는 세부 유형이 필수입니다."
        );
    }
}
```

**복잡도 체크리스트:**

- ✅ 조건부 검증: "전체활동이면 세부활동 필수", "서류/면접이면 세부유형 필수"
- ✅ 여러 필드 간 관계: category ↔ subjects 관계
- ❌ 상태 변화 로직: 없음
- ❌ 계산 로직: 없음

**결론:** 2개 이상 충족 → **Full DDD 유지**

---

#### ✅ SoptStory 도메인

**비즈니스 규칙:**

```java
// 좋아요 증감 규칙
public LikeCount increment() {
    if (value >= MAX_COUNT) {
        throw new IllegalStateException("좋아요 개수가 최대값에 도달했습니다.");
    }
    return new LikeCount(this.value + 1);
}

public LikeCount decrement() {
    if (value <= MIN_COUNT) {
        throw new IllegalStateException("좋아요 개수는 음수가 될 수 없습니다.");
    }
    return new LikeCount(this.value - 1);
}

// IP 기반 중복 좋아요 방지
if(soptStoryLikeCommandRepository.

existsBySoptStory_IdAndIpAddress_Value(...)){
        throw new

AlreadyLikedException(...);
}
```

**복잡도 체크리스트:**

- ❌ 조건부 검증: 없음
- ✅ 상태 변화 로직: 좋아요 증감, 음수 불가
- ❌ 여러 필드 간 관계: 없음 (LikeCount 단독)
- ❌ 계산 로직: 단순 증감 (복잡하지 않음)

**추가 규칙:**

- ✅ IP 기반 중복 좋아요 방지 (비즈니스 규칙)
- ✅ 불변 VO로 상태 관리 (LikeCount.increment() → 새 객체 반환)

**결론:** 상태 변화 + 도메인 불변식 → **Full DDD 유지**
**결론 (v2 재평가):** 상태 변화가 있으나 int 증감 수준. 조건부 검증, 필드 간 관계, 복잡한 계산 모두 없음. → **Light로 전환**
---

### 2.2 Light 단순화 대상 (단순 CRUD)

#### 🟡 Notification 도메인

**현재 상태:**

```java
// Email VO - 단순 형식 검증
public Email(String value) {
    if (!EMAIL_PATTERN.matcher(value).matches()) {
        throw NotificationDomainException.emailInvalidFormat(value);
    }
    this.value = value;
}

// Generation VO - 단순 양수 검증
public Generation(Integer value) {
    if (value == null || value <= 0) {
        throw NotificationDomainException.generationNotPositive(value);
    }
    this.value = value;
}
```

**복잡도 체크리스트:**

- ❌ 조건부 검증: 없음 (단순 형식/범위 검증만)
- ❌ 상태 변화 로직: 없음
- ❌ 여러 필드 간 관계: 없음
- ❌ 계산 로직: 없음

**문제점:**

- `@Email`, `@Min(1)` 어노테이션으로 충분
- 전용 VO + 전용 예외 + 에러코드 = 과잉 엔지니어링
- 파일 수: 약 10개 (단순 CRUD 치고 과다)

**결론:** 모든 항목 미충족 → **Light로 단순화**

---

#### 🟡 CoreValue, Member, Part, Generation, Recruitment, FAQ 도메인

**공통 특징:**

- 단순 CRUD 작업
- Admin에서 벌크 생성/삭제
- 복잡한 비즈니스 규칙 없음

**CoreValue 예시:**

```java
// 단순 빌더 패턴으로 생성
CoreValue coreValue = CoreValue.builder()
                .generationId(command.generationId())
                .value(data.value())
                .description(data.description())
                .imageUrl(data.imageUrl())
                .displayOrder(data.displayOrder())
                .build();
```

**결론:** 모두 **Light로 단순화**

---

#### 🟡 News 도메인

**현재 상태:**

- 레거시 구조 (리팩토링 안 됨)
- 단일 NewsService
- Lambda 환경 대응 추가 (Presigned URL)

**결론:** **Light로 정리**

---

### 2.3 조합 서비스 (Application Layer)

#### 🔵 Homepage 도메인

**역할:**

```java
// 여러 도메인 Query 서비스 조합
public MainPageResponse getMainPageData() {
    GenerationDetailView generation = generationQueryService.getLatestGeneration();
    List<CoreValueView> coreValues = coreValueQueryService.getCoreValuesByGeneration(...);
    List<MemberDetailView> members = memberQueryService.getMembersByGeneration(...);
    // ... 조합하여 응답 생성
}
```

**결론:** **application/ 패키지로 이동**

---

#### 🔵 Admin 도메인

**역할:**

```java
// 여러 도메인 Command 서비스 호출
coreValueCommandService.bulkCreateCoreValues(...);
memberCommandService.

bulkCreateMembers(...);
partCommandService.

bulkCreateParts(...);
recruitmentCommandService.

bulkCreateRecruitments(...);
```

**결론:** **application/ 패키지로 이동**

---

## 3. Full vs Light 최종 분류

### 3.1 분류 결과

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         최종 분류 결과                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  🟢 Full DDD 유지 (2개)                                                  │
│  ├── Review       : 카테고리-세부주제 조건부 검증                         │
│  └── SoptStory    : 좋아요 증감 규칙, IP 중복 체크                        │
│                                                                         │
│  🟡 Light 단순화 (9개)                                                   │
│  ├── Notification : VO 제거, @Valid 전환                                │
│  ├── CoreValue    : Command/Query 통합                                  │
│  ├── Member       : Command/Query 통합 (SnsLinks VO 유지 검토)          │
│  ├── Part         : Command/Query 통합                                  │
│  ├── Generation   : Command/Query 통합 (BrandingColor VO 유지 검토)     │
│  ├── Recruitment  : Command/Query 통합                                  │
│  ├── RecruitPartIntroduction : Command/Query 통합                       │
│  ├── FAQ          : Command/Query 통합                                  │
│  └── News         : Light로 정리 (레거시)                               │
│                                                                         │
│  🔵 조합 서비스 (2개)                                                    │
│  ├── Homepage     : application/ 패키지로 이동                          │
│  └── Admin        : application/ 패키지로 이동                          │
│                                                                         │
│  ⚪ 외부 연동 (리팩토링 범위 외)                                          │
│  ├── Project      : Playground API 호출 위주                            │
│  ├── Internal     : auth, crew, playground 연동                         │
│  └── Scrap        : 외부 URL 스크래핑                                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.2 VO 유지 검토

일부 Light 도메인의 VO는 **여러 필드를 묶는 용도**로 유지 가치가 있음:

| 도메인                     | VO               | 유지 여부 | 이유                                        |
|-------------------------|------------------|-------|-------------------------------------------|
| Member                  | SnsLinks         | ✅ 유지  | email, linkedin, github, behance 4개 필드 묶음 |
| Generation              | BrandingColor    | ✅ 유지  | main, high, low, point 4개 필드 묶음           |
| Recruitment             | Schedule         | ✅ 유지  | 6개 일정 필드 묶음                               |
| RecruitPartIntroduction | PartIntroduction | ✅ 유지  | content, preference 묶음                    |
| FAQ                     | QuestionAnswer   | ✅ 유지  | question, answer 묶음 (JSON 저장)             |

**제거 대상 VO:**

| 도메인          | VO         | 제거 이유                |
|--------------|------------|----------------------|
| Notification | Email      | `@Email` 어노테이션으로 충분  |
| Notification | Generation | `@Min(1)` 어노테이션으로 충분 |

---

## 4. 테스트 현황

### 4.1 현재 테스트 파일

| 도메인          | 단위 테스트                             | 통합 테스트                                 | 상태        |
|--------------|------------------------------------|----------------------------------------|-----------|
| Notification | ✅ NotificationTest, EmailTest      | ✅ CommandServiceTest, QueryServiceTest | 양호        |
| Review       | ✅ ReviewTest, ReviewSubjectsTest 등 | ✅ CommandServiceTest, QueryServiceTest | 양호        |
| SoptStory    | ✅ SoptStoryTest, LikeCountTest 등   | ✅ CommandServiceTest                   | 양호        |
| CoreValue    | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |
| Member       | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |
| Part         | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |
| Generation   | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |
| Recruitment  | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |
| FAQ          | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |
| News         | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |
| Homepage     | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |
| Admin        | ❌ 없음                               | ❌ 없음                                   | **작성 필요** |

### 4.2 테스트 인프라

```java
// IntegrationTestBase.java 존재
@SpringBootTest
@Testcontainers
public abstract class IntegrationTestBase {
    @Container
    static PostgreSQLContainer<?> postgres = ...
}
```

---

## 5. 현재 패키지 구조

```
sopt.org.homepage/
├── admin/                 # 조합 서비스 (여러 도메인 Command 호출)
│   ├── AdminController.java
│   ├── dto/
│   └── service/
│       ├── AdminService.java
│       └── AdminServiceImpl.java
├── homepage/              # 조합 서비스 (여러 도메인 Query 조합)
│   ├── controller/
│   └── service/
│       └── HomepageQueryService.java
├── notification/          # Full DDD 적용됨 (단순화 대상)
│   ├── domain/
│   │   ├── Notification.java
│   │   └── vo/
│   │       ├── Email.java
│   │       └── Generation.java
│   ├── repository/
│   │   └── NotificationCommandRepository.java  # Query도 여기에
│   ├── service/
│   │   ├── NotificationCommandService.java
│   │   └── NotificationQueryService.java
│   ├── controller/
│   └── exception/
│       ├── NotificationDomainException.java
│       └── NotificationErrorCode.java
├── review/                # Full DDD 적용됨 (유지)
│   ├── domain/
│   │   ├── Review.java
│   │   └── vo/
│   │       ├── ReviewCategory.java
│   │       ├── ReviewContent.java
│   │       ├── ReviewSubjects.java
│   │       ├── ReviewAuthor.java
│   │       └── ReviewUrl.java
│   ├── repository/
│   │   ├── command/
│   │   └── query/
│   ├── infrastructure/
│   │   └── repository/
│   │       └── query/
│   ├── service/
│   │   ├── command/
│   │   └── query/
│   ├── controller/
│   └── exception/
├── soptstory/             # Full DDD 적용됨 (유지)
│   ├── domain/
│   │   ├── SoptStory.java
│   │   ├── SoptStoryLike.java
│   │   └── vo/
│   │       ├── LikeCount.java
│   │       ├── SoptStoryContent.java
│   │       ├── SoptStoryUrl.java
│   │       └── IpAddress.java
│   ├── repository/
│   │   ├── command/
│   │   └── query/
│   ├── service/
│   │   ├── command/
│   │   └── query/
│   ├── controller/
│   └── exception/
├── corevalue/             # Command/Query 분리됨 (단순화 대상)
├── member/                # Command/Query 분리됨 (단순화 대상)
├── part/                  # Command/Query 분리됨 (단순화 대상)
├── generation/            # Command/Query 분리됨 (단순화 대상)
├── recruitment/           # Command/Query 분리됨 (단순화 대상)
├── faq/                   # Command/Query 분리됨 (단순화 대상)
├── news/                  # 레거시 구조 (정리 대상)
├── project/               # 외부 API 래핑
├── internal/              # 외부 연동 (auth, crew, playground)
├── scrap/                 # 스크래핑
├── common/                # 공통 (dto, type 등)
├── config/                # 설정
├── exception/             # 전역 예외
├── cache/                 # 캐시
└── aws/                   # AWS 연동
```

---

## 6. DB 스키마 현황

### 6.1 주요 테이블

| 테이블                     | 사용 도메인       | 상태     |
|-------------------------|--------------|--------|
| Notification            | Notification | ✅ 사용 중 |
| Review                  | Review       | ✅ 사용 중 |
| SoptStory               | SoptStory    | ✅ 사용 중 |
| SoptStoryLike           | SoptStory    | ✅ 사용 중 |
| Generation              | Generation   | ✅ 사용 중 |
| CoreValue               | CoreValue    | ✅ 사용 중 |
| Member                  | Member       | ✅ 사용 중 |
| Part                    | Part         | ✅ 사용 중 |
| Recruitment             | Recruitment  | ✅ 사용 중 |
| RecruitPartIntroduction | Recruitment  | ✅ 사용 중 |
| FAQ                     | FAQ          | ✅ 사용 중 |
| MainNews                | News         | ✅ 사용 중 |

### 6.2 Flyway 현황

```
src/main/resources/db/migration/
└── V1__init_notification_table.sql  # Notification 테이블만 있음
```

**참고:** 다른 테이블은 JPA ddl-auto 또는 기존 마이그레이션으로 생성된 것으로 추정

---

## 7. 리팩토링 우선순위 및 예상 작업량

### 7.1 작업량 예측

| 도메인              | 작업 유형            | 예상 시간 | 복잡도         |
|------------------|------------------|-------|-------------|
| **Notification** | VO 제거 + 통합       | 2-3시간 | 낮음          |
| CoreValue        | Command/Query 통합 | 1-2시간 | 낮음          |
| FAQ              | Command/Query 통합 | 1-2시간 | 낮음          |
| Generation       | Command/Query 통합 | 1-2시간 | 낮음          |
| Member           | Command/Query 통합 | 1-2시간 | 낮음          |
| Part             | Command/Query 통합 | 1-2시간 | 낮음          |
| Recruitment      | Command/Query 통합 | 2-3시간 | 중간 (2개 엔티티) |
| News             | Light로 정리        | 1-2시간 | 낮음          |
| Review           | 테스트 보강           | 2-3시간 | 낮음          |
| SoptStory        | 테스트 보강           | 2-3시간 | 낮음          |
| Homepage         | 패키지 이동           | 1-2시간 | 낮음          |
| Admin            | 패키지 이동           | 1-2시간 | 낮음          |

### 7.2 권장 순서

```
1️⃣ Notification (파일럿) - Light 패턴 확립
2️⃣ CoreValue, FAQ - 가장 단순, 의존성 없음
3️⃣ Generation - 다른 도메인이 참조하지만 자체는 단순
4️⃣ Member, Part - Generation 의존
5️⃣ Recruitment - 2개 엔티티라 약간 복잡
6️⃣ News - 레거시 정리
7️⃣ Review, SoptStory - 테스트 보강
8️⃣ Homepage, Admin - 패키지 이동
```

---

## 8. 결론

### 8.1 핵심 발견

1. **과잉 엔지니어링:** Notification의 Email/Generation VO는 불필요
2. **일관성 없음:** 일부 도메인만 Full DDD, 나머지는 중간 상태
3. **테스트 부족:** Full DDD 도메인 외에는 테스트 없음
4. **VO 유지 가치:** 여러 필드 묶음용 VO는 유지할 가치 있음

### 8.2 다음 단계

- [ ] Phase 1: 테스트 인프라 표준화
- [ ] Phase 2: 패키지 구조 정리 (global, infrastructure)
- [ ] Phase 3: Notification 단순화 (파일럿)
- [ ] 이후 순차적으로 진행

---

**작성일:** 2025년 12월 21일

