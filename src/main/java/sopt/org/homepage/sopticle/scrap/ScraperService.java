package sopt.org.homepage.sopticle.scrap;

import sopt.org.homepage.sopticle.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.sopticle.scrap.dto.ScrapArticleDto;

public interface ScraperService {
    /**
     * 웹 페이지를 스크랩하여 메타데이터를 추출합니다.
     *
     * @param dto 스크랩할 기사 URL 정보
     * @return 스크랩된 메타데이터 (썸네일, 제목, 설명 등)
     */
    CreateScraperResponseDto scrap(ScrapArticleDto dto);
}