package sopt.org.homepage.exception;

public class TokenException extends RuntimeException {
    public TokenException(String message) { super("[TokenException] : " + message); }
}
