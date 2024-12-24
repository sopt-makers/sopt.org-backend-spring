package sopt.org.homepage.sopticle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.sopticle.entity.SopticleEntity;

@Repository
public interface SopticleRepository extends JpaRepository<SopticleEntity, Long> {
    boolean existsBySopticleUrl(String sopticleUrl);
}