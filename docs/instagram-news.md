# 인스타그램 뉴스 수집 운영

## 실행과 복구

EC2에서만 수집한다. `lambda-dev`에는 수집 빈을 등록하지 않는다.
관리자 수집 API는 기존 관리자 JWT 인증을 거쳐 실행 ID를 반환한다.
이미 실행 중이면 같은 ID를 반환하며, 외부 실행 생성 중의 중복 요청은 409를 반환한다.
기능이 꺼져 있으면 503을 반환한다. 기능이 켜졌는데 토큰이 없으면 기동을 실패시킨다.

`instagram_collection_state`의 단일 행에 실행 ID, 페이지 offset, 연속 실패 횟수,
실행 시작 시각, 마지막 성공 기준 시각을 보존한다. 재시작 후 poll은 이 행에서 재개한다.
외부 요청 중에는 Java synchronized나 DB 행 락을 보유하지 않는다.
15분 lease로 인스턴스 간 처리를 조정하고 각 게시물 처리 전에 갱신한다.
프로세스가 강제 종료되어 lease가 남으면 만료 후 다른 프로세스가 이어받는다.
페이지 도중 종료되면 같은 페이지를 다시 읽으며 게시물 ID의 unique 제약으로 중복 저장을 막는다.
연속 오류 상한 또는 전체 경과 시간 상한을 넘으면 실행을 정리하고 다음 수집을 허용한다.
인스타 예약 작업은 `instagramTaskScheduler`를 사용하여 방문자 집계 작업과 분리한다.

Apify 실행 생성 응답을 받은 직후 DB 저장 전에 프로세스가 종료되는 짧은 구간까지
원자적으로 묶을 수는 없다. 이 경우 Apify 실행 내역을 확인해야 한다.

## 수집 기간과 재시도

첫 수집은 설정한 since부터 시작한다. 성공한 전체 수집 이후에는
`max(since, 마지막 수집 시작 시각 - overlap-days)`부터 다시 읽는다.
중복 구간은 최근 캡션 변경과 수집 지연을 다시 평가하기 위한 것이다.
개별 게시물 오류, 실패/중단 실행, 결과 한도 도달 시에는 기준 시각을 진행하지 않는다.
따라서 다음 수집에서 같은 범위를 다시 시도한다. 결과 한도 도달 시 로그를 확인해
한도를 늘려야 한다. 한 건이 계속 실패하면 수집 범위도 계속 유지된다.
성공한 수집 이후 중복 구간보다 오래된 과거 캡션 변경은 자동 재평가하지 않는다.
전체 재수집이 필요하면 활성 실행이 없는 상태에서 운영자가 DB checkpoint를 초기화한다.

## 설정

| 키 | 기본값 | 의미 |
| --- | --- | --- |
| news.display-limit | 0 | 0 이하이면 무제한 |
| news.instagram.enabled | false | 수집 활성화 |
| news.instagram.apify-token | 없음 | 활성화 시 필수 |
| news.instagram.since | 2025-12-31T15:00:00Z | 최초 수집 하한, KST 2026년 시작 |
| news.instagram.overlap-days | 7 | 성공 시각 이전 중복 수집 일수 |
| news.instagram.results-limit | 1000 | Apify 실행당 게시물 한도 |
| news.instagram.max-failures | 10 | 연속 폴링 오류 상한 |
| news.instagram.max-duration-seconds | 7200 | 전체 실행 경과 시간 상한 |
| news.instagram.additional-series | 빈 목록 | 기수별 신규 시리즈 키워드, 쉼표로 구분 |
| news.instagram.cron | 0 0 9 * * MON | 매주 월요일 KST 09시 |
| news.instagram.poll-delay-ms | 60000 | 폴링 사이 대기 시간 |

## 관리자 편집과 삭제

일괄 배포는 동일 링크의 기존 행을 갱신해 ID와 게시일을 보존한다.
인스타 링크는 shortcode로 매칭하며 www, p/reel/tv, 쿼리 차이를 무시한다.
인스타 항목의 제목·이미지도 갱신한다. 목록에 없다는 이유만으로 인스타 항목을 지우지는 않는다.
삭제 API는 인스타 뉴스의 hidden을 설정해 조회에서 제외하고 재수집을 막는다.
수동 뉴스는 기존처럼 실제 삭제한다. 숨긴 인스타 뉴스의 ID와 이미지는 유지한다.
관리자 응답의 instagramPostId가 null이면 수동 뉴스다.

## 리뷰에서 유지한 선택

- 썸네일 BodySubscriber는 용량 초과 시 수신을 취소하고 전체 다운로드 제한 시간을 보장하므로 유지한다.
- 외부 Client, Filter, Processor, Scheduler는 HTTP 처리·필터 정책·게시물 저장·예약 실행의 책임이 달라 유지한다.
- published_at은 NULL 허용을 유지하며 실제 조회의 NULLS LAST와 hidden 조건에 맞는 인덱스를 추가한다.
- 과거 중간 커밋은 재작성하지 않는다.

통합 테스트는 공통 Spring 컨텍스트를 재사용하면서 수집, 게시물 처리, 게시일,
필터, 썸네일, 마이그레이션으로 나눈다. V13·V14 SQL은 PostgreSQL의 별도 테스트
스키마에 실제로 실행하며, 외부 Apify와 S3는 테스트에서 호출하지 않는다.
