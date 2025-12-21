package sopt.org.homepage.application.admin.service;

import sopt.org.homepage.news.controller.dto.request.AddAdminConfirmRequestDto;
import sopt.org.homepage.news.controller.dto.request.AddAdminRequestDto;
import sopt.org.homepage.news.controller.dto.request.GetAdminRequestDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminConfirmResponseDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminResponseDto;
import sopt.org.homepage.news.controller.dto.response.GetAdminResponseDto;

public interface AdminService {
    AddAdminResponseDto addMainData(AddAdminRequestDto addAdminRequestDto);

    AddAdminConfirmResponseDto addMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto);

    GetAdminResponseDto getMain(GetAdminRequestDto getAdminRequestDto);
}
