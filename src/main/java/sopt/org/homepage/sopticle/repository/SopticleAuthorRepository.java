package sopt.org.homepage.sopticle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.sopticle.entity.SopticleAuthorEntity;

public interface SopticleAuthorRepository extends JpaRepository<SopticleAuthorEntity, Long> {
}