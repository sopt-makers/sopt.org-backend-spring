package sopt.org.homepage.review.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
		throw new IllegalArgumentException("Unknown Sub Recruiting value: " + value);
	}
}
