package sopt.org.homepage.global.common.constants;

import org.springframework.beans.factory.annotation.Value;

public class SecurityConstants {
    public static final String SCHEME_NAME = "Authorization";

    @Value("${jwt.access}")
    public static String JWT_KEY;

    @Value("${jwt.refresh}")
    private static String REFRESH_KEY;

//    public static long EXPIRATION_TIME = 864_000_000; // 10 days

    private SecurityConstants() {
    }
}
