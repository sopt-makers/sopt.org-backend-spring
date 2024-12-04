package sopt.org.homepage.review.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.review.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
}
