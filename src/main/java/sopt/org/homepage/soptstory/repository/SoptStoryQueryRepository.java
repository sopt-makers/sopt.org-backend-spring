package sopt.org.homepage.soptstory.repository;


import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.soptstory.dto.request.GetSoptStoryListRequestDto;
import sopt.org.homepage.soptstory.entity.QSoptStoryEntity;
import sopt.org.homepage.soptstory.entity.SoptStoryEntity;

@Repository
@RequiredArgsConstructor
public class SoptStoryQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QSoptStoryEntity soptStory = QSoptStoryEntity.soptStoryEntity;

    public List<SoptStoryEntity> findAllSorted(GetSoptStoryListRequestDto requestDto) {
        return queryFactory
                .selectFrom(soptStory)
                .orderBy(
                        getOrderSpecifier(requestDto.getSort())
                )
                .offset(requestDto.getOffset())
                .limit(requestDto.getLimit())
                .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if ("likes".equalsIgnoreCase(sort)) {
            return soptStory.likeCount.desc();
        } else {
            return soptStory.createdAt.desc(); // 기본은 최신순
        }
    }
}