package sopt.org.homepage.soptstory.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.homepage.soptstory.entity.SoptStoryEntity;
import sopt.org.homepage.soptstory.entity.SoptStoryLikeEntity;

public interface SoptStoryLikeRepository extends JpaRepository<SoptStoryLikeEntity, Long> {
    List<SoptStoryLikeEntity> findAllBySessionIdAndSoptStoryIn(String sessionId, List<SoptStoryEntity> soptStorys);
    boolean existsBySoptStoryIdAndSessionId(Long soptStoryId, String sessionId);
    Optional<SoptStoryLikeEntity> findBySoptStoryIdAndSessionId(Long soptStoryId, String sessionId);
}