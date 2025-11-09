package sopt.org.homepage.review.infrastructure.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.review.domain.QReview;
import sopt.org.homepage.review.domain.Review;
import sopt.org.homepage.review.domain.vo.CategoryType;
import sopt.org.homepage.review.repository.query.ReviewQueryRepository;
import sopt.org.homepage.review.service.query.dto.ReviewSearchCond;

import java.util.List;

/**
 * 리뷰 Query Repository 구현체 (QueryDSL)
 */
@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;

    @Override
    public List<Review> findAllWithFilters(ReviewSearchCond cond, long offset, int limit) {
        return queryFactory
                .selectFrom(review)
                .where(
                        categoryEq(cond.category()),
                        activityContains(cond.category(), cond.activity()),
                        partEq(cond.partType()),
                        generationEq(cond.generation())
                )
                .orderBy(
                        review.generation.desc(),
                        review.author.name.asc(),
                        review.id.asc()
                )
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public long countWithFilters(ReviewSearchCond cond) {
        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        categoryEq(cond.category()),
                        activityContains(cond.category(), cond.activity()),
                        partEq(cond.partType()),
                        generationEq(cond.generation())
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public Review findRandomReviewByPart(PartType partType) {
        return queryFactory
                .selectFrom(review)
                .where(review.partType.eq(partType))
                .orderBy(Expressions.numberTemplate(Double.class, "RANDOM()").asc())
                .limit(1)
                .fetchFirst();
    }

    @Override
    public List<Review> findAllByAuthorName(String authorName) {
        return queryFactory
                .selectFrom(review)
                .where(review.author.name.eq(authorName))
                .fetch();
    }

    // === 동적 쿼리 조건 메서드 ===
    private BooleanExpression categoryEq(String category) {
        return CategoryType.fromSafely(category)
                .map(type -> review.category.type.eq(type))
                .orElse(null);
    }

    private BooleanExpression partEq(PartType partType) {
        return partType != null ? review.partType.eq(partType) : null;
    }

    private BooleanExpression generationEq(Integer generation) {
        return generation != null ? review.generation.eq(generation) : null;
    }

    /**
     * "전체 활동" 카테고리일 때 특정 활동 필터링
     * JSON 배열에서 특정 값 검색
     */
    private BooleanExpression activityContains(String category, String activity) {
        // activity가 없거나 "전체"면 조건 무시
        if (activity == null || activity.isBlank() || activity.equals("전체")) {
            return null;
        }

        // category가 "전체 활동"이 아니면 조건 무시
        if (category == null || !CategoryType.ACTIVITY.getDisplayName().equals(category)) {
            return null;
        }

        String searchTerm = "\"" + activity + "\"";
        return Expressions.booleanTemplate(
                "CAST({0} AS string) LIKE {1}",
                review.subjects.values,
                "%" + searchTerm + "%"
        );
    }
}