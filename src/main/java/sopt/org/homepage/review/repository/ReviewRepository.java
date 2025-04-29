package sopt.org.homepage.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.homepage.review.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
	boolean existsByUrl(String reviewUrl);

	List<ReviewEntity> findAllByAuthor(String authorName);
}
