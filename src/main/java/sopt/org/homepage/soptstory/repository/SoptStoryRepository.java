package sopt.org.homepage.soptstory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.soptstory.entity.SoptStoryEntity;

@Repository
public interface SoptStoryRepository extends JpaRepository<SoptStoryEntity, Long> {
    boolean existsBySoptStoryUrl(String soptStoryUrl);
}