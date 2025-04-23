package sopt.org.homepage.review.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import sopt.org.homepage.exception.ClientBadRequestException;

public enum AddReviewSubActivity {
	ALL("전체"),
	APPJAM("앱잼"),
	SOPKATHON("솝커톤"),
	SEMINAR("세미나"),
	STUDY("스터디"),
	SOPTERM("솝텀"),
	MAKERS("메이커스");

	private final String value;

	AddReviewSubActivity(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static AddReviewSubActivity from(String value) {
		for (AddReviewSubActivity activity : AddReviewSubActivity.values()) {
			if (activity.getValue().equals(value)) {
				return activity;
			}
		}
		throw new ClientBadRequestException("유효하지 않은 세부 활동 입니다: " + value + ". 가능한 값: " + getPossibleValues());
	}

	public static String getPossibleValues() {
		StringBuilder sb = new StringBuilder();
		for (AddReviewSubActivity activity : AddReviewSubActivity.values()) {
			sb.append(activity.getValue()).append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}
}
