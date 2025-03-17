package sopt.org.homepage.sopticle.scrap.crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import sopt.org.homepage.sopticle.scrap.LinkCrawler;
import sopt.org.homepage.sopticle.scrap.LinkSource;
import sopt.org.homepage.sopticle.scrap.dto.CreateScraperResponseDto;

@Component
public class VelogMainLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";

	@Override
	public LinkSource supportSource() {
		return LinkSource.VELOG_MAIN;
	}

	@Override
	public CreateScraperResponseDto crawl(String link) throws IOException {
		Document document = scrap(link);
		String title = extractTitle(document);
		String image = extractImage(document);
		String description = extractDescription(document);
		return new CreateScraperResponseDto(image, title, description, link);
	}

	private Document scrap(String link) throws IOException {
		return Jsoup.connect(link)
			.timeout(TIMEOUT_MILLISECONDS)
			.get();
	}

	private String extractTitle(Document document) {
		Element head = document.head();
		return head.getElementsByTag("title").text();
	}

	private String extractImage(Document document) {
		Element image = document.selectFirst(
			"body > div > div:nth-child(2) > div:nth-child(2) > main > div > div:nth-child(1) > div:nth-child(1) > div > a > img");
		if (image == null) {
			return EMPTY_STRING;
		}
		return image.attr("src");
	}

	private String extractDescription(Document document) {
		Element description = document.selectFirst(
			"body > div > div:nth-child(2) > div:nth-child(2) > main > div > div:nth-child(1) > div:nth-child(1) > div > div > div:nth-child(2)");
		if (description == null) {
			return EMPTY_STRING;
		}
		return description.text();
	}
}
