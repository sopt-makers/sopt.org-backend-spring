package sopt.org.homepage.sopticle.repository;


import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import sopt.org.homepage.sopticle.dto.request.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.entity.SopticleEntity;
import sopt.org.homepage.sopticle.entity.QSopticleEntity;

@Repository
@RequiredArgsConstructor
public class SopticleQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QSopticleEntity sopticle = QSopticleEntity.sopticleEntity;

    public List<SopticleEntity> findAllSorted(GetSopticleListRequestDto requestDto) {
        return queryFactory
                .selectFrom(sopticle)
                .orderBy(
                        getOrderSpecifier(requestDto.getSort())
                )
                .offset(requestDto.getOffset())
                .limit(requestDto.getLimit())
                .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if ("likes".equalsIgnoreCase(sort)) {
            return sopticle.likeCount.desc();
        } else {
            return sopticle.createdAt.desc(); // 기본은 최신순
        }
    }
}