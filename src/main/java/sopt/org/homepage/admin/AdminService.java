package sopt.org.homepage.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sopt.org.homepage.admin.dao.*;
import sopt.org.homepage.admin.dto.*;
import sopt.org.homepage.admin.dto.request.*;
import sopt.org.homepage.admin.dto.response.AddMainResponseDto;
import sopt.org.homepage.admin.dto.response.CoreValueResponseRecordDto;
import sopt.org.homepage.admin.dto.response.GetMainNewsResponseDto;
import sopt.org.homepage.admin.dto.response.MemberResponseRecordDto;
import sopt.org.homepage.aws.s3.S3Service;
import sopt.org.homepage.cache.CacheService;
import sopt.org.homepage.common.constants.CacheType;
import sopt.org.homepage.exception.ClientBadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminService {
    private final MainRepository mainRepository;
    private final MainNewsRepository mainNewsRepository;

    private final S3Service s3Service;
    private final CacheService cacheService;

    public AddMainResponseDto addMainData(AddMainRequestDto addMainRequestDto) {
        String baseDir = addMainRequestDto.getGeneration() + "/";

        MainEntity mainEntity = new MainEntity();
        mainEntity.setGeneration(addMainRequestDto.getGeneration());
        mainEntity.setName(addMainRequestDto.getName());

        List<RecruitScheduleDao> recruitSchedule = RecruitScheduleDto.toDaoList(addMainRequestDto.getRecruitSchedule());
        mainEntity.setRecruitSchedule(recruitSchedule);

        BrandingColorDao brandingColor = addMainRequestDto.getBrandingColor().toDao();
        mainEntity.setBrandingColor(brandingColor);

        MainButtonDao mainButton = addMainRequestDto.getMainButton().toDao();
        mainEntity.setMainButton(mainButton);

        List<PartIntroductionDao> partIntroduction = PartIntroductionDto.toDaoList(addMainRequestDto.getPartIntroduction());
        mainEntity.setPartIntroduction(partIntroduction);

        mainEntity.setHeaderImage(s3Service.generatePresignedUrl(addMainRequestDto.getHeaderImageFileName(), baseDir));

        List<String> coreValueImages = new ArrayList<>();
        for (CoreValueDto coreValue: addMainRequestDto.getCoreValue()) {
            String fileName = coreValue.getImageFileName();
            String presignedUrl = s3Service.generatePresignedUrl(fileName, baseDir + "coreValue/");
            coreValueImages.add(presignedUrl);
        }
        List<CoreValueDao> coreValue = CoreValueDto.toDaoList(addMainRequestDto.getCoreValue(), coreValueImages);
        mainEntity.setCoreValue(coreValue);

        List<PartCurriculumDao> partCurriculum = PartCurriculumDto.toDaoList(addMainRequestDto.getPartCurriculum());
        mainEntity.setPartCurriculum(partCurriculum);

        List<String> memberProfileImages = new ArrayList<>();
        for (MemberDto member: addMainRequestDto.getMember()) {
            String fileName = member.getProfileImageFileName();
            String presignedUrl = s3Service.generatePresignedUrl(fileName, baseDir + "member/");
            memberProfileImages.add(presignedUrl);
        }
        List<MemberDao> member = MemberDto.toDaoList(addMainRequestDto.getMember(), memberProfileImages);
        mainEntity.setMember(member);

        mainEntity.setRecruitHeaderImage(s3Service.generatePresignedUrl(addMainRequestDto.getRecruitHeaderImageFileName(), baseDir));

        List<RecruitPartCurriculumDao> recruitPartCurriculum = RecruitPartCurriculumDto.toDaoList(addMainRequestDto.getRecruitPartCurriculum());
        mainEntity.setRecruitPartCurriculum(recruitPartCurriculum);

        List<RecruitQuestionDao> recruitQuestion = RecruitQuestionDto.toDaoList(addMainRequestDto.getRecruitQuestion());
        mainEntity.setRecruitQuestion(recruitQuestion);

        cacheService.put(CacheType.MAIN_ENTITY, String.valueOf(mainEntity.getGeneration()), mainEntity);

        return AddMainResponseDto.builder()
                .generation(mainEntity.getGeneration())
                .headerImage(mainEntity.getHeaderImage())
                .coreValues(mainEntity.getCoreValue().stream().map(coreValueDao ->
                        CoreValueResponseRecordDto.builder()
                                .value(coreValueDao.getValue())
                                .image(coreValueDao.getImage())
                                .build()
                ).collect(Collectors.toList()))
                .members(mainEntity.getMember().stream().map(memberDao ->
                        MemberResponseRecordDto.builder()
                                .role(memberDao.getRole())
                                .name(memberDao.getName())
                                .profileImage(memberDao.getProfileImage())
                                .build()
                ).collect(Collectors.toList()))
                .recruitHeaderImage(mainEntity.getRecruitHeaderImage())
                .build();
    }

    public void addMainDataConfirm(AddMainConfirmRequestDto addMainConfirmRequestDto) {
        MainEntity mainEntityCache = cacheService.get(CacheType.MAIN_ENTITY, String.valueOf(addMainConfirmRequestDto.getGeneration()), MainEntity.class);
        System.out.println(mainEntityCache.getHeaderImage());

    }

    public void addMainNews(AddMainNewsRequestDto addMainNewsRequestDto) {
        String imageUrl = s3Service.uploadFile(addMainNewsRequestDto.getImage(), "news/");
        MainNewsEntity newsEntity = new MainNewsEntity();
        newsEntity.setTitle(addMainNewsRequestDto.getTitle());
        newsEntity.setLink(addMainNewsRequestDto.getLink());
        newsEntity.setImage(imageUrl);

        mainNewsRepository.save(newsEntity);
    }

    public void deleteMainNews(DeleteMainNewsRequestDto deleteMainNewsRequestDto) {
        MainNewsEntity newsEntity = mainNewsRepository.findById(deleteMainNewsRequestDto.getId());
        if (newsEntity == null) {
            throw new ClientBadRequestException("News not found with id: " + deleteMainNewsRequestDto.getId());
        }

        s3Service.deleteFile(newsEntity.getImage());

        mainNewsRepository.delete(newsEntity);
    }

    public GetMainNewsResponseDto getMainNews(GetMainNewsRequestDto getMainNewsRequestDto) {
        MainNewsEntity newsEntity = mainNewsRepository.findById(getMainNewsRequestDto.getId());
        if (newsEntity == null) {
            throw new ClientBadRequestException("News not found with id: " + getMainNewsRequestDto.getId());
        }

        return GetMainNewsResponseDto.builder()
                .id(newsEntity.getId())
                .title(newsEntity.getTitle())
                .link(newsEntity.getLink())
                .image(newsEntity.getImage())
                .build();
    }

//    public GetNotificationListResponseDto getNotificationEmailList(int generation) {
//        List<String> emailList = notificationRepository.findByGeneration(generation).stream()
//                .map(MainEntity::getEmail)
//                .collect(Collectors.toList());
//
//        return new GetNotificationListResponseDto(generation, emailList);
//    }


}
