package sopt.org.homepage.generation.infrastructure.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.generation.domain.Generation;
import sopt.org.homepage.generation.repository.query.GenerationQueryRepository;

import java.util.List;
import java.util.Optional;

import static sopt.org.homepage.generation.domain.QGeneration.generation;

/**
 * GenerationQueryRepositoryImpl
 *
 * QueryDSL을 사용한 Generation 조회 구현체
 * - 복잡한 조회 쿼리 최적화
 * - 타입 세이프한 쿼리 작성
 */
@Repository
@RequiredArgsConstructor
public class GenerationQueryRepositoryImpl implements GenerationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Generation> findById(Integer id) {
        Generation result = queryFactory
                .selectFrom(generation)
                .where(generation.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Generation> findLatest() {
        Generation result = queryFactory
                .selectFrom(generation)
                .orderBy(generation.id.desc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Generation> findAllOrderByIdDesc() {
        return queryFactory
                .selectFrom(generation)
                .orderBy(generation.id.desc())
                .fetch();
    }

    @Override
    public List<Generation> findByIdBetween(Integer startId, Integer endId) {
        return queryFactory
                .selectFrom(generation)
                .where(
                        generation.id.goe(startId),  // greater or equal
                        generation.id.loe(endId)     // less or equal
                )
                .orderBy(generation.id.desc())
                .fetch();
    }

    @Override
    public boolean existsById(Integer id) {
        Integer result = queryFactory
                .selectOne()
                .from(generation)
                .where(generation.id.eq(id))
                .fetchFirst();

        return result != null;
    }
}