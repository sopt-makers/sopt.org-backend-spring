package sopt.org.homepage.exception;

public class ClientBadRequestException extends BusinessLogicException {
    public ClientBadRequestException(String message) { super("[ClientBadRequestException] : " + message); }
}
