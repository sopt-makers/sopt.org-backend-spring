package sopt.org.homepage.application.homepage.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.application.homepage.review.domain.HomepageReview;

public interface HomepageReviewRepository extends JpaRepository<HomepageReview, Long> {

    List<HomepageReview> findAllByOrderByIdAsc();
}
