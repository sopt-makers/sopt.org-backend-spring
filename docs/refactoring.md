## 🎯 리팩토링 목표

### 현재 상태 (AS-IS)

**Anemic Domain Model 문제점:**

- Entity가 단순 데이터 컨테이너 (getter/setter만 존재)
- 모든 비즈니스 로직이 Service 계층에 집중
- Service 메서드가 평균 200-300줄
- 도메인 규칙이 private 메서드에 숨겨져 테스트 불가능
- 외부 의존성(DB, HTTP, 캐시)과 강결합
- **단위 테스트 작성 불가능** (통합 테스트만 가능)

**구체적 문제 예시:**

```java
// develop 브랜치 - ReviewServiceImpl.java
@Service
public class ReviewServiceImpl {
    // 300줄 이상의 복잡한 로직
    // 외부 의존성: Repository, ScraperService, PlaygroundService
    // 비즈니스 규칙이 private 메서드에 숨어있음
    // Mock 없이는 테스트 불가능
    
    private List getReviewSubject(...) {
        // 이게 진짜 비즈니스 규칙인데 private이라 테스트 못함
        if (mainCategory == ACTIVITY && subActivities == null) {
            throw new ClientBadRequestException("...");
        }
    }
}
```

### 목표 상태 (TO-BE)

**Rich Domain Model + CQRS:**

- Entity에 비즈니스 로직 포함 (행위가 있는 객체)
- Value Object로 도메인 규칙 캡슐화
- Service는 얇은 계층 (평균 50줄 이하)
- Command/Query 분리로 읽기/쓰기 책임 명확화
- **단위 테스트 가능** (Mock 없이 도메인 로직 테스트)
- **고전파 스타일 테스트** (TestContainer 활용)

**목표 구조 예시:**

```java
// Domain에 비즈니스 규칙
@Entity
@Table(name = "\"Review\"")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private ReviewCategory category;
    
    @Embedded
    private ReviewContent content;
    
    // 팩토리 메서드 - 생성 규칙
    public static Review create(
        ReviewCategory category,
        List<SubActivity> subActivities,
        ReviewContent content,
        String author
    ) {
        validateCreation(category, subActivities);
        return new Review(category, content, author);
    }
    
    // 비즈니스 규칙을 도메인이 스스로 검증
    private static void validateCreation(
        ReviewCategory category, 
        List<SubActivity> subActivities
    ) {
        if (category.requiresSubActivities() && subActivities.isEmpty()) {
            throw new InvalidReviewException("전체활동은 세부 활동이 필요합니다");
        }
    }
}
```
```java
// Service는 얇아짐
@Service
public class ReviewCommandService {
    public ReviewId createReview(CreateReviewCommand command) {
        Review review = Review.create(...);  // 도메인에 위임
        return reviewRepository.save(review).getId();
    }
}
```
```java
// 단위 테스트 가능!
class ReviewTest {
    @Test
    void 전체활동_카테고리는_세부활동이_필수다() {
        // given
        ReviewCategory category = new ReviewCategory(CategoryType.ACTIVITY);
        List<SubActivity> emptyActivities = List.of();
        
        // when & then
        assertThatThrownBy(() -> Review.create(category, emptyActivities, ...))
            .isInstanceOf(InvalidReviewException.class)
            .hasMessage("전체활동은 세부 활동이 필요합니다");
    }
}
```

---

## 🏗️ 아키텍처 원칙

### 패키지 구조 표준

```
[domain]/
├─ domain/
│  ├─ [Entity].java              # Rich Domain Entity (@Entity)
│  └─ vo/
│     ├─ [ValueObject1].java     # Value Object (@Embeddable)
│     └─ [ValueObject2].java
├─ repository/
│  ├─ command/
│  │  └─ [Entity]CommandRepository.java
│  └─ query/
│     └─ [Entity]QueryRepository.java
├─ infrastructure/
│  └─ repository/
│     └─ query/
│        └─ [Entity]QueryRepositoryImpl.java  # QueryDSL 구현
├─ service/
│  ├─ command/
│  │  ├─ [Entity]CommandService.java
│  │  └─ dto/
│  │     ├─ Create[Entity]Command.java
│  │     └─ Edit[Entity]Command.java
│  └─ query/
│     ├─ [Entity]QueryService.java
│     └─ dto/
│        ├─ [Entity]SearchCond.java
│        ├─ [Entity]SummaryView.java
│        └─ [Entity]DetailView.java
└─ controller/
   ├─ [Entity]CommandController.java
   ├─ [Entity]QueryController.java
   └─ dto/
      ├─ Create[Entity]Req.java
      ├─ [Entity]Res.java
      └─ ...
```

### 핵심 원칙

#### 1. Rich Domain Model

```java
// ✅ Good: 도메인이 스스로 검증
@Entity
public class Review {
    public static Review create(ReviewCategory category, ...) {
        if (category.requiresSubActivities() && activities.isEmpty()) {
            throw new InvalidReviewException("세부 활동이 필요합니다");
        }
        return new Review(...);
    }
}

// ❌ Bad: Service에서 검증
@Service
public class ReviewService {
    public void create(...) {
        if (category == ACTIVITY && activities.isEmpty()) {
            throw new Exception(...);
        }
    }
}
```

#### 2. Value Object (VO)

```java
// ✅ Good: 불변 객체, 자가 검증
@Embeddable
public class Email {
    @Column(name = "email")
    private final String value;
    
    protected Email() {}  // JPA용
    
    public Email(String value) {
        validate(value);
        this.value = value;
    }
    
    private void validate(String value) {
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailException("유효하지 않은 이메일 형식입니다");
        }
    }
    
    // equals, hashCode 구현 필수
}

// ❌ Bad: String으로 검증 없이 사용
@Entity
public class Review {
    private String email;  // 검증 없음, 불변성 없음
}
```

#### 3. CQRS

```java
// ✅ Good: 읽기/쓰기 분리
@Service
public class ReviewCommandService {
    public ReviewId create(CreateReviewCommand command) {
        Review review = Review.create(...);
        return reviewRepository.save(review).getId();
    }
}

@Service
public class ReviewQueryService {
    public List<ReviewSummary> search(ReviewSearchCond cond) {
        return reviewQueryRepository.search(cond);
    }
}

// ❌ Bad: 한 Service에 모든 로직
@Service
public class ReviewService {
    public void create(...) { }
    public void update(...) { }
    public List search(...) { }
    public Review findById(...) { }
    // 300줄...
}
```

#### 4. 단위 테스트 우선

```java
// ✅ Good: Mock 없이 테스트 가능
class LikeCountTest {
    @Test
    void 최대_좋아요_도달시_예외() {
        // given
        LikeCount count = new LikeCount(10_000);
        
        // when & then
        assertThatThrownBy(() -> count.increment())
            .isInstanceOf(MaxLikeExceededException.class);
    }
}

// ✅ Good: TestContainer로 통합 테스트
@SpringBootTest
@Testcontainers
class ReviewCommandServiceTest {
    @Container
    static PostgreSQLContainer postgres = 
        new PostgreSQLContainer<>("postgres:15");
    
    @Autowired
    ReviewCommandService service;
    
    @Autowired
    ReviewRepository repository;
    
    @Test
    void 리뷰_생성_후_조회_가능() {
        // given
        CreateReviewCommand command = ...;
        
        // when
        ReviewId id = service.create(command);
        
        // then
        Review saved = repository.findById(id).get();
        assertThat(saved.getTitle()).isEqualTo("expected");
    }
}
```

---

## 📋 요약

이 리팩토링의 핵심은 **비즈니스 로직을 도메인으로 이동**하여:

1. **테스트 가능한 코드** 작성
2. **유지보수성** 향상
3. **책임 분리** 명확
