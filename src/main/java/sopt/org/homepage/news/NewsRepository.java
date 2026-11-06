package sopt.org.homepage.news;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * NewsRepository
 * <p>
 * 최신소식 Repository
 */
public interface NewsRepository extends JpaRepository<News, Integer> {
    boolean existsByInstagramPostId(String instagramPostId);

    @Query("select max(n.publishedAt) from News n where n.instagramPostId is not null")
    java.time.Instant findLatestInstagramPublishedAt();

    void deleteByInstagramPostIdIsNull();

    @Query("select n from News n where n.hidden = false order by n.publishedAt desc nulls last, n.createdAt desc, n.id desc")
    List<News> findAllByPublishedAtDesc();

    @Query(value = "select * from main_news where hidden = false order by published_at desc nulls last, created_at desc, id desc limit :limit", nativeQuery = true)
    List<News> findLatest(@Param("limit") int limit);

    boolean existsByInstagramPostIdIsNotNullAndLinkIn(List<String> links);
}
