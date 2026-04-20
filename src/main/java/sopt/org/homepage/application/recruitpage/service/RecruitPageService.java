package sopt.org.homepage.application.recruitpage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.application.recruitpage.dto.RecruitMainPageResponse;
import sopt.org.homepage.corevalue.CoreValueService;
import sopt.org.homepage.corevalue.dto.CoreValueView;
import sopt.org.homepage.faq.FAQService;
import sopt.org.homepage.faq.dto.FAQView;
import sopt.org.homepage.generation.GenerationService;
import sopt.org.homepage.generation.dto.GenerationDetailView;
import sopt.org.homepage.member.MemberService;
import sopt.org.homepage.part.PartService;
import sopt.org.homepage.part.dto.PartIntroductionView;
import sopt.org.homepage.recruitment.RecruitmentService;
import sopt.org.homepage.recruitpartintroduction.RecruitPartIntroductionService;
import sopt.org.homepage.recruitpartintroduction.dto.RecruitPartIntroductionView;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitPageService {
    private final CoreValueService coreValueService;
    private final FAQService faqService;
    private final GenerationService generationService;
    private final MemberService memberService;
    private final PartService partService;
    private final RecruitmentService recruitmentService;
    private final RecruitPartIntroductionService recruitPartIntroductionService;


    public RecruitMainPageResponse getRecruitMainPageData(){

        GenerationDetailView generation = generationService.findLatest();
        Integer generationId = generation.id();

        List<CoreValueView> coreValues =
                coreValueService.findByGeneration(generationId);

        List<PartIntroductionView> partIntroductions =
                partService.findIntroductionsByGeneration(generationId);

        List<FAQView> faqs = faqService.findAll();

        return RecruitMainPageResponse.from(generation, coreValues, partIntroductions, faqs);

    }
}
