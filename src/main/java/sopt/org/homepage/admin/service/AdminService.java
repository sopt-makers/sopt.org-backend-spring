package sopt.org.homepage.admin.service;

import sopt.org.homepage.news.controller.dto.request.AddAdminConfirmRequestDto;
import sopt.org.homepage.news.controller.dto.request.AddAdminRequestDto;
import sopt.org.homepage.news.controller.dto.request.GetAdminRequestDto;
import sopt.org.homepage.news.controller.dto.request.AddAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.request.DeleteAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.request.GetAdminNewsRequestDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminConfirmResponseDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminResponseDto;
import sopt.org.homepage.news.controller.dto.response.GetAdminResponseDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminNewsResponseDto;
import sopt.org.homepage.news.controller.dto.response.DeleteAdminNewsResponseDto;
import sopt.org.homepage.news.controller.dto.response.GetAdminNewsResponseDto;

public interface AdminService {
    AddAdminResponseDto addMainData(AddAdminRequestDto addAdminRequestDto);
    AddAdminConfirmResponseDto addMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto);
    GetAdminResponseDto getMain(GetAdminRequestDto getAdminRequestDto);
}
