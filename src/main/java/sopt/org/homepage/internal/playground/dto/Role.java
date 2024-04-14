package sopt.org.homepage.internal.playground.dto;

public enum Role {
        TEAMLEADER("Team Leader"),
        MAINPM("Main PM"),
        PM("PM"),
        TEAMIMPROVEMENT("Team Improvement"),
        DESIGN("디자이너"),
        IOS("iOS 개발자"),
        ANDROID("Android 개발자"),
        WEB("웹 프론트엔드 개발자"),
        SERVER("서버 개발자");

        private final String description;

        Role(String description) {
                this.description = description;
        }

        public String getDescription() {
                return description;
        }

        public static Role fromValue(String value) {
                for (Role role : values()) {
                        if (role.toString().equalsIgnoreCase(value)) {
                                return role;
                        }
                }
                throw new IllegalArgumentException("No constant with text " + value + " found");
        }
}
