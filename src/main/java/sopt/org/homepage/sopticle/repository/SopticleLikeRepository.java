package sopt.org.homepage.sopticle.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.homepage.sopticle.entity.SopticleEntity;
import sopt.org.homepage.sopticle.entity.SopticleLikeEntity;

public interface SopticleLikeRepository extends JpaRepository<SopticleLikeEntity, Long> {
    List<SopticleLikeEntity> findAllBySessionIdAndSopticleIn(String sessionId, List<SopticleEntity> sopticles);
}
