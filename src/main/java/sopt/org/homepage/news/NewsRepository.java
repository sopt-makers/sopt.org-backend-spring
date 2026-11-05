package sopt.org.homepage.news;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * NewsRepository
 * <p>
 * 최신소식 Repository
 */
public interface NewsRepository extends JpaRepository<News, Integer> {
    boolean existsByInstagramPostId(String instagramPostId);

    void deleteByInstagramPostIdIsNull();

    @Query("select n from News n order by n.publishedAt desc nulls last, n.createdAt desc, n.id desc")
    List<News> findAllByPublishedAtDesc();

    boolean existsByInstagramPostIdIsNotNullAndLink(String link);
}
