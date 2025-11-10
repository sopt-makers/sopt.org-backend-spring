package sopt.org.homepage.soptstory.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.soptstory.domain.SoptStoryLike;

import java.util.Optional;

/**
 * SoptStoryLike Command Repository
 *
 * 책임:
 * - 좋아요 생성, 삭제
 * - 중복 좋아요 검증
 */
@Repository
public interface SoptStoryLikeCommandRepository extends JpaRepository<SoptStoryLike, Long> {

    /**
     * 특정 IP가 특정 SoptStory에 이미 좋아요를 눌렀는지 확인
     *
     * @param soptStoryId SoptStory ID
     * @param ip IP 주소 (Value Object의 value)
     * @return 존재 여부
     */
    boolean existsBySoptStory_IdAndIpAddress_Value(Long soptStoryId, String ip);

    /**
     * 특정 IP의 특정 SoptStory 좋아요 조회 (삭제용)
     *
     * @param soptStoryId SoptStory ID
     * @param ip IP 주소 (Value Object의 value)
     * @return SoptStoryLike Optional
     */
    Optional<SoptStoryLike> findBySoptStory_IdAndIpAddress_Value(Long soptStoryId, String ip);
}