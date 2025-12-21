package sopt.org.homepage.part.infrastructure.repository.query;

import static sopt.org.homepage.part.domain.QPart.part;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.part.domain.Part;
import sopt.org.homepage.part.repository.query.PartQueryRepository;

/**
 * PartQueryRepositoryImpl
 * <p>
 * QueryDSL을 사용한 PartType 조회 구현체
 */
@Repository
@RequiredArgsConstructor
public class PartQueryRepositoryImpl implements PartQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Part> findById(Long id) {
        Part result = queryFactory
                .selectFrom(part)
                .where(part.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Part> findByGenerationId(Integer generationId) {
        return queryFactory
                .selectFrom(part)
                .where(part.generationId.eq(generationId))
                .orderBy(part.partType.asc())  // 파트 타입 순서대로
                .fetch();
    }

    @Override
    public Optional<Part> findByGenerationIdAndPartType(Integer generationId, PartType partType) {
        Part result = queryFactory
                .selectFrom(part)
                .where(
                        part.generationId.eq(generationId),
                        part.partType.eq(partType)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public long countByGenerationId(Integer generationId) {
        Long count = queryFactory
                .select(part.count())
                .from(part)
                .where(part.generationId.eq(generationId))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public boolean existsByGenerationId(Integer generationId) {
        Integer result = queryFactory
                .selectOne()
                .from(part)
                .where(part.generationId.eq(generationId))
                .fetchFirst();

        return result != null;
    }

    @Override
    public List<Part> findByGenerationIdIn(List<Integer> generationIds) {
        return queryFactory
                .selectFrom(part)
                .where(part.generationId.in(generationIds))
                .orderBy(
                        part.generationId.desc(),
                        part.partType.asc()
                )
                .fetch();
    }
}
