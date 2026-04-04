# 부하 테스트 가이드

> **목적:** 서버 통합(t3.small 단일 인스턴스, 다중 컨테이너) 이후 SOPT 공식 홈페이지 API 서버가
> 실제 트래픽을 정상적으로 처리하는지 검증합니다.
>
> **대상 독자:** 부하 테스트 경험이 없는 팀원도 이해하고 실행할 수 있도록 작성되었습니다.
>
> **관련 문서:** [`load-test-results.md`](./load-test-results.md) — 테스트 결과 기록

---

## 목차

1. [부하 테스트란?](#1-부하-테스트란)
2. [우리가 부하 테스트를 하는 이유](#2-우리가-부하-테스트를-하는-이유)
3. [사전 준비](#3-사전-준비)
4. [테스트 대상 API](#4-테스트-대상-api)
5. [두 가지 테스트 방식](#5-두-가지-테스트-방식)
6. [측정 지표와 기준값](#6-측정-지표와-기준값)
7. [실행 방법](#7-실행-방법)
8. [결과 확인 방법](#8-결과-확인-방법)
9. [주의사항](#9-주의사항)
10. [결과 기록 템플릿](#10-결과-기록-템플릿)

---

## 1. 부하 테스트란?

### 비유로 이해하기

식당이 점심 피크 타임에 손님 100명을 동시에 받을 수 있는지 미리 확인하는 것과 같습니다.
실제 영업 전에 직원들과 함께 "100명 동시 서빙 모의 훈련"을 하는 것이죠.

**부하 테스트 = 실제 서비스 전에 서버가 많은 요청을 동시에 처리할 수 있는지 확인하는 것**

### 무엇을 확인하나?

- 요청이 많아져도 응답이 빠른가? (응답시간)
- 요청이 실패하지 않는가? (에러율)
- 서버가 뻗지 않는가? (안정성)

### k6란?

Grafana가 만든 오픈소스 부하 테스트 도구입니다. JavaScript로 테스트 시나리오를 작성하고,
수천 개의 가상 사용자(VU, Virtual User)를 시뮬레이션하여 서버에 동시 요청을 보냅니다.

### 동시 접속자란?

VU 50명은 50명이 **같은 순간에 서버에 요청을 보내고 있는 상태**입니다.

```
시간 →
VU1  [요청] → [대기] → [요청] → ...
VU2  [요청] → [대기] → [요청] → ...
...
VU50 [요청] → [대기] → [요청] → ...
```

실제 사용자는 페이지를 읽는 시간이 있으므로 "동시 접속자 1,000명 = 실질 동시 요청 약 50~100개" 수준입니다.
따라서 **VU 50명은 실제 동시 접속자 수백~수천 명에 해당하는 부하**입니다.

---

## 2. 우리가 부하 테스트를 하는 이유

### 현재 상황

```
[이전]                          [현재]
전용 EC2                        통합 EC2 (t3.small)
  └─ spring-prod-server           ├─ spring-prod-server  ← 우리 서비스
                                  ├─ [다른 makers 서비스]
                                  ├─ [다른 makers 서비스]
                                  └─ [...]
```

**핵심 문제:** t3.small (vCPU 2개, RAM 2GB)에 여러 컨테이너가 함께 올라가 있어,
우리 서비스에 허용된 자원이 이전보다 줄어들었습니다.

### 이번 테스트의 목표

1. **통합 이후 정상 동작 확인** — 일반적인 트래픽에서 응답시간과 에러율이 기준 이하인가?
2. **한계점 파악** — 몇 TPS까지 버틸 수 있는가?
3. **병목 지점 발견** — 느리거나 에러가 발생하는 엔드포인트가 있는가?

---

## 3. 사전 준비

### k6 설치 (Windows + Git Bash 환경)

> 이 가이드는 **Windows + Git Bash** 환경을 기준으로 작성되었습니다.

**방법 1 — winget (권장)**

```bash
winget install k6 --source winget
```

**방법 2 — chocolatey**

```bash
choco install k6
```

**방법 3 — 설치 파일 직접 다운로드**

`https://grafana.com/docs/k6/latest/set-up/install-k6/` 에서 `.msi` 설치

**설치 확인 (Git Bash 재시작 후)**

```bash
k6 version
# 출력 예시: k6 v0.54.0 (...)
```

**k6가 인식 안 될 경우 PATH 추가**

```bash
echo 'export PATH=$PATH:/c/Program\ Files/k6' >> ~/.bashrc && source ~/.bashrc
```

### 환경 확인

```bash
# prod 서버 응답 확인
curl https://api.sopt.org/v2/health
# HealthCheck Complete 가 출력되면 정상
```

### CloudWatch (권장)

테스트 중 서버 CPU/메모리를 같이 모니터링합니다:
- AWS 콘솔 → CloudWatch → 지표 → EC2 → 인스턴스별 지표
- `CPUUtilization`, `CPUCreditBalance` 확인

> t3.small은 **버스트 가능 인스턴스**입니다. `CPUCreditBalance`가 0에 가까워지면 성능이 급격히 저하됩니다.

---

## 4. 테스트 대상 API

### 선정 기준

공식 홈페이지의 특성상, **페이지를 열 때 자동으로 호출되는 조회성 API**가 트래픽의 대부분을 차지합니다.
관리자 API, 내부 연동 API는 일반 사용자가 호출하지 않으므로 제외합니다.

### 대상 API 목록

| 우선순위 | 메서드 | 경로 | 설명 | 선정 이유 |
|:---:|:---:|---|---|---|
| 🔴 높음 | GET | `/v2/homepage` | 메인 페이지 조회 | 모든 방문자가 가장 먼저 호출 |
| 🔴 높음 | GET | `/v2/homepage/recruit` | 모집 페이지 조회 | 모집 시즌에 트래픽 폭발적 증가 |
| 🟡 중간 | GET | `/v2/projects` | 프로젝트 목록 | 프로젝트 탭 진입 시 호출 |
| 🟡 중간 | GET | `/v2/reviews` | 활동후기 목록 | 활동후기 탭 진입 시 호출 |
| 🟡 중간 | GET | `/v2/reviews/random` | 랜덤 활동후기 | 메인 페이지에서 호출 |
| 🟡 중간 | GET | `/v2/soptstory` | 솝트스토리 목록 | 솝트스토리 탭 진입 시 호출 |
| 🟢 낮음 | POST | `/v2/visitor` | 방문자 수 증가 | 페이지 방문마다 호출 |

### 제외 API

- `/v2/admin/*` — 관리자 전용
- `/v2/notification/*` — 모집 시즌 한정
- `/v2/internal/*`, `/v2/s3/*` — 내부 연동용
- `/v2/health` — 헬스체크용

---

## 5. 두 가지 테스트 방식

목적이 다른 두 가지 방식으로 테스트를 진행합니다.

### 방식 비교

| 항목 | VU 기반 (방식 1) | TPS 고정 (방식 2) |
|---|---|---|
| 스크립트 | `k6/load-test-homepage.js` | `k6/load-test-tps.js` |
| 제어 대상 | 동시 접속자 수 (VU) | 초당 요청 수 (TPS) |
| 1 iteration | API 4개 호출 (사용자 1회 방문) | API 1개 호출 |
| TPS 변화 | 서버 성능에 따라 자동 변동 | 항상 고정 |
| 한계 발견 방식 | 응답시간이 늘어남 | 에러율이 올라감 |
| 적합한 용도 | 사용자 행동 시뮬레이션 | 서버 한계 TPS 수치 측정 |

---

### 방식 1 — VU 기반 (load-test-homepage.js)

**"사람 N명을 동시에 풀어놓고 실제 사용자처럼 행동하게 한다"**

VU 1명이 1번 방문할 때 하는 행동:
```
POST /visitor        (방문자 수 증가)
GET  /homepage       (메인 페이지)
GET  /reviews/random (랜덤 활동후기)
GET  /projects 또는 /reviews 또는 /soptstory 또는 /homepage/recruit 중 랜덤 1개
+ sleep (사용자가 페이지를 읽는 시간 시뮬레이션)
```

**시나리오 4단계:**

| 단계 | 최대 VU | 소요 시간 | 목적 |
|---|:---:|:---:|---|
| Smoke Test | 1명 | 1분 | 기본 동작 확인 (예비 점검) |
| Load Test | 10명 | 8분 | 정상 트래픽 안정성 검증 |
| Stress Test | 40명 | 12분 | 한계점 탐색 |
| Spike Test | 50명 | 3분 | 모집 공고 오픈 시 급증 시뮬레이션 |

---

### 방식 2 — TPS 고정 (load-test-tps.js)

**"컨베이어 벨트를 초당 N개 속도로 강제로 돌린다"**

1 TPS = 초당 1개의 API 요청. 서버가 느려져도 k6는 계속 같은 속도로 요청을 보냅니다.
서버가 못 버티면 에러율이 올라갑니다.

> **1 TPS = 초당 1개의 API 요청**으로 정의합니다.

7개 API를 실제 트래픽 비율에 따라 가중치를 두고 분산 호출합니다:

| API | 가중치 | 비율 |
|---|:---:|:---:|
| `GET /homepage` | 20 | 20% |
| `POST /visitor` | 15 | 15% |
| `GET /homepage/recruit` | 15 | 15% |
| `GET /reviews` | 15 | 15% |
| `GET /reviews/random` | 15 | 15% |
| `GET /projects` | 10 | 10% |
| `GET /soptstory` | 10 | 10% |

**시나리오 2종류:**

**Steady Test** — 목표 TPS를 5분간 유지
```
에러율 < 1% 이면 합격
권장 순서: 100 TPS → 150 TPS → 200 TPS 단계적으로 올리기
```

**Ramp-up Test** — 10 → 200 TPS 자동 단계적 증가 (약 12분)
```
0→1분:  10 → 50 TPS
1→2분:  50 TPS 유지
2→3분:  50 → 100 TPS
3→5분: 100 TPS 유지
5→6분: 100 → 150 TPS
6→8분: 150 TPS 유지
8→9분: 150 → 200 TPS
9→11분: 200 TPS 유지
11→12분: 0으로 감소 (회복 확인)
```
에러율이 올라가기 시작하는 TPS 구간이 서버 한계점입니다.

---

## 6. 측정 지표와 기준값

### 주요 지표

| 지표 | 의미 | 기준값 |
|---|---|:---:|
| `http_req_duration` p(95) | 100개 요청 중 95개가 이 시간 내 완료 | **< 2,000ms** |
| `http_req_duration` p(99) | 100개 요청 중 99개가 이 시간 내 완료 | **< 5,000ms** |
| `http_req_duration` avg | 평균 응답시간 | **< 1,000ms** |
| `error_rate` | 실패한 요청 비율 (4xx/5xx) | **< 1%** |
| `http_reqs /s` | 실제 처리된 TPS | 목표 TPS와 비교 |

> **P95가 중요한 이유:** 평균은 극단적으로 느린 요청 1개 때문에 왜곡될 수 있습니다.
> P95가 실제 사용자 경험을 더 정확하게 반영합니다.

### TPS 기반 테스트 한계점 판단 기준

| 상태 | 조건 |
|---|---|
| **안정** | 에러율 0%, P95 < 500ms |
| **주의** | 에러율 0~1%, P95 500~2,000ms |
| **한계 도달** | 에러율 > 1% 또는 P95 > 2,000ms |
| **서버 과부하** | 에러율 > 10% 또는 타임아웃 다수 |

### 서버 자원 기준 (CloudWatch)

| 지표 | 경고 | 위험 |
|---|:---:|:---:|
| CPU 사용률 | > 60% | > 80% |
| 메모리 사용률 | > 70% | > 85% |

---

## 7. 실행 방법

> **Git Bash 환경 기준. 줄 이어쓰기는 백슬래시(`\`)를 사용합니다.**

### 사전 확인

```bash
curl https://api.sopt.org/v2/health
```

---

### VU 기반 테스트 (load-test-homepage.js)

**반드시 Smoke → Load → Stress → Spike 순서로 진행합니다.**

#### Step 1. Smoke Test (1분)

```bash
k6 run \
  --env BASE_URL=https://api.sopt.org \
  --env SCENARIO=smoke \
  --out web-dashboard=open \
  k6/load-test-homepage.js
```

확인 포인트: 에러 0%, 모든 `✓` 표시

#### Step 2. Load Test (8분)

```bash
k6 run \
  --env BASE_URL=https://api.sopt.org \
  --env SCENARIO=load \
  --out web-dashboard=open \
  k6/load-test-homepage.js
```

확인 포인트: P95 < 2,000ms, 에러율 < 1%

#### Step 3. Stress Test (12분)

```bash
k6 run \
  --env BASE_URL=https://api.sopt.org \
  --env SCENARIO=stress \
  --out web-dashboard=open \
  k6/load-test-homepage.js
```

확인 포인트: 응답시간이 급격히 늘어나기 시작하는 VU 수, 부하 감소 후 회복 여부

#### Step 4. Spike Test (3분)

```bash
k6 run \
  --env BASE_URL=https://api.sopt.org \
  --env SCENARIO=spike \
  --out web-dashboard=open \
  k6/load-test-homepage.js
```

확인 포인트: 급증 구간 에러 발생 여부, 트래픽 감소 후 정상 회복 여부

---

### TPS 고정 테스트 (load-test-tps.js)

#### Steady Test — 목표 TPS 고정 (5분씩)

```bash
# 100 TPS (처음 시작 권장)
k6 run \
  --env BASE_URL=https://api.sopt.org \
  --env SCENARIO=steady \
  --env TARGET_TPS=100 \
  --out web-dashboard=open \
  k6/load-test-tps.js

# 합격 시 150 TPS
k6 run \
  --env BASE_URL=https://api.sopt.org \
  --env SCENARIO=steady \
  --env TARGET_TPS=150 \
  --out web-dashboard=open \
  k6/load-test-tps.js

# 합격 시 200 TPS
k6 run \
  --env BASE_URL=https://api.sopt.org \
  --env SCENARIO=steady \
  --env TARGET_TPS=200 \
  --out web-dashboard=open \
  k6/load-test-tps.js
```

확인 포인트: 실제 처리 TPS가 목표 TPS와 일치하는가, 에러율 < 1%

#### Ramp-up Test — 한계점 자동 탐색 (12분)

```bash
k6 run \
  --env BASE_URL=https://api.sopt.org \
  --env SCENARIO=rampup \
  --out web-dashboard=open \
  k6/load-test-tps.js
```

확인 포인트: 에러율이 올라가기 시작하는 TPS 구간

---

## 8. 결과 확인 방법

| 시점 | 방법 |
|---|---|
| **테스트 중 (실시간)** | 브라우저 자동 오픈 → `http://127.0.0.1:5665` |
| **테스트 후 (차트)** | 프로젝트 루트에 생성된 `report-날짜시간.html` 브라우저로 열기 |
| **테스트 후 (요약)** | 터미널 자동 출력 (`✓`/`✗` 표시) |

### 터미널 출력 읽는 법

```
✓ http_req_duration p(95)=54ms   ← 기준값 통과
✗ error_rate rate=3.5%           ← 기준값 초과 (문제 있음)
  http_reqs: 30000 / 100.0/s     ← 총 요청 수 / 초당 처리량
  dropped_iterations: 500        ← 서버가 처리 못한 요청 (TPS 테스트에서 한계 신호)
```

### 자주 보이는 문제와 원인

| 증상 | 가능한 원인 |
|---|---|
| 응답시간이 점점 느려짐 | DB 커넥션 풀 부족, 메모리 부족 |
| 갑자기 에러율 폭등 | CPU 버스트 크레딧 소진, OOM |
| 특정 API만 느림 | 해당 API의 DB 쿼리 또는 외부 API 호출 문제 |
| `dropped_iterations` 발생 | 서버가 목표 TPS를 따라가지 못함 |

---

## 9. 주의사항

> ⚠️ **이 테스트는 실제 prod 서버에 요청을 보냅니다.**

1. **시간대**: 반드시 새벽 2~6시에 진행합니다.
2. **점진적 시작**: Smoke → Load → Stress 순서로, 처음부터 높은 부하 금지.
3. **다른 컨테이너 모니터링**: CloudWatch로 인스턴스 전체 CPU/메모리 확인.
4. **긴급 중단**: 에러율이 10%를 넘으면 즉시 `Ctrl+C`.
5. **사전 공유**: 다른 makers 팀에 "n시에 부하 테스트 예정" 공유.
6. **DB 부하**: `POST /visitor`는 DB write 발생. Smoke Test에서 먼저 확인.

### HikariCP 커넥션 풀

```yaml
hikari:
  maximum-pool-size: 5
```

다른 서비스들과 DB를 공유하므로 전체 커넥션 합계가 DB `max_connections`를 초과하지 않도록 주의하세요.
`HikariPool-1 - Connection is not available` 에러가 뜨면 이 문제입니다.

---

## 10. 결과 기록 템플릿

테스트 완료 후 GitHub 이슈에 기록합니다.

### VU 기반 테스트 템플릿

```markdown
## VU 기반 부하 테스트 결과

**테스트 일시:** 20XX-XX-XX (새벽)
**스크립트:** k6/load-test-homepage.js

### Smoke Test
- 에러율: 0% ✅ / X% ❌ / 모든 check 통과: ✅ / ❌

### Load Test (최대 10 VU, 8분)
| 지표 | 결과 | 기준 | 합격 |
|---|---|---|:---:|
| P95 응답시간 | XXXms | < 2,000ms | ✅/❌ |
| 에러율 | X.X% | < 1% | ✅/❌ |
| 평균 응답시간 | XXXms | < 1,000ms | ✅/❌ |

### Stress Test (최대 40 VU, 12분)
- 응답시간 급증 시작 VU: 약 XX명
- 에러 발생 시작 VU: 약 XX명
- 부하 감소 후 정상 회복: ✅ / ❌

### Spike Test (최대 50 VU, 3분)
- 급증 구간 에러 발생: ✅ / ❌
- 급증 구간 P95: XXXms
- 트래픽 감소 후 정상 회복: ✅ / ❌
```

### TPS 고정 테스트 템플릿

```markdown
## TPS 고정 부하 테스트 결과

**테스트 일시:** 20XX-XX-XX (새벽)
**스크립트:** k6/load-test-tps.js

### Steady Test
| 목표 TPS | 실제 TPS | P95 | 에러율 | 판정 |
|---|---|---|---|:---:|
| 100 TPS | XX/s | XXms | X.X% | ✅/❌ |
| 150 TPS | XX/s | XXms | X.X% | ✅/❌ |
| 200 TPS | XX/s | XXms | X.X% | ✅/❌ |

### Ramp-up Test
- 에러율 1% 초과 시점: 약 XXX TPS
- dropped_iterations 발생 시점: 약 XXX TPS
- 부하 감소 후 정상 회복: ✅ / ❌

### 결론
- 안정적 처리 가능한 최대 TPS: **XXX TPS**
```

---

**작성일:** 2026-04-02
**스크립트:** `k6/load-test-homepage.js`, `k6/load-test-tps.js`
**결과:** `docs/load-test-results.md`
