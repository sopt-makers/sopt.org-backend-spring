package sopt.org.homepage.application.admin.service;

import sopt.org.homepage.application.admin.dto.AddAdminConfirmRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminConfirmResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminResponseDto;
import sopt.org.homepage.application.admin.dto.GetAdminRequestDto;
import sopt.org.homepage.application.admin.dto.GetAdminResponseDto;

public interface AdminService {
    AddAdminResponseDto addMainData(AddAdminRequestDto addAdminRequestDto);

    AddAdminConfirmResponseDto addMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto);

    GetAdminResponseDto getMain(GetAdminRequestDto getAdminRequestDto);
}
