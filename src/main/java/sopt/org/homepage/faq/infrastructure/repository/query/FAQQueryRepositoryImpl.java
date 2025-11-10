package sopt.org.homepage.faq.infrastructure.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.faq.domain.FAQ;
import sopt.org.homepage.faq.repository.query.FAQQueryRepository;

import java.util.List;
import java.util.Optional;

import static sopt.org.homepage.faq.domain.QFAQ.fAQ;

/**
 * FAQQueryRepositoryImpl
 *
 * QueryDSL을 사용한 FAQ 조회 구현체
 */
@Repository
@RequiredArgsConstructor
public class FAQQueryRepositoryImpl implements FAQQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<FAQ> findById(Long id) {
        FAQ result = queryFactory
                .selectFrom(fAQ)
                .where(fAQ.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<FAQ> findByPart(PartType part) {
        FAQ result = queryFactory
                .selectFrom(fAQ)
                .where(fAQ.part.eq(part))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<FAQ> findAll() {
        return queryFactory
                .selectFrom(fAQ)
                .orderBy(fAQ.part.asc())
                .fetch();
    }

    @Override
    public boolean existsByPart(PartType part) {
        Integer result = queryFactory
                .selectOne()
                .from(fAQ)
                .where(fAQ.part.eq(part))
                .fetchFirst();

        return result != null;
    }
}