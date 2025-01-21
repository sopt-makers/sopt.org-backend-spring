package sopt.org.homepage.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.admin.dto.request.main.AddAdminConfirmRequestDto;
import sopt.org.homepage.admin.dto.request.main.AddAdminRequestDto;
import sopt.org.homepage.admin.dto.request.main.GetAdminRequestDto;
import sopt.org.homepage.admin.dto.request.news.AddAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.DeleteAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.GetAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.response.main.AddAdminConfirmResponseDto;
import sopt.org.homepage.admin.dto.response.main.AddAdminResponseDto;
import sopt.org.homepage.admin.dto.response.main.GetAdminResponseDto;
import sopt.org.homepage.admin.dto.response.news.AddAdminNewsResponseDto;
import sopt.org.homepage.admin.dto.response.news.DeleteAdminNewsResponseDto;
import sopt.org.homepage.admin.dto.response.news.GetAdminNewsResponseDto;
import sopt.org.homepage.main.service.MainService;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminServiceImpl implements AdminService{
    private final MainService mainService;

    public AddAdminResponseDto addMainData(AddAdminRequestDto addAdminRequestDto) {
        return mainService.adminAddMainData(addAdminRequestDto);
    }

    public AddAdminConfirmResponseDto addMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto) {
        mainService.adminAddMainDataConfirm(addAdminConfirmRequestDto);
        return AddAdminConfirmResponseDto.builder()
                .message("파일 업로드 확인 및 어드민 데이터 배포 성공")
                .build();
    }

    @Transactional
    public GetAdminResponseDto getMain(GetAdminRequestDto getAdminRequestDto) {
        return mainService.adminGetMain(getAdminRequestDto);
    }


    public AddAdminNewsResponseDto addMainNews(AddAdminNewsRequestDto addAdminNewsRequestDto) {
        mainService.adminAddMainNews(addAdminNewsRequestDto);
        return AddAdminNewsResponseDto.builder()
                .message("최신소식 추가 성공")
                .build();
    }

    public DeleteAdminNewsResponseDto deleteMainNews(DeleteAdminNewsRequestDto deleteAdminNewsRequestDto) {
        mainService.adminDeleteMainNews(deleteAdminNewsRequestDto);
        return DeleteAdminNewsResponseDto.builder()
                .message("최신소식 삭제 성공")
                .build();
    }

    public GetAdminNewsResponseDto getMainNews(GetAdminNewsRequestDto getAdminNewsRequestDto) {
        return mainService.adminGetMainNews(getAdminNewsRequestDto);
    }


}
