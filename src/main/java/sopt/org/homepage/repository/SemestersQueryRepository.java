package sopt.org.homepage.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.dto.QSemesterDao;
import sopt.org.homepage.dto.SemesterDao;
import sopt.org.homepage.entity.QSemesterEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SemestersQueryRepository {
    private final JPAQueryFactory queryFactory;
    public List<SemesterDao> findAllByPage(Integer limit, Integer page) {
        val skip = limit * (page - 1);
        val semester = QSemesterEntity.semesterEntity;

        return queryFactory.select(new QSemesterDao(
                    semester.id, semester.color, semester.logo, semester.background, semester.name, semester.year
                ))
                .offset(skip)
                .limit(limit)
                .orderBy(semester.id.desc())
                .fetch();
    }

}
