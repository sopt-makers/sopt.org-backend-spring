package sopt.org.homepage.news;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * NewsRepository
 * <p>
 * 최신소식 Repository
 */
public interface NewsRepository extends JpaRepository<News, Integer> {

}
