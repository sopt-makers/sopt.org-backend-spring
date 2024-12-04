package sopt.org.homepage.review.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.review.entity.QReviewEntity;
import sopt.org.homepage.review.entity.ReviewEntity;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;


@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QReviewEntity review = QReviewEntity.reviewEntity;

    public List<ReviewEntity> findAllWithFilters(ReviewsRequestDto requestDto, long offset, int limit) {
        var query = queryFactory
                .selectFrom(review);

        if (requestDto.getPart() != null) {
            query.where(review.part.eq(requestDto.getPart()));
        }

        if (requestDto.getGeneration() != null) {
            query.where(review.generation.eq(requestDto.getGeneration()));
        }

        return query
                .orderBy(review.generation.desc())
                .orderBy(review.author.asc())
                .orderBy(review.id.asc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public long countWithFilters(ReviewsRequestDto requestDto) {
        var query = queryFactory
                .select(review.count())
                .from(review);

        if (requestDto.getPart() != null) {
            query.where(review.part.eq(requestDto.getPart()));
        }

        if (requestDto.getGeneration() != null) {
            query.where(review.generation.eq(requestDto.getGeneration()));
        }

        return query.fetchOne();
    }

    public ReviewEntity findRandomReviewByPart(Part part) {
        return queryFactory
                .selectFrom(review)
                .where(review.part.eq(part))
                .orderBy(Expressions.numberTemplate(Double.class, "RANDOM()").asc())
                .limit(1)
                .fetchFirst();
    }
}
