package sopt.org.homepage.soptstory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.domain.SoptStoryLike;
import sopt.org.homepage.soptstory.exception.AlreadyLikedException;
import sopt.org.homepage.soptstory.exception.DuplicateSoptStoryUrlException;
import sopt.org.homepage.soptstory.exception.NotLikedException;
import sopt.org.homepage.soptstory.exception.SoptStoryNotFoundException;

/**
 * SoptStory Service
 * <p>
 * 책임: - SoptStory 생성, 좋아요 추가/취소 - 목록 조회 (정렬, 페이징, 좋아요 상태) - 트랜잭션 관리
 */
@Service
@RequiredArgsConstructor
public class SoptStoryService {

    private final SoptStoryRepository soptStoryRepository;
    private final SoptStoryLikeRepository soptStoryLikeRepository;

    // ===== Command =====

    @Transactional
    public Long createSoptStory(String title, String description, String thumbnailUrl, String url) {
        if (soptStoryRepository.existsByUrl(url)) {
            throw new DuplicateSoptStoryUrlException(url);
        }

        SoptStory soptStory = SoptStory.of(title, description, thumbnailUrl, url);
        return soptStoryRepository.save(soptStory).getId();
    }

    @Transactional
    public Long like(Long soptStoryId, String ip) {
        SoptStory soptStory = findById(soptStoryId);

        if (soptStoryLikeRepository.existsBySoptStory_IdAndIp(soptStoryId, ip)) {
            throw new AlreadyLikedException(soptStoryId, ip);
        }

        SoptStoryLike like = SoptStoryLike.of(soptStory, ip);
        soptStory.incrementLike();

        return soptStoryLikeRepository.save(like).getId();
    }

    @Transactional
    public Long unlike(Long soptStoryId, String ip) {
        SoptStory soptStory = findById(soptStoryId);

        SoptStoryLike like = soptStoryLikeRepository
                .findBySoptStory_IdAndIp(soptStoryId, ip)
                .orElseThrow(() -> new NotLikedException(soptStoryId, ip));

        soptStory.decrementLike();

        Long likeId = like.getId();
        soptStoryLikeRepository.delete(like);

        return likeId;
    }

    // ===== Query =====

    @Transactional(readOnly = true)
    public Page<SoptStory> getSoptStoryList(String sort, int pageNo, int limit) {
        Pageable pageable = PageRequest.of(pageNo - 1, limit);

        if ("likes".equalsIgnoreCase(sort)) {
            return soptStoryRepository.findAllByOrderByLikeCountDesc(pageable);
        }
        return soptStoryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Set<Long> getLikedSoptStoryIds(String ip, List<SoptStory> soptStories) {
        if (soptStories.isEmpty()) {
            return Set.of();
        }

        return soptStoryLikeRepository.findAllByIpAndSoptStoryIn(ip, soptStories)
                .stream()
                .map(like -> like.getSoptStory().getId())
                .collect(Collectors.toSet());
    }

    // ===== Private =====

    private SoptStory findById(Long id) {
        return soptStoryRepository.findById(id)
                .orElseThrow(() -> new SoptStoryNotFoundException(id));
    }
}
