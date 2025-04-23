package sopt.org.homepage.review.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import sopt.org.homepage.exception.ClientBadRequestException;

public enum AddReviewMainCategory {
	ACTIVITY("전체 활동"),
	RECRUITING("서류/면접");

	private final String value;

	AddReviewMainCategory(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static AddReviewMainCategory from(String value) {
		for (AddReviewMainCategory category : AddReviewMainCategory.values()) {
			if (category.getValue().equals(value)) {
				return category;
			}
		}
		throw new ClientBadRequestException("유효하지 않은 메인 유형 입니다: " + value + ". 가능한 값: " + getPossibleValues());
	}

	public static String getPossibleValues() {
		StringBuilder sb = new StringBuilder();
		for (AddReviewMainCategory category : AddReviewMainCategory.values()) {
			sb.append(category.getValue()).append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}
}
