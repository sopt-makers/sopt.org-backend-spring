package sopt.org.homepage.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.main.entity.MainNewsEntity;

public interface MainNewsRepository extends JpaRepository<MainNewsEntity, Integer> {
    MainNewsEntity findById(int id);
}
