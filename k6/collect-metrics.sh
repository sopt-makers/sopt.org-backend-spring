#!/bin/bash

# ============================================
# Actuator 메트릭 수집 스크립트
# ============================================
#
# 사용법:
#   ./collect-metrics.sh [BASE_URL] [CTX_PATH]
#
# 예시:
#   ./collect-metrics.sh http://localhost:8080 /v2
#   ./collect-metrics.sh http://13.125.203.123:8080 /v2
#
# 수집 메트릭:
#   - playground.cache.hit (캐시 히트 수)
#   - playground.cache.miss (캐시 미스 수 = 외부 API 호출 트리거)
#   - playground.api.call.count (Playground API 호출 횟수)
#   - playground.api.call.duration (API 호출 소요 시간)
#
# ============================================

set -euo pipefail

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

# 인자 파싱
BASE_URL="${1:-http://localhost:8080}"
CTX_PATH="${2:-/v2}"
ACTUATOR_URL="${BASE_URL}${CTX_PATH}/actuator/metrics"

echo -e "${CYAN}============================================${NC}"
echo -e "${CYAN}📊 Actuator 메트릭 수집${NC}"
echo -e "${CYAN}============================================${NC}"
echo -e "대상: ${ACTUATOR_URL}"
echo ""

# 메트릭 조회 함수
get_metric_value() {
  local metric_name=$1
  local url="${ACTUATOR_URL}/${metric_name}"
  local response

  response=$(curl -s --max-time 5 "$url" 2>/dev/null)

  if [ -z "$response" ] || echo "$response" | grep -q "404\|error\|Not Found"; then
    echo "N/A"
    return
  fi

  # COUNT 값 추출
  echo "$response" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    measurements = data.get('measurements', [])
    for m in measurements:
        if m['statistic'] == 'COUNT':
            print(m['value'])
            break
    else:
        print('N/A')
except:
    print('N/A')
" 2>/dev/null
}

# Timer의 TOTAL_TIME 조회 함수
get_timer_total() {
  local metric_name=$1
  local url="${ACTUATOR_URL}/${metric_name}"
  local response

  response=$(curl -s --max-time 5 "$url" 2>/dev/null)

  if [ -z "$response" ]; then
    echo "N/A"
    return
  fi

  echo "$response" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    measurements = data.get('measurements', [])
    result = {}
    for m in measurements:
        result[m['statistic']] = m['value']
    count = result.get('COUNT', 0)
    total = result.get('TOTAL_TIME', 0)
    avg = total / count if count > 0 else 0
    print(f'{total:.3f}s total, {avg:.3f}s avg ({int(count)} calls)')
except:
    print('N/A')
" 2>/dev/null
}

# 메트릭 수집
echo -e "${GREEN}▶ 캐시 메트릭${NC}"
HIT=$(get_metric_value "playground.cache.hit")
MISS=$(get_metric_value "playground.cache.miss")
echo "  캐시 히트:    ${HIT}"
echo "  캐시 미스:    ${MISS}"

# 히트율 계산
if [ "$HIT" != "N/A" ] && [ "$MISS" != "N/A" ]; then
  HIT_RATE=$(python3 -c "
h = float($HIT)
m = float($MISS)
total = h + m
rate = (h / total * 100) if total > 0 else 0
print(f'{rate:.1f}%')
" 2>/dev/null)
  echo -e "  ${YELLOW}캐시 히트율:  ${HIT_RATE}${NC}"
fi

echo ""
echo -e "${GREEN}▶ API 호출 메트릭${NC}"
API_COUNT=$(get_metric_value "playground.api.call.count")
API_DURATION=$(get_timer_total "playground.api.call.duration")
echo "  API 호출 수:  ${API_COUNT}"
echo "  API 소요 시간: ${API_DURATION}"

echo ""
echo -e "${CYAN}============================================${NC}"
echo -e "${CYAN}수집 완료: $(date '+%Y-%m-%d %H:%M:%S')${NC}"
echo -e "${CYAN}============================================${NC}"
