package sopt.org.homepage.review.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import sopt.org.homepage.exception.ClientBadRequestException;

public enum AddReviewSubRecruiting {
	ALL("서류/면접"),
	RESUME("서류"),
	INTERVIEW("면접");

	private final String value;

	AddReviewSubRecruiting(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static AddReviewSubRecruiting from(String value) {
		for (AddReviewSubRecruiting recruiting : AddReviewSubRecruiting.values()) {
			if (recruiting.getValue().equals(value)) {
				return recruiting;
			}
		}
		throw new ClientBadRequestException("유효하지 않은 세부 유형입니다: " + value + ". 가능한 값: " + getPossibleValues());
	}

	public static String getPossibleValues() {
		StringBuilder sb = new StringBuilder();
		for (AddReviewSubRecruiting recruiting : AddReviewSubRecruiting.values()) {
			sb.append(recruiting.getValue()).append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}
}
