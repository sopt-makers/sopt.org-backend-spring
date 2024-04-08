package sopt.org.homepage.semester;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.semester.dto.SemesterDao;
import sopt.org.homepage.semester.repo.SemestersQueryRepository;
import sopt.org.homepage.semester.repo.SemestersRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SemestersService {
    private final SemestersRepository semestersRepository;
    private final SemestersQueryRepository semestersQueryRepository;

    @Transactional(readOnly = true)
    public List<SemesterDao> findAll(Integer limit, Integer page) {
        return semestersQueryRepository.findAllByPage(limit, page);
    }

    @Transactional(readOnly = true)
    public Long countAll() {
        return semestersRepository.count();
    }

}
