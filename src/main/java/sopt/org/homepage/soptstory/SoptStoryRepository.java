package sopt.org.homepage.soptstory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.soptstory.domain.SoptStory;

/**
 * SoptStory Repository
 */
@Repository
public interface SoptStoryRepository extends JpaRepository<SoptStory, Long> {

    boolean existsByUrl(String url);

    Page<SoptStory> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<SoptStory> findAllByOrderByLikeCountDesc(Pageable pageable);
}
