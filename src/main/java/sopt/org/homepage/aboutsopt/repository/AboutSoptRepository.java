package sopt.org.homepage.aboutsopt.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.aboutsopt.entity.AboutSoptEntity;

@Repository
public interface AboutSoptRepository extends JpaRepository<AboutSoptEntity, Long> {
    Optional<AboutSoptEntity> findTopByIsPublishedTrueOrderByIdDesc();
    Optional<AboutSoptEntity> findByIdAndIsPublishedTrue(Long id);
}