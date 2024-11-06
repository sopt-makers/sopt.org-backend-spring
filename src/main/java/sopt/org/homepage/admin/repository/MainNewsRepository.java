package sopt.org.homepage.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.admin.entity.MainNewsEntity;

public interface MainNewsRepository extends JpaRepository<MainNewsEntity, Integer> {
    MainNewsEntity findById(int id);
}
