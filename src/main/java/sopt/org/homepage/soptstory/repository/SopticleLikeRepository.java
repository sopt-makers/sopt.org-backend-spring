package sopt.org.homepage.soptstory.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.homepage.soptstory.entity.SoptStoryEntity;
import sopt.org.homepage.soptstory.entity.SoptStoryLikeEntity;

public interface SopticleLikeRepository extends JpaRepository<SoptStoryLikeEntity, Long> {
    List<SoptStoryLikeEntity> findAllBySessionIdAndSopticleIn(String sessionId, List<SoptStoryEntity> sopticles);
    boolean existsBySopticleIdAndSessionId(Long sopticleId, String sessionId);
    Optional<SoptStoryLikeEntity> findBySopticleIdAndSessionId(Long sopticleId, String sessionId);
}