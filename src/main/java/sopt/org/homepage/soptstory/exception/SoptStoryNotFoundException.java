package sopt.org.homepage.soptstory.exception;

/**
 * SoptStory를 찾을 수 없을 때 발생하는 예외
 */
public class SoptStoryNotFoundException extends RuntimeException {

    public SoptStoryNotFoundException(Long id) {
        super(String.format("SoptStory를 찾을 수 없습니다. ID: %d", id));
    }

    public SoptStoryNotFoundException(String message) {
        super(message);
    }
}