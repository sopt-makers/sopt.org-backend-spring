package sopt.org.homepage.admin.service;

import sopt.org.homepage.admin.dto.request.main.AddAdminConfirmRequestDto;
import sopt.org.homepage.admin.dto.request.main.AddAdminRequestDto;
import sopt.org.homepage.admin.dto.request.main.GetAdminRequestDto;
import sopt.org.homepage.admin.dto.request.news.AddAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.DeleteAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.GetAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.response.main.AddAdminResponseDto;
import sopt.org.homepage.admin.dto.response.news.GetAdminNewsResponseDto;
import sopt.org.homepage.admin.dto.response.main.GetAdminResponseDto;

public interface AdminService {
    AddAdminResponseDto addMainData(AddAdminRequestDto addAdminRequestDto);
    void addMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto);
    GetAdminResponseDto getMain(GetAdminRequestDto getAdminRequestDto);
    void addMainNews(AddAdminNewsRequestDto addAdminNewsRequestDto);
    void deleteMainNews(DeleteAdminNewsRequestDto deleteAdminNewsRequestDto);
    GetAdminNewsResponseDto getMainNews(GetAdminNewsRequestDto getAdminNewsRequestDto);
}