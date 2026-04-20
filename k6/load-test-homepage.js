/**
 * SOPT 공식 홈페이지 부하 테스트 스크립트
 *
 * 목적:
 *   서버 통합(t3.small 단일 인스턴스, 다중 컨테이너) 이후
 *   실제 트래픽을 견딜 수 있는지 검증합니다.
 *
 * 사용법:
 *   # Smoke Test (기본 동작 확인, 1분)
 *   k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=smoke k6/load-test-homepage.js
 *
 *   # Load Test (정상 트래픽 시뮬레이션, 8분)
 *   k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=load k6/load-test-homepage.js
 *
 *   # Stress Test (한계점 탐색, 12분)
 *   k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=stress k6/load-test-homepage.js
 *
 *   # Spike Test (모집 시즌 트래픽 급증 시뮬레이션, 3분)
 *   k6 run --env BASE_URL=https://api.sopt.org --env SCENARIO=spike k6/load-test-homepage.js
 *
 * 환경변수:
 *   BASE_URL  - 대상 서버 URL (기본값: https://api.sopt.org)
 *   SCENARIO  - 테스트 시나리오 (smoke | load | stress | spike, 기본값: smoke)
 *
 * 주의:
 *   이 스크립트는 실제 prod 서버에 요청을 보냅니다.
 *   반드시 새벽 2~6시에 실행하고, 다른 makers 팀에 사전 공유하세요.
 */

import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// ============================================
// 커스텀 메트릭 정의
// ============================================

// 각 API별 응답시간을 분리해서 추적
const homepageLatency    = new Trend('latency_homepage',       true);
const recruitLatency     = new Trend('latency_recruit',        true);
const projectsLatency    = new Trend('latency_projects',       true);
const reviewsLatency     = new Trend('latency_reviews',        true);
const reviewsRandLatency = new Trend('latency_reviews_random', true);
const soptstoryLatency   = new Trend('latency_soptstory',      true);
const visitorLatency     = new Trend('latency_visitor',        true);

// 전체 에러율 및 성공/실패 카운터
const errorRate    = new Rate('error_rate');
const successCount = new Counter('success_count');
const failCount    = new Counter('fail_count');

// ============================================
// 환경변수 파싱
// ============================================

const BASE_URL = __ENV.BASE_URL || 'https://api.sopt.org';
const SCENARIO = __ENV.SCENARIO || 'smoke';

// context-path: prod는 /v2
const V2 = `${BASE_URL}/v2`;

// ============================================
// 시나리오 정의
//
// stages: VU(가상 사용자) 수를 시간에 따라 조절합니다.
//   target: 목표 VU 수
//   duration: 해당 target까지 도달하거나 유지하는 시간
//
// ramping-vus executor:
//   stages에 따라 VU 수가 점진적으로 변합니다.
//   예) { target: 10, duration: '2m' } → 2분에 걸쳐 10명까지 증가
// ============================================

const SCENARIOS = {

    // ─── Smoke Test ───────────────────────────────────────────────
    // 목적: 스크립트와 서버 기본 동작 확인 (예비 점검)
    // VU 1명, 1분
    smoke: {
        executor: 'ramping-vus',
        stages: [
            { target: 1, duration: '30s' },  // 1명으로 시작
            { target: 1, duration: '30s' },  // 1명 유지
        ],
        gracefulRampDown: '10s',
    },

    // ─── Load Test ────────────────────────────────────────────────
    // 목적: 예상 정상 트래픽에서 서버 안정성 확인
    // 최대 10 VU, 약 8분
    load: {
        executor: 'ramping-vus',
        stages: [
            { target: 5,  duration: '1m' },  // 0→5명 (워밍업)
            { target: 10, duration: '1m' },  // 5→10명
            { target: 10, duration: '5m' },  // 10명 유지 (핵심 측정 구간)
            { target: 0,  duration: '1m' },  // 0명으로 감소 (쿨다운)
        ],
        gracefulRampDown: '30s',
    },

    // ─── Stress Test ──────────────────────────────────────────────
    // 목적: 서버 한계점 탐색 (어느 VU에서 응답시간/에러율이 나빠지는가)
    // 최대 40 VU, 약 12분
    stress: {
        executor: 'ramping-vus',
        stages: [
            { target: 10, duration: '2m' },  // 0→10명
            { target: 10, duration: '2m' },  // 10명 유지
            { target: 20, duration: '1m' },  // 10→20명
            { target: 20, duration: '2m' },  // 20명 유지
            { target: 40, duration: '1m' },  // 20→40명
            { target: 40, duration: '2m' },  // 40명 유지
            { target: 0,  duration: '2m' },  // 회복 구간 (부하 제거 후 정상화 확인)
        ],
        gracefulRampDown: '30s',
    },

    // ─── Spike Test ───────────────────────────────────────────────
    // 목적: 모집 공고 오픈 등 갑작스러운 트래픽 급증 시뮬레이션
    // 순간 최대 50 VU, 약 3분
    spike: {
        executor: 'ramping-vus',
        stages: [
            { target: 5,  duration: '30s' }, // 0→5명 (워밍업)
            { target: 50, duration: '30s' }, // 5→50명 (급격한 스파이크)
            { target: 50, duration: '1m' },  // 50명 유지
            { target: 5,  duration: '30s' }, // 50→5명 (급격한 감소)
            { target: 0,  duration: '30s' }, // 회복 확인
        ],
        gracefulRampDown: '10s',
    },
};

// ============================================
// 합격 기준 (Thresholds)
//
// 이 기준을 초과하면 k6가 종료 코드 1로 종료됩니다.
// CI/CD에 연동하면 자동으로 실패 처리할 수 있습니다.
// ============================================

export const options = {
    scenarios: {
        main: SCENARIOS[SCENARIO] || SCENARIOS.smoke,
    },
    thresholds: {
        // 전체 요청 응답시간
        http_req_duration: [
            'p(95)<2000',   // 95%의 요청이 2초 내에 완료
            'p(99)<5000',   // 99%의 요청이 5초 내에 완료
            'avg<1000',     // 평균 응답시간 1초 미만
        ],
        // 에러율
        error_rate: ['rate<0.01'],  // 에러율 1% 미만

        // API별 응답시간 (개별 모니터링용, 실패 처리는 http_req_duration에서)
        latency_homepage:       ['p(95)<2000'],
        latency_recruit:        ['p(95)<2000'],
        latency_projects:       ['p(95)<3000'], // 외부 API 호출 가능성으로 여유 있게
        latency_reviews:        ['p(95)<3000'],
        latency_reviews_random: ['p(95)<2000'],
        latency_soptstory:      ['p(95)<2000'],
        latency_visitor:        ['p(95)<1000'], // 단순 write, 빨라야 함
    },
};

// ============================================
// 테스트 시작 시 1회 실행 (setup)
// ============================================

export function setup() {
    console.log('='.repeat(50));
    console.log(`SOPT 공식 홈페이지 부하 테스트`);
    console.log(`시나리오: ${SCENARIO}`);
    console.log(`대상 서버: ${V2}`);
    console.log('='.repeat(50));

    // 사전 확인: 서버가 응답하는지 체크
    const healthRes = http.get(`${BASE_URL}/v2/health`);
    if (healthRes.status !== 200) {
        console.error(`❌ 사전 확인 실패! 서버가 응답하지 않습니다.`);
        console.error(`   URL: ${BASE_URL}/v2/health`);
        console.error(`   Status: ${healthRes.status}`);
        console.error(`   테스트를 중단하거나 URL을 확인하세요.`);
    } else {
        console.log(`✅ 사전 확인 통과. 서버 정상 응답 (${healthRes.status})`);
    }

    return { baseUrl: V2 };
}

// ============================================
// 메인 테스트 로직 (각 VU가 반복 실행)
//
// 각 VU는 실제 사용자의 행동 패턴을 시뮬레이션합니다:
//   1. 사이트 진입 → 메인 페이지 호출 + 방문자 수 증가
//   2. 탭 이동 → 프로젝트, 활동후기, 솝트스토리 중 하나 조회
//   3. 짧은 대기 (실제 사용자는 페이지를 읽는 시간이 있음)
// ============================================

export default function (data) {
    const url = data.baseUrl;

    // ─── Phase 1: 메인 페이지 진입 ───────────────────────────────
    // 모든 방문자가 공통으로 호출하는 API
    group('메인 페이지 진입', function () {

        // 방문자 수 증가 (페이지 진입 시 항상 호출)
        const visitorRes = http.post(`${url}/visitor`, null, {
            tags: { name: 'POST /visitor' },
            timeout: '10s',
        });
        recordResult(visitorRes, visitorLatency, 'visitor');

        // 메인 홈페이지 데이터 조회
        const homepageRes = http.get(`${url}/homepage`, {
            tags: { name: 'GET /homepage' },
            timeout: '10s',
        });
        recordResult(homepageRes, homepageLatency, 'homepage');

        // 랜덤 활동후기 조회 (메인 페이지에 표시될 수 있음)
        const reviewsRandRes = http.get(`${url}/reviews/random`, {
            tags: { name: 'GET /reviews/random' },
            timeout: '10s',
        });
        recordResult(reviewsRandRes, reviewsRandLatency, 'reviews/random');
    });

    // 페이지 로딩 후 사용자가 내용을 읽는 시간 (0.5~1.5초)
    sleep(randomBetween(0.5, 1.5));

    // ─── Phase 2: 탭 이동 ────────────────────────────────────────
    // 사용자가 다양한 탭을 이동하는 패턴 시뮬레이션
    // 각 탭에 골고루 트래픽이 가도록 랜덤 선택
    const tabChoice = Math.random();

    if (tabChoice < 0.25) {
        // 25% 확률: 모집 페이지 (모집 시즌에 트래픽 높음)
        group('모집 페이지', function () {
            const res = http.get(`${url}/homepage/recruit`, {
                tags: { name: 'GET /homepage/recruit' },
                timeout: '10s',
            });
            recordResult(res, recruitLatency, 'homepage/recruit');
        });

    } else if (tabChoice < 0.50) {
        // 25% 확률: 프로젝트 탭
        group('프로젝트 탭', function () {
            const res = http.get(`${url}/projects`, {
                tags: { name: 'GET /projects' },
                timeout: '15s',  // 외부 API 호출 가능성으로 여유 있게
            });
            recordResult(res, projectsLatency, 'projects');
        });

    } else if (tabChoice < 0.75) {
        // 25% 확률: 활동후기 탭
        group('활동후기 탭', function () {
            const res = http.get(`${url}/reviews`, {
                tags: { name: 'GET /reviews' },
                timeout: '15s',
            });
            recordResult(res, reviewsLatency, 'reviews');
        });

    } else {
        // 25% 확률: 솝트스토리 탭
        group('솝트스토리 탭', function () {
            const res = http.get(`${url}/soptstory`, {
                tags: { name: 'GET /soptstory' },
                timeout: '10s',
            });
            recordResult(res, soptstoryLatency, 'soptstory');
        });
    }

    // 탭 간 이동 시 대기 (0.3~1초)
    sleep(randomBetween(0.3, 1.0));
}

// ============================================
// 테스트 종료 시 1회 실행 (teardown)
// ============================================

export function teardown(data) {
    console.log('='.repeat(50));
    console.log(`테스트 완료`);
    console.log(`시나리오: ${SCENARIO}`);
    console.log(`대상 서버: ${data.baseUrl}`);
    console.log('='.repeat(50));
    console.log('결과를 GitHub 이슈에 docs/load-testing-guide.md 템플릿으로 기록해주세요.');
}

// ============================================
// 헬퍼 함수
// ============================================

/**
 * 요청 결과를 메트릭에 기록하고 check를 실행합니다.
 *
 * @param {object} res - k6 http 응답 객체
 * @param {Trend} latencyMetric - 기록할 Trend 메트릭
 * @param {string} name - 로그 출력용 엔드포인트 이름
 */
function recordResult(res, latencyMetric, name) {
    const passed = check(res, {
        [`${name}: status is 200`]: (r) => r.status === 200,
        [`${name}: has response body`]: (r) => r.body && r.body.length > 0,
    });

    latencyMetric.add(res.timings.duration);
    errorRate.add(!passed);

    if (passed) {
        successCount.add(1);
    } else {
        failCount.add(1);
        // 실패 시 상세 로그 출력 (디버깅용)
        if (res.status !== 200) {
            console.error(`[FAIL] ${name} - Status: ${res.status}, Duration: ${res.timings.duration.toFixed(0)}ms`);
        }
    }
}

/**
 * min 이상 max 미만의 랜덤 숫자를 반환합니다. (sleep 시간에 사용)
 */
function randomBetween(min, max) {
    return Math.random() * (max - min) + min;
}
