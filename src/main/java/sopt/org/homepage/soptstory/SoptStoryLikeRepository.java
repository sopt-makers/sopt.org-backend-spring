package sopt.org.homepage.soptstory;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.domain.SoptStoryLike;

/**
 * SoptStoryLike Repository
 */
@Repository
public interface SoptStoryLikeRepository extends JpaRepository<SoptStoryLike, Long> {

    boolean existsBySoptStory_IdAndIp(Long soptStoryId, String ip);

    Optional<SoptStoryLike> findBySoptStory_IdAndIp(Long soptStoryId, String ip);

    @Query("SELECT sl FROM SoptStoryLike sl WHERE sl.ip = :ip AND sl.soptStory IN :soptStories")
    List<SoptStoryLike> findAllByIpAndSoptStoryIn(
            @Param("ip") String ip,
            @Param("soptStories") List<SoptStory> soptStories
    );
}
