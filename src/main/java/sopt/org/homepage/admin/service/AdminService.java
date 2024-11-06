package sopt.org.homepage.admin.service;

import sopt.org.homepage.admin.dto.request.main.AddMainConfirmRequestDto;
import sopt.org.homepage.admin.dto.request.main.AddMainRequestDto;
import sopt.org.homepage.admin.dto.request.main.GetMainRequestDto;
import sopt.org.homepage.admin.dto.request.news.AddMainNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.DeleteMainNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.GetMainNewsRequestDto;
import sopt.org.homepage.admin.dto.response.main.AddMainResponseDto;
import sopt.org.homepage.admin.dto.response.news.GetMainNewsResponseDto;
import sopt.org.homepage.admin.dto.response.main.GetMainResponseDto;

public interface AdminService {
    AddMainResponseDto addMainData(AddMainRequestDto addMainRequestDto);
    void addMainDataConfirm(AddMainConfirmRequestDto addMainConfirmRequestDto);
    GetMainResponseDto getMain(GetMainRequestDto getMainRequestDto);
    void addMainNews(AddMainNewsRequestDto addMainNewsRequestDto);
    void deleteMainNews(DeleteMainNewsRequestDto deleteMainNewsRequestDto);
    GetMainNewsResponseDto getMainNews(GetMainNewsRequestDto getMainNewsRequestDto);
}