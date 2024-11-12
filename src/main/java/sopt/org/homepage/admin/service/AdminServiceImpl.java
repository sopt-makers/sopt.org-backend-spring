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
import sopt.org.homepage.admin.dto.response.main.AddAdminResponseDto;
import sopt.org.homepage.admin.dto.response.main.GetAdminResponseDto;
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

    public void addMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto) {
        mainService.adminAddMainDataConfirm(addAdminConfirmRequestDto);
    }

    @Transactional
    public GetAdminResponseDto getMain(GetAdminRequestDto getAdminRequestDto) {
        return mainService.adminGetMain(getAdminRequestDto);
    }


    public void addMainNews(AddAdminNewsRequestDto addAdminNewsRequestDto) {
        mainService.adminAddMainNews(addAdminNewsRequestDto);
    }

    public void deleteMainNews(DeleteAdminNewsRequestDto deleteAdminNewsRequestDto) {
        mainService.adminDeleteMainNews(deleteAdminNewsRequestDto);
    }

    public GetAdminNewsResponseDto getMainNews(GetAdminNewsRequestDto getAdminNewsRequestDto) {
        return mainService.adminGetMainNews(getAdminNewsRequestDto);
    }


}
