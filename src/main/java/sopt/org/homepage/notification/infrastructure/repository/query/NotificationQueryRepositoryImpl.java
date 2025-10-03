package sopt.org.homepage.notification.infrastructure.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.notification.domain.QNotification;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.repository.query.NotificationQueryRepository;
import sopt.org.homepage.notification.service.query.dto.NotificationListView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * NotificationQueryRepository 구현체
 * - QueryDSL을 사용한 타입 안전한 쿼리
 */
@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QNotification notification = QNotification.notification;

    @Override
    public NotificationListView findNotificationsByGeneration(Generation generation) {
        // QueryDSL로 조회
        List<String> emailList = queryFactory
                .select(notification.email.value)
                .from(notification)
                .where(notification.generation.value.eq(generation.getValue()))
                .orderBy(notification.createdAt.desc())
                .fetch();

        return NotificationListView.of(
                generation.getValue(),
                emailList
        );
    }
}