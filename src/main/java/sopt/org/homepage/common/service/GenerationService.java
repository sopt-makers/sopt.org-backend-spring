package sopt.org.homepage.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.org.homepage.main.entity.MainEntity;
import sopt.org.homepage.main.repository.MainRepository;

@Service
@RequiredArgsConstructor
public class GenerationService {
    private final MainRepository mainRepository;

    public int getLatestGeneration() {
        MainEntity mainEntity = mainRepository.findFirstByOrderByGenerationDesc();
        return mainEntity.getGeneration();
    }
}