package sopt.org.homepage.scrap.service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.homepage.scrap.CrawlerComposite;
import sopt.org.homepage.scrap.LinkSource;
import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.scrap.dto.ScrapArticleDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScraperServiceImpl implements ScraperService {

	private final CrawlerComposite crawlerComposite;

	@Override
	public CreateScraperResponseDto scrap(ScrapArticleDto dto) {
		try {
			LinkSource source = LinkSource.parseSource(dto.getArticleUrl());
			return crawlerComposite.crawl(source, dto.getArticleUrl());
		} catch (IOException e) {
			log.error("Scraping failed for URL: {}", dto.getArticleUrl(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "스크래핑에 실패했습니다");
		}
	}

}
