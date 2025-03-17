package sopt.org.homepage.scrap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Component
public class WebDriverManager {

	public WebDriver fetch() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");

		String isDockerBuild = System.getenv("IS_DOCKER_BUILD");
		if (isDockerBuild.equals("true")) {
			options.setBinary("/usr/bin/chromium");
		}

		return new ChromeDriver(options);
	}

	public void quit(WebDriver webDriver) {
		webDriver.quit();
	}
}
