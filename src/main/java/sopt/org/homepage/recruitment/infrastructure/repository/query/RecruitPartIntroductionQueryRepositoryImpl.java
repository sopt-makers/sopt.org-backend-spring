package sopt.org.homepage.recruitment.infrastructure.repository.query;

import static sopt.org.homepage.recruitment.domain.QRecruitPartIntroduction.recruitPartIntroduction;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.recruitment.domain.RecruitPartIntroduction;
import sopt.org.homepage.recruitment.repository.query.RecruitPartIntroductionQueryRepository;

/**
 * RecruitPartIntroductionQueryRepositoryImpl
 * <p>
 * QueryDSL을 사용한 RecruitPartIntroduction 조회 구현체
 */
@Repository
@RequiredArgsConstructor
public class RecruitPartIntroductionQueryRepositoryImpl implements RecruitPartIntroductionQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<RecruitPartIntroduction> findById(Long id) {
        RecruitPartIntroduction result = queryFactory
                .selectFrom(recruitPartIntroduction)
                .where(recruitPartIntroduction.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<RecruitPartIntroduction> findByGenerationId(Integer generationId) {
        return queryFactory
                .selectFrom(recruitPartIntroduction)
                .where(recruitPartIntroduction.generationId.eq(generationId))
                .orderBy(recruitPartIntroduction.part.asc())
                .fetch();
    }

    @Override
    public Optional<RecruitPartIntroduction> findByGenerationIdAndPart(Integer generationId, PartType part) {
        RecruitPartIntroduction result = queryFactory
                .selectFrom(recruitPartIntroduction)
                .where(
                        recruitPartIntroduction.generationId.eq(generationId),
                        recruitPartIntroduction.part.eq(part)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public long countByGenerationId(Integer generationId) {
        Long count = queryFactory
                .select(recruitPartIntroduction.count())
                .from(recruitPartIntroduction)
                .where(recruitPartIntroduction.generationId.eq(generationId))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public boolean existsByGenerationId(Integer generationId) {
        Integer result = queryFactory
                .selectOne()
                .from(recruitPartIntroduction)
                .where(recruitPartIntroduction.generationId.eq(generationId))
                .fetchFirst();

        return result != null;
    }

    @Override
    public List<RecruitPartIntroduction> findByGenerationIdIn(List<Integer> generationIds) {
        return queryFactory
                .selectFrom(recruitPartIntroduction)
                .where(recruitPartIntroduction.generationId.in(generationIds))
                .orderBy(
                        recruitPartIntroduction.generationId.desc(),
                        recruitPartIntroduction.part.asc()
                )
                .fetch();
    }
}
