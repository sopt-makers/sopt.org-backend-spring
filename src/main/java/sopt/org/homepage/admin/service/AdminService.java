package sopt.org.homepage.admin.service;

import sopt.org.homepage.admin.dto.request.*;
import sopt.org.homepage.admin.dto.response.AddMainResponseDto;
import sopt.org.homepage.admin.dto.response.GetMainNewsResponseDto;
import sopt.org.homepage.admin.dto.response.GetMainResponseDto;

public interface AdminService {
    AddMainResponseDto addMainData(AddMainRequestDto addMainRequestDto);
    void addMainDataConfirm(AddMainConfirmRequestDto addMainConfirmRequestDto);
    GetMainResponseDto getMain(GetMainRequestDto getMainRequestDto);
    void addMainNews(AddMainNewsRequestDto addMainNewsRequestDto);
    void deleteMainNews(DeleteMainNewsRequestDto deleteMainNewsRequestDto);
    GetMainNewsResponseDto getMainNews(GetMainNewsRequestDto getMainNewsRequestDto);
}