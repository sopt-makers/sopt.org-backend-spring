package sopt.org.homepage.sopticle.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CreateSopticleAuthorRole {
	WEB("웹"),
	PLAN("기획"),
	DESIGN("디자인"),
	IOS("iOS"),
	SERVER("서버"),
	ANDROID("안드로이드"),
	WEB_LEADER("웹 파트장"),
	PLAN_LEADER("기획 파트장"),
	DESIGN_LEADER("디자인 파트장"),
	IOS_LEADER("iOS 파트장"),
	SERVER_LEADER("서버 파트장"),
	ANDROID_LEADER("안드로이드 파트장"),
	PRESIDENT("회장"),
	VICE_PRESIDENT("부회장"),
	GENERAL_AFFAIRS("총무"),
	OPERATION_LEADER("운영 팀장"),
	MEDIA_LEADER("미디어 팀장");

	private final String value;

	CreateSopticleAuthorRole(String value) {
		this.value = value;
	}

	@JsonValue  // 직렬화 시 한글 값 사용
	public String getValue() {
		return value;
	}

	@JsonCreator  // 역직렬화 시 한글 값으로부터 enum 생성
	public static CreateSopticleAuthorRole from(String value) {
		for (CreateSopticleAuthorRole role : CreateSopticleAuthorRole.values()) {
			if (role.getValue().equals(value)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role value: " + value);
	}
}
