package sopt.org.homepage.review.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
		throw new IllegalArgumentException("Unknown Main Category value: " + value);
	}
}
