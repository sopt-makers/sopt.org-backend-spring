package sopt.org.homepage.soptstory.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.domain.SoptStoryLike;
import sopt.org.homepage.soptstory.repository.query.SoptStoryLikeQueryRepository;
import sopt.org.homepage.soptstory.repository.query.SoptStoryQueryRepository;
import sopt.org.homepage.soptstory.service.query.dto.SoptStoryListView;
import sopt.org.homepage.soptstory.service.query.dto.SoptStoryPageView;
import sopt.org.homepage.soptstory.service.query.dto.SoptStorySearchCond;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SoptStory Query Service
 *
 * 책임:
 * - SoptStory 목록 조회
 * - 페이지네이션 처리
 * - 사용자 좋아요 상태 조회
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptStoryQueryService {

    private final SoptStoryQueryRepository soptStoryQueryRepository;
    private final SoptStoryLikeQueryRepository soptStoryLikeQueryRepository;

    /**
     * SoptStory 목록 조회 (페이지네이션)
     *
     * 프로세스:
     * 1. 검색 조건에 따라 SoptStory 목록 조회
     * 2. 전체 개수 조회
     * 3. 사용자의 좋아요 상태 조회
     * 4. View DTO 변환
     *
     * @param cond 검색 조건 (정렬, 페이징)
     * @param userIp 사용자 IP 주소
     * @return 페이지네이션된 SoptStory 목록
     */
    public SoptStoryPageView getSoptStoryList(SoptStorySearchCond cond, String userIp) {
        // 1. SoptStory 목록 조회
        List<SoptStory> soptStories = soptStoryQueryRepository.findAllSorted(cond);

        // 2. 전체 개수 조회
        long totalCount = soptStoryQueryRepository.countAll();

        // 3. 사용자가 좋아요를 누른 SoptStory ID 집합 생성
        Set<Long> likedSoptStoryIds = getUserLikedSoptStoryIds(userIp, soptStories);

        // 4. Entity -> View DTO 변환
        List<SoptStoryListView> content = soptStories.stream()
                .map(soptStory -> SoptStoryListView.from(
                        soptStory,
                        likedSoptStoryIds.contains(soptStory.getId())
                ))
                .toList();

        // 5. 페이지 뷰 생성
        return new SoptStoryPageView(
                content,
                (int) totalCount,
                cond.limit(),
                cond.pageNo()
        );
    }

    /**
     * 사용자가 좋아요를 누른 SoptStory ID 집합 조회
     *
     * @param userIp 사용자 IP
     * @param soptStories 조회할 SoptStory 목록
     * @return 좋아요를 누른 SoptStory ID 집합
     */
    private Set<Long> getUserLikedSoptStoryIds(String userIp, List<SoptStory> soptStories) {
        if (soptStories.isEmpty()) {
            return Set.of();
        }

        List<SoptStoryLike> likedList =
                soptStoryLikeQueryRepository.findAllByIpAndSoptStoryIn(userIp, soptStories);

        return likedList.stream()
                .map(SoptStoryLike::getSoptStoryId)
                .collect(Collectors.toSet());
    }
}