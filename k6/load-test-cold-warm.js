import http from 'k6/http';
import {check, group, sleep} from 'k6';
import {Counter, Trend} from 'k6/metrics';

// ============================================
// 커스텀 메트릭
// ============================================
const coldLatency = new Trend('cold_start_latency', true);
const warmLatency = new Trend('warm_request_latency', true);
const coldCount = new Counter('cold_request_count');
const warmCount = new Counter('warm_request_count');

const VUS = Number(__ENV.VUS || 10);
const ITERATIONS = Number(__ENV.ITERATIONS || 20);


// ============================================
// 테스트 설정
// ============================================
//
// 이 스크립트는 Cold Start(캐시 미스)와 Warm(캐시 히트)를 분리 측정합니다.
//
// Lambda 환경에서 의미 있는 측정을 위해:
// - 각 VU의 첫 요청 = Cold (새 Lambda 인스턴스 or 캐시 비어있음)
// - 이후 요청 = Warm (동일 인스턴스에서 캐시 히트 기대)
//
// VU를 높이면 여러 Lambda 인스턴스가 생성되어
// Cold Start 비율이 증가하는 것을 관측할 수 있음
//
export const options = {
    scenarios: {
        cold_warm_test: {
            executor: 'per-vu-iterations',
            vus: VUS,               // VU 수 (env로 조절 가능)
            iterations: ITERATIONS, // VU당 반복 횟수 (env로 조절 가능)


        },
    },
    thresholds: {
        warm_request_latency: ['p(95)<1000'],   // Warm P95 < 1초
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
    console.log('Cold/Warm Split Measurement');
    console.log(`Target URL: ${PROJECTS_URL}`);
    console.log(`VUs: ${VUS}`);
    console.log(`Iterations per VU: ${ITERATIONS}`);
    console.log('========================================');

    return {projectsUrl: PROJECTS_URL};
}

// ============================================
// 메인 테스트 로직
// ============================================
export default function (data) {
    const iteration = __ITER;   // 현재 VU 내 반복 횟수 (0부터 시작)
    const vuId = __VU;          // 현재 VU ID

    if (iteration === 0) {
        // ─── Cold Start: 각 VU의 첫 번째 요청 ───
        // Lambda에서는 새 인스턴스 할당 가능 → 캐시 비어있음
        group('Cold Start', function () {
            const res = http.get(data.projectsUrl, {
                tags: {name: 'GET /projects [cold]', type: 'cold'},
                timeout: '30s',        // Cold Start는 시간이 더 걸릴 수 있음
            });

            check(res, {
                'cold: status 200': (r) => r.status === 200,
            });

            coldLatency.add(res.timings.duration);
            coldCount.add(1);

            console.log(`[VU ${vuId}] Cold: ${res.timings.duration.toFixed(0)}ms (status: ${res.status})`);
        });

        // Cold → Warm 전환 대기 (캐시 저장 완료 보장)
        sleep(1);

    } else {
        // ─── Warm Request: 이후 요청 ───
        // 동일 인스턴스라면 캐시 히트 기대
        group('Warm Request', function () {
            const res = http.get(data.projectsUrl, {
                tags: {name: 'GET /projects [warm]', type: 'warm'},
                timeout: '10s',
            });

            check(res, {
                'warm: status 200': (r) => r.status === 200,
                'warm: response time < 500ms': (r) => r.timings.duration < 500,
            });

            warmLatency.add(res.timings.duration);
            warmCount.add(1);
        });

        sleep(0.3);
    }
}

// ============================================
// 테스트 종료 후 요약
// ============================================
export function teardown(data) {
    console.log('========================================');
    console.log('Cold/Warm test completed.');
    console.log(`Target: ${data.projectsUrl}`);
    console.log('========================================');
}
