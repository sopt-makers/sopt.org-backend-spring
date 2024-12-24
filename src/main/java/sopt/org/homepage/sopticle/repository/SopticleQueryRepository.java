package sopt.org.homepage.sopticle.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.common.type.Part;

import sopt.org.homepage.sopticle.dto.request.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.entity.SopticleEntity;
import sopt.org.homepage.sopticle.entity.QSopticleEntity;

@Repository
@RequiredArgsConstructor
public class SopticleQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QSopticleEntity sopticle = QSopticleEntity.sopticleEntity;

    public List<SopticleEntity> findAllWithFilters(GetSopticleListRequestDto requestDto, String sessionId) {
        return queryFactory
                .selectFrom(sopticle)
                .where(
                        partEq(requestDto.getPart()),
                        generationEq(requestDto.getGeneration())
                )
                .orderBy(sopticle.id.desc())
                .offset(requestDto.getOffset())
                .limit(requestDto.getLimit())
                .fetch();
    }
    public Long countWithFilters(GetSopticleListRequestDto requestDto) {
        return queryFactory
                .select(sopticle.count())
                .from(sopticle)
                .where(
                        partEq(requestDto.getPart()),
                        generationEq(requestDto.getGeneration())
                )
                .fetchOne();
    }

    private BooleanExpression partEq(Part part) {
        if (part == null) {
            return null;
        }
        return sopticle.part.eq(part);
    }

    private BooleanExpression generationEq(Integer generation) {
        return generation != null ? sopticle.generation.eq(generation) : null;
    }
}