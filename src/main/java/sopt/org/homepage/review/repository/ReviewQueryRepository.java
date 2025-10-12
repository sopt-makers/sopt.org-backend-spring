package sopt.org.homepage.review.repository;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.entity.QReviewEntity;
import sopt.org.homepage.review.entity.ReviewEntity;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final QReviewEntity review = QReviewEntity.reviewEntity;

	public List<ReviewEntity> findAllWithFilters(ReviewsRequestDto requestDto, long offset, int limit) {
		System.out.println(requestDto.toString());
		var query = queryFactory
			.selectFrom(review)
			.where(review.category.eq(requestDto.getCategory()));

		if (Objects.equals(requestDto.getCategory(), "전체 활동") && requestDto.getActivity() != null
			&& !requestDto.getActivity().equals("전체")) {
			String searchTerm = "\"" + requestDto.getActivity() + "\"";

			BooleanExpression subjectContains = Expressions.booleanTemplate(
				"CAST({0} AS string) LIKE {1}",
				review.subject,
				"%" + searchTerm + "%"
			);

			query.where(subjectContains);

		}

		if (requestDto.getPartType() != null) {
			query.where(review.partType.eq(requestDto.getPartType()));
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
			.from(review)
			.where(review.category.eq(requestDto.getCategory()));

		if (Objects.equals(requestDto.getCategory(), "전체 활동") && requestDto.getActivity() != null
			&& !requestDto.getActivity().equals("전체")) {
			String searchTerm = "\"" + requestDto.getActivity() + "\"";

			BooleanExpression subjectContains = Expressions.booleanTemplate(
				"CAST({0} AS string) LIKE {1}",
				review.subject,
				"%" + searchTerm + "%"
			);

			query.where(subjectContains);
		}

		if (requestDto.getPartType() != null) {
			query.where(review.partType.eq(requestDto.getPartType()));
		}

		if (requestDto.getGeneration() != null) {
			query.where(review.generation.eq(requestDto.getGeneration()));
		}

		return query.fetchOne();
	}

	public ReviewEntity findRandomReviewByPart(PartType partType) {
		return queryFactory
			.selectFrom(review)
			.where(review.partType.eq(partType))
			.orderBy(Expressions.numberTemplate(Double.class, "RANDOM()").asc())
			.limit(1)
			.fetchFirst();
	}
}
