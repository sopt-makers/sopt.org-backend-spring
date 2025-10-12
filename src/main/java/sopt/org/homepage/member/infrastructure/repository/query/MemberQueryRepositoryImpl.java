package sopt.org.homepage.member.infrastructure.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.member.domain.Member;
import sopt.org.homepage.member.domain.vo.MemberRole;
import sopt.org.homepage.member.repository.query.MemberQueryRepository;

import java.util.List;
import java.util.Optional;

import static sopt.org.homepage.member.domain.QMember.member;

/**
 * MemberQueryRepositoryImpl
 *
 * QueryDSL을 사용한 Member 조회 구현체
 */
@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findById(Long id) {
        Member result = queryFactory
                .selectFrom(member)
                .where(member.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Member> findByGenerationId(Integer generationId) {
        return queryFactory
                .selectFrom(member)
                .where(member.generationId.eq(generationId))
                .orderBy(
                        member.role.asc(),  // 역할 순서대로
                        member.name.asc()   // 이름순
                )
                .fetch();
    }

    @Override
    public List<Member> findByGenerationIdAndRole(Integer generationId, MemberRole role) {
        return queryFactory
                .selectFrom(member)
                .where(
                        member.generationId.eq(generationId),
                        member.role.eq(role)
                )
                .orderBy(member.name.asc())
                .fetch();
    }

    @Override
    public long countByGenerationId(Integer generationId) {
        Long count = queryFactory
                .select(member.count())
                .from(member)
                .where(member.generationId.eq(generationId))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public boolean existsByGenerationId(Integer generationId) {
        Integer result = queryFactory
                .selectOne()
                .from(member)
                .where(member.generationId.eq(generationId))
                .fetchFirst();

        return result != null;
    }

    @Override
    public List<Member> findByGenerationIdIn(List<Integer> generationIds) {
        return queryFactory
                .selectFrom(member)
                .where(member.generationId.in(generationIds))
                .orderBy(
                        member.generationId.desc(),
                        member.role.asc(),
                        member.name.asc()
                )
                .fetch();
    }

    @Override
    public Optional<Member> findPresidentByGenerationId(Integer generationId) {
        Member result = queryFactory
                .selectFrom(member)
                .where(
                        member.generationId.eq(generationId),
                        member.role.eq(MemberRole.PRESIDENT)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}