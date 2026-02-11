import http from 'k6/http';
import {check} from 'k6';
import {Counter, Rate, Trend} from 'k6/metrics';

// ============================================
// 커스텀 메트릭
// ============================================
const errorRate = new Rate('error_rate');
const projectsLatency = new Trend('projects_latency', true); // true = 시간 단위 사용
const successCount = new Counter('success_count');
const failCount = new Counter('fail_count');

// ============================================
// 테스트 설정
// ============================================
//
// 환경변수:
//   BASE_URL  - 대상 서버 URL (기본: http://localhost:8080)
//   CTX_PATH  - context-path (기본: /v2, Lambda는 빈 문자열)
//
// 사용 예시:
//   [EC2]    k6 run --env BASE_URL=http://localhost:8080 --env CTX_PATH=/v2 load-test-projects.js
//   [Lambda] k6 run --env BASE_URL=https://<api-gw-url> --env CTX_PATH= load-test-projects.js
//
export const options = {
    scenarios: {
        // ─── 시나리오: Constant Arrival Rate ───
        // 초당 고정 요청률로 부하를 가함
        // 서버 응답과 무관하게 일정한 부하를 유지하므로
        // 캐시 유무에 따른 처리 능력 차이를 명확히 비교 가능
        constant_load: {
            executor: 'constant-arrival-rate',
            rate: 30,                // 초당 30 요청
            timeUnit: '1s',
            duration: '2m',          // 2분간 유지 (총 약 3,600 요청)
            preAllocatedVUs: 50,     // 초기 VU 수
            maxVUs: 100,             // 최대 VU 수
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<3000'],   // P95 < 3초
        error_rate: ['rate<0.05'],           // 에러율 < 5%
    },
};

// ============================================
// 환경변수 기반 URL 구성
// ============================================
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const CTX_PATH = __ENV.CTX_PATH !== undefined ? __ENV.CTX_PATH : '/v2';
const PROJECTS_URL = `${BASE_URL}${CTX_PATH}/projects`;

// ============================================
// 테스트 시작 시 설정 출력
// ============================================
export function setup() {
    console.log('========================================');
    console.log(`Target URL: ${PROJECTS_URL}`);
    console.log(`Rate: ${options.scenarios.constant_load.rate} req/s`);
    console.log(`Duration: ${options.scenarios.constant_load.duration}`);
    console.log('========================================');

    // 사전 확인: 서버 응답 가능 여부
    const res = http.get(PROJECTS_URL);
    if (res.status !== 200) {
        console.error(`❌ Pre-check failed! Status: ${res.status}`);
        console.error(`   URL: ${PROJECTS_URL}`);
        console.error(`   Body: ${res.body ? res.body.substring(0, 200) : 'empty'}`);
    } else {
        console.log(`✅ Pre-check passed. Status: ${res.status}`);
    }

    return {projectsUrl: PROJECTS_URL};
}

// ============================================
// 메인 테스트 로직
// ============================================
export default function (data) {
    const res = http.get(data.projectsUrl, {
        tags: {name: 'GET /projects'},
        timeout: '10s',
    });

    // 응답 검증
    const passed = check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 1000ms': (r) => r.timings.duration < 1000,
        'response time < 3000ms': (r) => r.timings.duration < 3000,
        'has response body': (r) => r.body && r.body.length > 0,
    });

    // 커스텀 메트릭 기록
    projectsLatency.add(res.timings.duration);
    errorRate.add(!passed);

    if (passed) {
        successCount.add(1);
    } else {
        failCount.add(1);
    }
}

// ============================================
// 테스트 종료 후 요약
// ============================================
export function teardown(data) {
    console.log('========================================');
    console.log('Load test completed.');
    console.log(`Target: ${data.projectsUrl}`);
    console.log('========================================');
}
