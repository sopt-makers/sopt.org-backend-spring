package sopt.org.homepage.global.exception;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super("[TokenException] : " + message);
    }
}
