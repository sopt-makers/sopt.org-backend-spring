/**
 * SOPT 공식 홈페이지 TPS 고정 부하 테스트 스크립트
 *
 * 이전 스크립트(load-test-homepage.js)와의 차이:
 *   - 이전: ramping-vus    → 사람 수(VU)를 조절, TPS는 서버 성능에 따라 변동
 *   - 이번: constant-arrival-rate → TPS를 직접 고정, 서버가 못 버티면 에러율 상승
 *
 * 목적:
 *   "우리 서버는 몇 TPS까지 버티는가?"를 수치로 확인합니다.
 *   1 TPS = 초당 1개의 API 요청 (다른 팀과 동일한 기준)
 *
 * 사용법:
 *   # Steady Test: 100 TPS로 5분간 고정 테스트
 *   k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=steady --env TARGET_TPS=100 --out web-dashboard=open k6/load-test-tps.js
 *
 *   # Ramp-up Test: 10 TPS부터 200 TPS까지 점진적으로 올려서 한계 탐색
 *   k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=rampup --out web-dashboard=open k6/load-test-tps.js
 *
 * 환경변수:
 *   BASE_URL    - 대상 서버 URL (기본값: https://api.sopt.org)
 *   SCENARIO    - steady | rampup (기본값: steady)
 *   TARGET_TPS  - 목표 TPS, steady 모드에서만 사용 (기본값: 100)
 */

import http from 'k6/http';
import { check } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// ============================================
// 커스텀 메트릭
// ============================================

const errorRate    = new Rate('error_rate');
const successCount = new Counter('success_count');
const failCount    = new Counter('fail_count');

const latencyHomepage      = new Trend('latency_homepage',       true);
const latencyRecruit       = new Trend('latency_recruit',        true);
const latencyProjects      = new Trend('latency_projects',       true);
const latencyReviews       = new Trend('latency_reviews',        true);
const latencyReviewsRandom = new Trend('latency_reviews_random', true);
const latencySoptstory     = new Trend('latency_soptstory',      true);
const latencyVisitor       = new Trend('latency_visitor',        true);

// ============================================
// 환경변수 파싱
// ============================================

const BASE_URL    = __ENV.BASE_URL    || 'https://api.sopt.org';
const SCENARIO    = __ENV.SCENARIO    || 'steady';
const TARGET_TPS  = parseInt(__ENV.TARGET_TPS || '100');

const V2 = `${BASE_URL}/v2`;

// ============================================
// 시나리오 정의
//
// [steady]
//   TARGET_TPS를 5분간 유지합니다.
//   "우리 서버가 N TPS를 안정적으로 처리할 수 있는가?" 확인용
//   → 에러율이 1% 미만이면 합격
//
// [rampup]
//   10 TPS부터 200 TPS까지 단계적으로 올립니다.
//   "어느 TPS에서부터 에러가 발생하는가?" 한계점 탐색용
//   → 에러율이 올라가기 시작하는 TPS가 서버 한계
// ============================================

const SCENARIOS = {

    // ─── Steady Test ──────────────────────────────────────────────
    // 목표 TPS를 5분간 유지
    // TARGET_TPS 환경변수로 조절 (기본 100 TPS)
    steady: {
        executor: 'constant-arrival-rate',
        rate: TARGET_TPS,           // 초당 TARGET_TPS개 요청
        timeUnit: '1s',
        duration: '5m',
        preAllocatedVUs: TARGET_TPS * 2,   // 충분한 VU 미리 확보
        maxVUs: TARGET_TPS * 4,
    },

    // ─── Ramp-up Test ─────────────────────────────────────────────
    // 10 TPS → 200 TPS까지 단계적으로 올려서 한계점 탐색
    // 에러율이 올라가기 시작하는 구간이 서버 한계
    rampup: {
        executor: 'ramping-arrival-rate',
        startRate: 10,
        timeUnit: '1s',
        stages: [
            { target: 50,  duration: '1m' },  // 10 → 50 TPS
            { target: 50,  duration: '1m' },  // 50 TPS 유지
            { target: 100, duration: '1m' },  // 50 → 100 TPS
            { target: 100, duration: '2m' },  // 100 TPS 유지
            { target: 150, duration: '1m' },  // 100 → 150 TPS
            { target: 150, duration: '2m' },  // 150 TPS 유지
            { target: 200, duration: '1m' },  // 150 → 200 TPS
            { target: 200, duration: '2m' },  // 200 TPS 유지
            { target: 0,   duration: '1m' },  // 회복 확인
        ],
        preAllocatedVUs: 200,
        maxVUs: 500,
    },
};

// ============================================
// 합격 기준 (Thresholds)
// ============================================

export const options = {
    scenarios: {
        main: SCENARIOS[SCENARIO] || SCENARIOS.steady,
    },
    thresholds: {
        http_req_duration: [
            'p(95)<2000',  // 95%의 요청이 2초 내에 완료
            'p(99)<5000',  // 99%의 요청이 5초 내에 완료
            'avg<1000',    // 평균 응답시간 1초 미만
        ],
        error_rate: ['rate<0.01'],  // 에러율 1% 미만

        latency_homepage:       ['p(95)<2000'],
        latency_recruit:        ['p(95)<2000'],
        latency_projects:       ['p(95)<3000'],
        latency_reviews:        ['p(95)<3000'],
        latency_reviews_random: ['p(95)<2000'],
        latency_soptstory:      ['p(95)<2000'],
        latency_visitor:        ['p(95)<1000'],
    },
};

// ============================================
// API 목록 및 가중치
//
// 1 iteration = 1 API 호출 (1 TPS = 초당 1 요청)
// 실제 트래픽 비율을 반영한 가중치로 API를 선택합니다.
//
// 가중치 기준 (합계 100):
//   homepage       20 — 모든 방문자 호출
//   visitor        15 — 모든 방문자 호출
//   recruit        15 — 모집 시즌 높은 트래픽
//   reviews        15 — 활동후기 탭
//   reviews/random 15 — 메인 페이지 호출
//   projects       10 — 프로젝트 탭
//   soptstory      10 — 솝트스토리 탭
// ============================================

const APIS = [
    { weight: 20, method: 'GET',  path: '/homepage',       metric: latencyHomepage,      name: 'homepage'       },
    { weight: 15, method: 'POST', path: '/visitor',        metric: latencyVisitor,       name: 'visitor'        },
    { weight: 15, method: 'GET',  path: '/homepage/recruit', metric: latencyRecruit,     name: 'recruit'        },
    { weight: 15, method: 'GET',  path: '/reviews',        metric: latencyReviews,       name: 'reviews'        },
    { weight: 15, method: 'GET',  path: '/reviews/random', metric: latencyReviewsRandom, name: 'reviews/random' },
    { weight: 10, method: 'GET',  path: '/projects',       metric: latencyProjects,      name: 'projects'       },
    { weight: 10, method: 'GET',  path: '/soptstory',      metric: latencySoptstory,     name: 'soptstory'      },
];

// 가중치 기반 누적합 배열 생성 (매 iteration 재계산 방지)
const CUMULATIVE_WEIGHTS = (() => {
    let sum = 0;
    return APIS.map(api => { sum += api.weight; return sum; });
})();
const TOTAL_WEIGHT = CUMULATIVE_WEIGHTS[CUMULATIVE_WEIGHTS.length - 1];

// ============================================
// 테스트 시작 (setup)
// ============================================

export function setup() {
    console.log('='.repeat(50));
    console.log('SOPT 공식 홈페이지 TPS 고정 부하 테스트');
    console.log(`시나리오: ${SCENARIO}`);
    if (SCENARIO === 'steady') {
        console.log(`목표 TPS: ${TARGET_TPS} req/s`);
    } else {
        console.log('목표 TPS: 10 → 200 (단계적 증가)');
    }
    console.log(`대상 서버: ${V2}`);
    console.log('='.repeat(50));

    const res = http.get(`${BASE_URL}/v2/health`);
    if (res.status !== 200) {
        console.error(`❌ 사전 확인 실패! Status: ${res.status}`);
    } else {
        console.log(`✅ 사전 확인 통과. 서버 정상 응답 (${res.status})`);
    }

    return { baseUrl: V2 };
}

// ============================================
// 메인 테스트 로직
//
// 1 iteration = 1 API 호출
// → rate=100이면 초당 100번 이 함수가 실행됨 = 100 TPS
// ============================================

export default function (data) {
    const api = selectApi();
    const url = `${data.baseUrl}${api.path}`;

    const res = api.method === 'POST'
        ? http.post(url, null, { tags: { name: `POST ${api.path}` }, timeout: '10s' })
        : http.get(url,        { tags: { name: `GET ${api.path}` },  timeout: '10s' });

    const passed = check(res, {
        [`${api.name}: status 200`]:     (r) => r.status === 200,
        [`${api.name}: has body`]:       (r) => r.body && r.body.length > 0,
    });

    api.metric.add(res.timings.duration);
    errorRate.add(!passed);

    if (passed) {
        successCount.add(1);
    } else {
        failCount.add(1);
        console.error(`[FAIL] ${api.method} ${api.path} — Status: ${res.status}, ${res.timings.duration.toFixed(0)}ms`);
    }
}

// ============================================
// 테스트 종료 (teardown)
// ============================================

export function teardown(data) {
    console.log('='.repeat(50));
    console.log('테스트 완료');
    console.log(`시나리오: ${SCENARIO}`);
    console.log(`대상 서버: ${data.baseUrl}`);
    console.log('='.repeat(50));
    console.log('결과를 GitHub 이슈에 docs/load-testing-guide.md 템플릿으로 기록해주세요.');
}

// ============================================
// 헬퍼: 가중치 기반으로 API 선택
// ============================================

function selectApi() {
    const rand = Math.random() * TOTAL_WEIGHT;
    for (let i = 0; i < CUMULATIVE_WEIGHTS.length; i++) {
        if (rand < CUMULATIVE_WEIGHTS[i]) return APIS[i];
    }
    return APIS[APIS.length - 1];
}
