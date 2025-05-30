package sopt.org.homepage.soptstory.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.homepage.soptstory.entity.SoptStoryEntity;
import sopt.org.homepage.soptstory.entity.SoptStoryLikeEntity;

public interface SoptStoryLikeRepository extends JpaRepository<SoptStoryLikeEntity, Long> {
    List<SoptStoryLikeEntity> findAllByIpAndSoptStoryIn(String ip, List<SoptStoryEntity> soptStorys);
    boolean existsBySoptStoryIdAndIp(Long soptStoryId, String ip);
    Optional<SoptStoryLikeEntity> findBySoptStoryIdAndIp(Long soptStoryId, String ip);
}
