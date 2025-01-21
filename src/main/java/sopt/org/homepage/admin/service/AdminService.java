package sopt.org.homepage.admin.service;

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

public interface AdminService {
    AddAdminResponseDto addMainData(AddAdminRequestDto addAdminRequestDto);
    AddAdminConfirmResponseDto addMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto);
    GetAdminResponseDto getMain(GetAdminRequestDto getAdminRequestDto);
    AddAdminNewsResponseDto addMainNews(AddAdminNewsRequestDto addAdminNewsRequestDto);
    DeleteAdminNewsResponseDto deleteMainNews(DeleteAdminNewsRequestDto deleteAdminNewsRequestDto);
    GetAdminNewsResponseDto getMainNews(GetAdminNewsRequestDto getAdminNewsRequestDto);
}