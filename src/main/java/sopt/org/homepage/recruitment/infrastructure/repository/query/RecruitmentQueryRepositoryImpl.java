package sopt.org.homepage.recruitment.infrastructure.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.recruitment.domain.Recruitment;
import sopt.org.homepage.recruitment.domain.vo.RecruitType;
import sopt.org.homepage.recruitment.repository.query.RecruitmentQueryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static sopt.org.homepage.recruitment.domain.QRecruitment.recruitment;

/**
 * RecruitmentQueryRepositoryImpl
 *
 * QueryDSL을 사용한 Recruitment 조회 구현체
 */
@Repository
@RequiredArgsConstructor
public class RecruitmentQueryRepositoryImpl implements RecruitmentQueryRepository {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Recruitment> findById(Long id) {
        Recruitment result = queryFactory
                .selectFrom(recruitment)
                .where(recruitment.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Recruitment> findByGenerationId(Integer generationId) {
        return queryFactory
                .selectFrom(recruitment)
                .where(recruitment.generationId.eq(generationId))
                .orderBy(recruitment.recruitType.asc())  // OB, YB 순서
                .fetch();
    }

    @Override
    public Optional<Recruitment> findByGenerationIdAndRecruitType(Integer generationId, RecruitType recruitType) {
        Recruitment result = queryFactory
                .selectFrom(recruitment)
                .where(
                        recruitment.generationId.eq(generationId),
                        recruitment.recruitType.eq(recruitType)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public long countByGenerationId(Integer generationId) {
        Long count = queryFactory
                .select(recruitment.count())
                .from(recruitment)
                .where(recruitment.generationId.eq(generationId))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public boolean existsByGenerationId(Integer generationId) {
        Integer result = queryFactory
                .selectOne()
                .from(recruitment)
                .where(recruitment.generationId.eq(generationId))
                .fetchFirst();

        return result != null;
    }

    @Override
    public List<Recruitment> findByGenerationIdIn(List<Integer> generationIds) {
        return queryFactory
                .selectFrom(recruitment)
                .where(recruitment.generationId.in(generationIds))
                .orderBy(
                        recruitment.generationId.desc(),
                        recruitment.recruitType.asc()
                )
                .fetch();
    }

    @Override
    public List<Recruitment> findActiveRecruitments() {
        // 현재 시간을 문자열로 변환
        String now = LocalDateTime.now().format(FORMATTER);

        return queryFactory
                .selectFrom(recruitment)
                .where(
                        recruitment.schedule.applicationStartTime.loe(now),
                        recruitment.schedule.finalResultTime.goe(now)
                )
                .orderBy(recruitment.generationId.desc())
                .fetch();
    }
}