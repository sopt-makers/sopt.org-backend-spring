package sopt.org.homepage.soptstory.infrastructure.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.soptstory.domain.QSoptStory;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.repository.query.SoptStoryQueryRepository;
import sopt.org.homepage.soptstory.service.query.dto.SoptStorySearchCond;

import java.util.List;

/**
 * SoptStory Query Repository 구현체 (QueryDSL)
 *
 * 책임:
 * - 복잡한 조회 쿼리 구현
 * - 정렬, 페이징 처리
 * - 타입 안전한 쿼리 작성
 *
 * 참고:
 * - QSoptStory는 Gradle 빌드 시 자동 생성됨 (build/generated/querydsl)
 * - QueryDSL 설정은 build.gradle에 정의되어 있음
 */
@Repository
@RequiredArgsConstructor
public class SoptStoryQueryRepositoryImpl implements SoptStoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    // QueryDSL Q타입 - static으로 선언하여 재사용
    private static final QSoptStory soptStory = QSoptStory.soptStory;

    /**
     * 검색 조건에 따라 SoptStory 목록 조회
     *
     * QueryDSL 장점:
     * - 컴파일 타임에 오타 검증 (필드명, 타입 등)
     * - IDE 자동완성 지원
     * - SQL 인젝션 방지
     *
     * @param cond 검색 조건 (정렬 기준, 페이징 정보)
     * @return SoptStory 리스트
     */
    @Override
    public List<SoptStory> findAllSorted(SoptStorySearchCond cond) {
        return queryFactory
                .selectFrom(soptStory)                    // SELECT * FROM SoptStory
                .orderBy(getOrderSpecifier(cond.sort()))  // ORDER BY ...
                .offset(cond.offset())                    // OFFSET (pageNo-1) * limit
                .limit(cond.limit())                      // LIMIT limit
                .fetch();                                 // 실행 및 결과 반환
    }

    /**
     * 전체 SoptStory 개수 조회
     *
     * @return 전체 개수
     */
    @Override
    public long countAll() {
        Long count = queryFactory
                .select(soptStory.count())   // SELECT COUNT(*)
                .from(soptStory)             // FROM SoptStory
                .fetchOne();                 // 단일 결과 반환

        // null 체크 (결과가 없을 경우 0 반환)
        return count != null ? count : 0L;
    }

    /**
     * 정렬 조건에 따른 OrderSpecifier 반환
     *
     * QueryDSL의 OrderSpecifier:
     * - SQL의 ORDER BY 절을 타입 안전하게 표현
     * - soptStory.likeCount.value.desc() → ORDER BY likeCount DESC
     *
     * @param sort 정렬 기준 ("likes" 또는 "recent")
     * @return QueryDSL OrderSpecifier
     */
    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if ("likes".equalsIgnoreCase(sort)) {
            // 좋아요 많은 순 정렬
            // Value Object의 value 필드에 접근: likeCount.value
            return soptStory.likeCount.value.desc();
        }

        // 기본값: 최신순 정렬
        return soptStory.createdAt.desc();
    }
}