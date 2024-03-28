package sopt.org.homepage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.dto.SemesterDao;
import sopt.org.homepage.entity.SemesterEntity;
import sopt.org.homepage.repository.SemestersQueryRepository;
import sopt.org.homepage.repository.SemestersRepository;

import java.util.List;
import lombok.val;

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
