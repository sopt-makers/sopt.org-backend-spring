package sopt.org.homepage.infrastructure.external.scrap;

import java.io.IOException;

import sopt.org.homepage.infrastructure.external.scrap.dto.CreateScraperResponseDto;

public interface LinkCrawler {

    LinkSource supportSource();

    CreateScraperResponseDto crawl(String link) throws IOException;
}
