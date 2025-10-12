package sopt.org.homepage.corevalue.infrastructure.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.corevalue.domain.CoreValue;
import sopt.org.homepage.corevalue.repository.query.CoreValueQueryRepository;

import java.util.List;
import java.util.Optional;

import static sopt.org.homepage.corevalue.domain.QCoreValue.coreValue;

/**
 * CoreValueQueryRepositoryImpl
 *
 * QueryDSL을 사용한 CoreValue 조회 구현체
 */
@Repository
@RequiredArgsConstructor
public class CoreValueQueryRepositoryImpl implements CoreValueQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CoreValue> findById(Long id) {
        CoreValue result = queryFactory
                .selectFrom(coreValue)
                .where(coreValue.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<CoreValue> findByGenerationIdOrderByDisplayOrder(Integer generationId) {
        return queryFactory
                .selectFrom(coreValue)
                .where(coreValue.generationId.eq(generationId))
                .orderBy(coreValue.displayOrder.asc())
                .fetch();
    }

    @Override
    public long countByGenerationId(Integer generationId) {
        Long count = queryFactory
                .select(coreValue.count())
                .from(coreValue)
                .where(coreValue.generationId.eq(generationId))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public boolean existsByGenerationId(Integer generationId) {
        Integer result = queryFactory
                .selectOne()
                .from(coreValue)
                .where(coreValue.generationId.eq(generationId))
                .fetchFirst();

        return result != null;
    }

    @Override
    public List<CoreValue> findByGenerationIdIn(List<Integer> generationIds) {
        return queryFactory
                .selectFrom(coreValue)
                .where(coreValue.generationId.in(generationIds))
                .orderBy(
                        coreValue.generationId.desc(),
                        coreValue.displayOrder.asc()
                )
                .fetch();
    }
}