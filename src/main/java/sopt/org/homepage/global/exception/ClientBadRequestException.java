package sopt.org.homepage.global.exception;

public class ClientBadRequestException extends BusinessLogicException {
    public ClientBadRequestException(String message) {
        super("[ClientBadRequestException] : " + message);
    }
}
