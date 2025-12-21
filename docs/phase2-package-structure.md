# 📦 Phase 2: 패키지 구조 1차 정리

> #4 [REFACTOR] 패키지 구조 1차 정리

---

## 1. 현재 vs 목표 구조

### 1.1 현재 구조

```
sopt.org.homepage/
├── admin/                 # 조합 서비스
├── aws/                   # 인프라
│   └── s3/
├── cache/                 # 인프라
├── common/                # 공통
│   ├── constants/
│   ├── dto/
│   ├── filter/
│   ├── mapper/
│   ├── type/
│   └── util/
├── config/                # 설정
├── corevalue/             # 도메인
├── exception/             # 전역 예외
├── faq/                   # 도메인
├── generation/            # 도메인
├── homepage/              # 조합 서비스
├── internal/              # 외부 연동
│   ├── auth/
│   ├── crew/
│   └── playground/
├── member/                # 도메인
├── news/                  # 도메인
├── notification/          # 도메인
├── part/                  # 도메인
├── project/               # 도메인 (외부 API 래핑)
├── recruitment/           # 도메인
├── review/                # 도메인
├── scrap/                 # 인프라 (스크래핑)
├── soptstory/             # 도메인
└── visitor/               # 서비스
```

### 1.2 목표 구조

```
sopt.org.homepage/
│
├── global/                        # 🌐 전역 공통
│   ├── common/
│   │   ├── constants/            # CacheType, SecurityConstants 등
│   │   ├── dto/                  # PaginateResponseDto 등
│   │   ├── filter/               # JwtAuthenticationFilter 등
│   │   ├── mapper/               # ResponseMapper 등
│   │   ├── type/                 # PartType 등
│   │   └── util/                 # ArrayUtil 등
│   ├── config/                   # 모든 @Configuration
│   │   ├── CacheConfig.java
│   │   ├── OpenApiConfig.java
│   │   ├── QueryDslConfig.java
│   │   ├── S3Config.java
│   │   ├── SecurityConfig.java
│   │   └── AuthConfig.java
│   └── exception/                # 전역 예외 핸들러
│       ├── GlobalExceptionHandler.java
│       ├── ClientBadRequestException.java
│       └── ...
│
├── infrastructure/               # 🔧 인프라 계층
│   ├── aws/                      # AWS 연동
│   │   └── s3/
│   │       ├── S3Service.java
│   │       └── S3ServiceImpl.java
│   ├── cache/                    # 캐시
│   │   ├── CacheService.java
│   │   └── CacheServiceImpl.java
│   └── external/                 # 외부 API 연동 (internal → external)
│       ├── auth/
│       │   ├── AuthService.java
│       │   └── AuthServiceImpl.java
│       ├── crew/
│       │   ├── CrewService.java
│       │   └── CrewClient.java
│       ├── playground/
│       │   ├── PlaygroundService.java
│       │   ├── PlaygroundServiceImpl.java
│       │   └── PlaygroundClient.java
│       └── scrap/
│           ├── ScraperService.java
│           └── ScraperServiceImpl.java
│
├── application/                  # 📱 응용 서비스 (조합)
│   ├── admin/                   # Admin 벌크 작업
│   │   ├── AdminController.java
│   │   ├── AdminService.java
│   │   ├── AdminServiceImpl.java
│   │   └── dto/
│   ├── homepage/                # 페이지별 응답 조합
│   │   ├── HomepageController.java
│   │   ├── HomepageQueryService.java
│   │   └── dto/
│   └── visitor/                 # 방문자 카운팅
│       ├── VisitorController.java
│       ├── VisitorService.java
│       └── dto/
│
└── domain/                       # 🎯 도메인 계층
    ├── notification/            # Light
    ├── corevalue/               # Light
    ├── faq/                     # Light
    ├── generation/              # Light
    ├── member/                  # Light
    ├── part/                    # Light
    ├── recruitment/             # Light
    ├── news/                    # Light
    ├── project/                 # 외부 API 래핑
    ├── review/                  # Full DDD
    └── soptstory/               # Full DDD
```

---

## 2. 변경 단계별 가이드

### 2.1 Step 1: global/ 패키지 생성

```bash
# 디렉토리 생성
mkdir -p src/main/java/sopt/org/homepage/global/common
mkdir -p src/main/java/sopt/org/homepage/global/config
mkdir -p src/main/java/sopt/org/homepage/global/exception
```

**이동 대상:**
| 원본 경로 | 목표 경로 |
|----------|----------|
| `common/` | `global/common/` |
| `config/` | `global/config/` |
| `exception/` | `global/exception/` |

**IDE 리팩토링 사용:**

```
IntelliJ: Refactor > Move (F6)
- 패키지 이동 시 자동으로 import 수정
```

### 2.2 Step 2: infrastructure/ 패키지 생성

```bash
mkdir -p src/main/java/sopt/org/homepage/infrastructure/aws
mkdir -p src/main/java/sopt/org/homepage/infrastructure/cache
mkdir -p src/main/java/sopt/org/homepage/infrastructure/external
```

**이동 대상:**
| 원본 경로 | 목표 경로 |
|----------|----------|
| `aws/` | `infrastructure/aws/` |
| `cache/` | `infrastructure/cache/` |
| `internal/auth/` | `infrastructure/external/auth/` |
| `internal/crew/` | `infrastructure/external/crew/` |
| `internal/playground/` | `infrastructure/external/playground/` |
| `scrap/` | `infrastructure/external/scrap/` |

**주의:** `internal` → `external`로 이름 변경

- 외부 API를 호출하므로 `external`이 더 적절

### 2.3 Step 3: application/ 패키지 생성

```bash
mkdir -p src/main/java/sopt/org/homepage/application/admin
mkdir -p src/main/java/sopt/org/homepage/application/homepage
mkdir -p src/main/java/sopt/org/homepage/application/visitor
```

**이동 대상:**
| 원본 경로 | 목표 경로 |
|----------|----------|
| `admin/` | `application/admin/` |
| `homepage/` | `application/homepage/` |
| `visitor/` | `application/visitor/` |

### 2.4 Step 4: domain/ 패키지 정리 (선택적)

도메인 패키지는 현재 위치에 그대로 두거나, `domain/` 하위로 이동 가능

**옵션 A: 현재 위치 유지**

```
sopt.org.homepage/
├── global/
├── infrastructure/
├── application/
├── notification/      # 도메인들 그대로
├── review/
├── soptstory/
└── ...
```

**옵션 B: domain/ 하위로 이동**

```
sopt.org.homepage/
├── global/
├── infrastructure/
├── application/
└── domain/
    ├── notification/
    ├── review/
    ├── soptstory/
    └── ...
```

**권장: 옵션 A (현재 위치 유지)**

- 이동량 최소화
- 기존 import 변경 최소화
- Phase 3에서 각 도메인 내부만 정리

---

## 3. 변경 후 import 수정 예시

### 3.1 common 패키지 변경

```java
// Before

import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.common.constants.CacheType;

// After
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.global.common.constants.CacheType;
```

### 3.2 config 패키지 변경

```java
// Before

import sopt.org.homepage.config.CacheConfig;
import sopt.org.homepage.config.SecurityConfig;

// After
import sopt.org.homepage.global.config.CacheConfig;
import sopt.org.homepage.global.config.SecurityConfig;
```

### 3.3 internal → external 변경

```java
// Before

import sopt.org.homepage.internal.playground.PlaygroundService;
import sopt.org.homepage.internal.auth.AuthService;

// After
import sopt.org.homepage.infrastructure.external.playground.PlaygroundService;
import sopt.org.homepage.infrastructure.external.auth.AuthService;
```

---

## 4. 체크리스트

### 4.1 이동 전

- [ ] 현재 브랜치에서 작업 (feature/refactor-package-structure)
- [ ] 모든 테스트 통과 확인
- [ ] IDE 리팩토링 기능 준비

### 4.2 이동 중

- [ ] Step 1: global/ 이동
    - [ ] common/ → global/common/
    - [ ] config/ → global/config/
    - [ ] exception/ → global/exception/
    - [ ] 테스트 실행 ✓

- [ ] Step 2: infrastructure/ 이동
    - [ ] aws/ → infrastructure/aws/
    - [ ] cache/ → infrastructure/cache/
    - [ ] internal/ → infrastructure/external/
    - [ ] scrap/ → infrastructure/external/scrap/
    - [ ] 테스트 실행 ✓

- [ ] Step 3: application/ 이동
    - [ ] admin/ → application/admin/
    - [ ] homepage/ → application/homepage/
    - [ ] visitor/ → application/visitor/
    - [ ] 테스트 실행 ✓

### 4.3 이동 후

- [ ] 모든 테스트 통과
- [ ] 빌드 성공 (`./gradlew build`)
- [ ] 서버 정상 기동
- [ ] 주요 API 동작 확인
- [ ] PR 생성 및 리뷰 요청

---

## 5. 예상 이슈 및 해결책

### 5.1 순환 참조

**문제:** global ↔ infrastructure 간 순환 참조

**해결:**

- global은 infrastructure를 참조하면 안 됨
- 필요 시 인터페이스 분리

### 5.2 Config 클래스 이동 시 Bean 인식 문제

**문제:** @ComponentScan 범위 밖으로 이동

**해결:**

```java
// HomepageApplication.java
@SpringBootApplication(scanBasePackages = "sopt.org.homepage")
public class HomepageApplication {
}
```

### 5.3 테스트 코드 import 깨짐

**문제:** 테스트 코드도 import 수정 필요

**해결:** IDE 리팩토링 시 테스트 코드도 함께 수정됨

---

## 6. 완료 기준

- [ ] 패키지 구조가 목표 구조와 일치
- [ ] 모든 테스트 통과
- [ ] 빌드 성공
- [ ] 기존 API 모두 정상 동작
- [ ] PR 리뷰 완료 및 머지

---


---

**작성일:** 2025년 12월 21일
