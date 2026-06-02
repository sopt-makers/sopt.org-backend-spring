package sopt.org.homepage.application.admin.service;

import sopt.org.homepage.application.admin.dto.AddAdminAboutRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminAboutResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminCommonRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminCommonResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminConfirmRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminConfirmResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminHomeRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminHomeResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminRecruitRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminRecruitResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminResponseDto;
import sopt.org.homepage.application.admin.dto.GetAdminRequestDto;
import sopt.org.homepage.application.admin.dto.GetAdminResponseDto;

public interface AdminService {

    GetAdminResponseDto getMain(GetAdminRequestDto getAdminRequestDto);

    // ===== 기존 통합 배포 =====
    AddAdminResponseDto addMainData(AddAdminRequestDto addAdminRequestDto);
    AddAdminConfirmResponseDto addMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto);

    // ===== 탭별 배포 - 공통 =====
    AddAdminCommonResponseDto addCommonData(AddAdminCommonRequestDto request);
    AddAdminConfirmResponseDto addCommonDataConfirm(AddAdminConfirmRequestDto request);

    // ===== 탭별 배포 - 홈 =====
    AddAdminHomeResponseDto addHomeData(AddAdminHomeRequestDto request);
    AddAdminConfirmResponseDto addHomeDataConfirm(AddAdminConfirmRequestDto request);

    // ===== 탭별 배포 - 소개 =====
    AddAdminAboutResponseDto addAboutData(AddAdminAboutRequestDto request);
    AddAdminConfirmResponseDto addAboutDataConfirm(AddAdminConfirmRequestDto request);

    // ===== 탭별 배포 - 모집안내 =====
    AddAdminRecruitResponseDto addRecruitData(AddAdminRecruitRequestDto request);
    AddAdminConfirmResponseDto addRecruitDataConfirm(AddAdminConfirmRequestDto request);
}
