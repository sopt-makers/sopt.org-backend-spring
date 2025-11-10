package sopt.org.homepage.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.news.MainNewsEntity;

public interface MainNewsRepository extends JpaRepository<MainNewsEntity, Integer> {
    MainNewsEntity findById(int id);
}
