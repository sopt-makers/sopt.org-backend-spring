import http from 'k6/http';
import {check, sleep} from 'k6';
import {Counter, Trend} from 'k6/metrics';

// ============================================
// 커스텀 메트릭
// ============================================
const responseLatency = new Trend('interval_latency', true);
const coldCount = new Counter('cold_count');
const warmCount = new Counter('warm_count');

// ============================================
// 테스트 설정
// ============================================
//
// 간헐적 트래픽 시뮬레이션:
//   일정 간격으로 API를 호출하여 Lambda 인스턴스 소멸 후
//   재호출 시 캐시가 유지되는지 검증한다.
//
// 환경변수:
//   BASE_URL  - 대상 서버 URL
//   CTX_PATH  - context-path (기본: /v2)
//   INTERVAL  - 호출 간격 초 (기본: 600 = 10분)
//   THRESHOLD - Cold 판별 임계값 ms (기본: 500)
//   COUNT     - 반복 횟수 (기본: 10)
//
// 사용 예시:
//   [Lambda 10분 간격]
//   k6 run --env BASE_URL=https://org-api-dev.sopt.org --env CTX_PATH=/v2 \
//          --env INTERVAL=600 --env COUNT=10 k6/load-test-interval.js
//
//   [Lambda 5분 간격]
//   k6 run --env BASE_URL=https://org-api-dev.sopt.org --env CTX_PATH=/v2 \
//          --env INTERVAL=300 --env COUNT=10 k6/load-test-interval.js
//
//   [EC2 대조군]
//   k6 run --env BASE_URL=https://api-dev.sopt.org --env CTX_PATH=/v2 \
//          --env INTERVAL=600 --env COUNT=10 k6/load-test-interval.js
//

const BASE_URL = __ENV.BASE_URL || 'https://org-api-dev.sopt.org';
const CTX_PATH = __ENV.CTX_PATH !== undefined ? __ENV.CTX_PATH : '/v2';
const PROJECTS_URL = `${BASE_URL}${CTX_PATH}/projects`;
const INTERVAL_SECONDS = parseInt(__ENV.INTERVAL || '600');
const COLD_THRESHOLD_MS = parseInt(__ENV.THRESHOLD || '500');
const ITERATION_COUNT = parseInt(__ENV.COUNT || '10');

export const options = {
    scenarios: {
        interval_test: {
            executor: 'per-vu-iterations',
            vus: 1,
            iterations: ITERATION_COUNT,
            maxDuration: '12h', // ✅ 추가 (충분히 크게)
        },
    },
    // 간격 테스트는 장시간 실행되므로 타임아웃을 넉넉하게
    // 10분 × 10회 = 100분 + 여유
    setupTimeout: '30s',
    teardownTimeout: '30s',
};

// ============================================
// 테스트 시작
// ============================================
export function setup() {
    const totalMinutes = ((ITERATION_COUNT - 1) * INTERVAL_SECONDS / 60).toFixed(0);

    console.log('========================================');
    console.log('🕐 Interval Traffic Simulation');
    console.log(`Target URL    : ${PROJECTS_URL}`);
    console.log(`Interval      : ${INTERVAL_SECONDS}s (${INTERVAL_SECONDS / 60}min)`);
    console.log(`Iterations    : ${ITERATION_COUNT}`);
    console.log(`Cold threshold: ${COLD_THRESHOLD_MS}ms`);
    console.log(`Estimated time: ~${totalMinutes} minutes`);
    console.log('========================================');

    // 사전 확인
    const res = http.get(PROJECTS_URL, {timeout: '30s'});
    if (res.status !== 200) {
        console.error(`❌ Pre-check failed! Status: ${res.status}`);
    } else {
        console.log(`✅ Pre-check passed. Status: ${res.status}, Duration: ${res.timings.duration.toFixed(0)}ms`);
    }

    return {
        projectsUrl: PROJECTS_URL,
        startTime: new Date().toISOString(),
    };
}

// ============================================
// 메인 테스트 로직
// ============================================
export default function (data) {
    const iteration = __ITER;
    const now = new Date().toLocaleTimeString('ko-KR', {hour12: false});

    // API 호출
    const res = http.get(data.projectsUrl, {
        timeout: '30s',
        tags: {name: 'GET /projects [interval]'},
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    const duration = res.timings.duration;
    responseLatency.add(duration);

    // Cold/Warm 판별
    const isCold = duration > COLD_THRESHOLD_MS;
    if (isCold) {
        coldCount.add(1);
    } else {
        warmCount.add(1);
    }

    const icon = isCold ? '🥶' : '🔥';
    const label = isCold ? 'COLD (cache miss)' : 'WARM (cache hit)';
    console.log(
        `[${now}] #${iteration + 1}/${ITERATION_COUNT} ${icon} ${label} — ${duration.toFixed(0)}ms`
    );

    // 마지막 반복이 아니면 간격 대기
    if (iteration < ITERATION_COUNT - 1) {
        const nextTime = new Date(Date.now() + INTERVAL_SECONDS * 1000)
            .toLocaleTimeString('ko-KR', {hour12: false});
        console.log(`⏳ Waiting ${INTERVAL_SECONDS}s (next call at ~${nextTime})...`);
        sleep(INTERVAL_SECONDS);
    }
}

// ============================================
// 테스트 종료
// ============================================
export function teardown(data) {
    console.log('========================================');
    console.log('🏁 Interval test completed.');
    console.log(`Started : ${data.startTime}`);
    console.log(`Finished: ${new Date().toISOString()}`);
    console.log(`Target  : ${data.projectsUrl}`);
    console.log('========================================');
}
