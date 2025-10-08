package sopt.org.homepage.soptstory.repository.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.domain.SoptStoryLike;

import java.util.List;

/**
 * SoptStoryLike Query Repository
 *
 * 책임:
 * - 특정 IP의 좋아요 목록 조회
 * - 사용자별 좋아요 상태 조회
 */
@Repository
public interface SoptStoryLikeQueryRepository extends JpaRepository<SoptStoryLike, Long> {

    /**
     * 특정 IP가 누른 좋아요 목록 조회 (특정 SoptStory 리스트 내에서)
     *
     * 사용 목적: 페이지네이션된 SoptStory 목록에서 사용자의 좋아요 상태를 확인
     *
     * @param ip IP 주소 (Value Object의 value)
     * @param soptStories 조회할 SoptStory 리스트
     * @return 해당 IP가 누른 좋아요 리스트
     */
    @Query("SELECT sl FROM SoptStoryLike sl " +
            "WHERE sl.ipAddress.value = :ip " +
            "AND sl.soptStory IN :soptStories")
    List<SoptStoryLike> findAllByIpAndSoptStoryIn(
            @Param("ip") String ip,
            @Param("soptStories") List<SoptStory> soptStories
    );
}