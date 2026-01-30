package sopt.org.homepage.global.exception;

public class ForbiddenClientException extends BusinessLogicException {
    public ForbiddenClientException(String message) {
        super("[ForbiddenClientException] : " + message);
    }
}
