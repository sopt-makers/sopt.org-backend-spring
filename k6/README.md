# 🚀 k6 부하 테스트

## 스크립트 종류

| 스크립트 | 목적 |
|---|---|
| `load-test-homepage.js` | **VU 기반 부하 테스트** — 사용자 수를 조절, 사용자 행동 시뮬레이션 |
| `load-test-tps.js` | **TPS 고정 부하 테스트** — TPS를 직접 지정, 서버 한계 탐색 |
| `load-test-projects.js` | 프로젝트 조회 API 캐시 전략 비교용 |
| `load-test-cold-warm.js` | Lambda Cold/Warm 분리 측정용 |
| `load-test-interval.js` | Lambda 간헐적 트래픽 시뮬레이션용 |

> 자세한 가이드는 [`docs/load-testing-guide.md`](../docs/load-testing-guide.md)를 참고하세요.

---

## load-test-homepage.js (서버 통합 부하 테스트)

t3.small 통합 인스턴스에서 SOPT 공식 홈페이지 API 서버의 부하를 측정합니다.

### k6 설치 (Windows + Git Bash)

```bash
# winget (권장)
winget install k6 --source winget

# 설치 확인 (Git Bash 재시작 후)
k6 version
```

### 실행 방법 (Git Bash)

`--out web-dashboard=open` 옵션을 붙이면 브라우저가 자동으로 열리며 실시간 대시보드가 표시됩니다.
테스트 종료 후에는 `report-날짜시간.html` 파일이 자동 생성됩니다.

```bash
# Smoke Test (1분, 기본 동작 확인) → 항상 먼저 실행
k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=smoke --out web-dashboard=open k6/load-test-homepage.js

# Load Test (8분, 정상 트래픽)
k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=load --out web-dashboard=open k6/load-test-homepage.js

# Stress Test (12분, 한계점 탐색)
k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=stress --out web-dashboard=open k6/load-test-homepage.js

# Spike Test (3분, 모집 시즌 급증 시뮬레이션)
k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=spike --out web-dashboard=open k6/load-test-homepage.js
```

### 결과 확인

| 시점 | 방법 |
|---|---|
| **테스트 중 (실시간)** | 브라우저 자동 오픈 → `http://127.0.0.1:5665` |
| **테스트 후 (차트)** | 프로젝트 루트에 생성된 `report-날짜시간.html` 브라우저로 열기 |
| **테스트 후 (요약)** | 터미널에 자동 출력되는 숫자 확인 (`✓`/`✗` 표시) |

> 서버 CPU/메모리는 k6 대시보드에서 볼 수 없습니다.
> AWS 콘솔 → CloudWatch → EC2 에서 별도로 확인하세요.

---

## load-test-tps.js (TPS 고정 부하 테스트)

`load-test-homepage.js`와 달리 **TPS를 직접 지정**합니다.
1 TPS = 초당 1개의 API 요청으로, 다른 팀과 동일한 기준으로 서버 한계를 측정합니다.

### 실행 방법 (Git Bash)

```bash
# Steady Test: 100 TPS로 5분간 고정 (기본값)
k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=steady --env TARGET_TPS=100 --out web-dashboard=open k6/load-test-tps.js

# Steady Test: 150 TPS로 테스트
k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=steady --env TARGET_TPS=150 --out web-dashboard=open k6/load-test-tps.js

# Ramp-up Test: 10 TPS → 200 TPS까지 단계적으로 올려서 한계 탐색 (약 12분)
k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=rampup --out web-dashboard=open k6/load-test-tps.js
```

### 두 스크립트 비교

| | load-test-homepage.js | load-test-tps.js |
|---|---|---|
| 방식 | VU 수 조절 | TPS 직접 지정 |
| 1 iteration | API 4개 호출 (사용자 1회 방문) | API 1개 호출 |
| TPS 변화 | 서버 성능에 따라 변동 | 항상 고정 |
| 적합한 용도 | 사용자 행동 시뮬레이션 | 서버 한계 TPS 측정 |

---

## load-test-projects.js (캐시 전략 비교)

프로젝트 조회 API(`GET /projects`) 캐시 전략별 성능 비교를 위한 k6 부하 테스트 스크립트입니다.

## 사전 준비
```bash
# k6 설치 (macOS)
brew install k6

# 버전 확인
k6 version
```

## 스크립트 목록

| 스크립트 | 목적 | 시나리오 |
|---|---|---|
| `load-test-projects.js` | 메인 부하 테스트 | 초당 30 요청, 2분간 (약 3,600 요청) |
| `load-test-cold-warm.js` | Cold/Warm 분리 측정 | VU 10개 × 20회 (200 요청) |
| `collect-metrics.sh` | Actuator 메트릭 수집 | 캐시 히트율, API 호출 횟수 |
| `load-test-interval.js` | 간헐적 트래픽 시뮬레이션 | 지정 간격으로 반복 호출 (Lambda 캐시 소멸 검증) |

## 실행 방법

### EC2 환경 (context-path: `/v2`)
```bash
# 메인 부하 테스트
k6 run --env BASE_URL=http://localhost:8080 --env CTX_PATH=/v2 k6/load-test-projects.js

# Cold/Warm 분리 측정
k6 run --env BASE_URL=http://localhost:8080 --env CTX_PATH=/v2 k6/load-test-cold-warm.js

# 메트릭 수집
chmod +x k6/collect-metrics.sh
./k6/collect-metrics.sh http://localhost:8080 /v2
```

### Lambda 환경 (context-path 없음)
```bash
# 메인 부하 테스트
k6 run --env BASE_URL=https://<API-GATEWAY-URL> --env CTX_PATH= k6/load-test-projects.js

# Cold/Warm 분리 측정
k6 run --env BASE_URL=https://<API-GATEWAY-URL> --env CTX_PATH= k6/load-test-cold-warm.js
```

### Lambda 간헐적 트래픽 시뮬레이션
```bash
# 10분 간격, 10회 반복 (총 약 90분 소요)
k6 run \
  --env BASE_URL=https://org-api-dev.sopt.org \
  --env CTX_PATH=/v2 \
  --env INTERVAL=600 \
  --env COUNT=10 \
  k6/load-test-interval.js

# 5분 간격으로 빠르게 테스트 (총 약 45분 소요)
k6 run \
  --env BASE_URL=https://org-api-dev.sopt.org \
  --env CTX_PATH=/v2 \
  --env INTERVAL=300 \
  --env COUNT=10 \
  k6/load-test-interval.js

# EC2 대조군 (동일 조건)
k6 run \
  --env BASE_URL=https://api-dev.sopt.org \
  --env CTX_PATH=/v2 \
  --env INTERVAL=600 \
  --env COUNT=10 \
  k6/load-test-interval.js
```

## 결과 확인

### k6 출력 주요 지표

http_req_duration .......: avg=XXms  min=XXms  med=XXms  max=XXms  p(90)=XXms  p(95)=XXms

- **avg**: 평균 응답시간
- **med (P50)**: 중앙값
- **p(95)**: 95번째 백분위
- **http_reqs**: 총 요청 수 / 초당 처리량(RPS)

### Actuator 메트릭
```bash
# 부하 테스트 후 수집
./k6/collect-metrics.sh http://localhost:8080 /v2
```

## 시나리오별 측정 워크플로우

1. 애플리케이션 재시작 (Micrometer 메트릭 초기화)
2. 사전 메트릭 수집 -   ./k6/collect-metrics.sh ...   # 값이 0인지 확인
3. k6 부하 테스트 실행 -    k6 run ...
4. 사후 메트릭 수집-    ./k6/collect-metrics.sh ...   # 히트율, 외부 API 호출 횟수 확인
5. 결과를 이슈 코멘트에 기록


## 테스트 조건 조정

`load-test-projects.js`의 `options.scenarios.constant_load`에서:
```javascript
rate: 30,          // 초당 요청 수 (트래픽에 따라 조정)
duration: '2m',    // 테스트 지속 시간
```

> ⚠️ Playground 외부 API에 과도한 부하를 주지 않도록,
> 캐시 미적용 시나리오에서는 rate를 낮추는 것을 권장합니다.


