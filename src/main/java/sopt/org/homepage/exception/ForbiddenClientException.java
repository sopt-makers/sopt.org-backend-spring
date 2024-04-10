package sopt.org.homepage.exception;

public class ForbiddenClientException extends BusinessLogicException {
    public ForbiddenClientException(String message) { super("[ForbiddenClientException] : " + message); }
}
