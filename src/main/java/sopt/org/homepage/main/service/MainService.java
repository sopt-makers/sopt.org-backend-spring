package sopt.org.homepage.main.service;

import sopt.org.homepage.admin.dto.request.main.AddAdminConfirmRequestDto;
import sopt.org.homepage.admin.dto.request.main.AddAdminRequestDto;
import sopt.org.homepage.admin.dto.request.main.GetAdminRequestDto;
import sopt.org.homepage.admin.dto.request.news.AddAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.DeleteAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.GetAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.response.main.AddAdminResponseDto;
import sopt.org.homepage.admin.dto.response.main.GetAdminResponseDto;
import sopt.org.homepage.admin.dto.response.news.GetAdminNewsResponseDto;
import sopt.org.homepage.main.dto.response.GetAboutPageResponseDto;
import sopt.org.homepage.main.dto.response.GetMainPageResponseDto;
import sopt.org.homepage.main.dto.response.GetRecruitingPageResponseDto;

public interface MainService {
    AddAdminResponseDto adminAddMainData(AddAdminRequestDto addAdminRequestDto);
    void adminAddMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto);
    GetAdminResponseDto adminGetMain(GetAdminRequestDto getAdminRequestDto);
    void adminAddMainNews(AddAdminNewsRequestDto addAdminNewsRequestDto);
    void adminDeleteMainNews(DeleteAdminNewsRequestDto deleteAdminNewsRequestDto);
    GetAdminNewsResponseDto adminGetMainNews(GetAdminNewsRequestDto getAdminNewsRequestDto);
    GetMainPageResponseDto getMainPageData();
    GetAboutPageResponseDto getAboutPageData();
    GetRecruitingPageResponseDto getRecruitingPageData();
    int getLatestGeneration();
}