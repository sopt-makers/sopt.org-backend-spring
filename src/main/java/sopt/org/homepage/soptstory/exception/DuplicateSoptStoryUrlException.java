package sopt.org.homepage.soptstory.exception;

/**
 * 중복된 SoptStory URL로 생성하려고 할 때 발생하는 예외
 */
public class DuplicateSoptStoryUrlException extends RuntimeException {

    public DuplicateSoptStoryUrlException(String url) {
        super(String.format("이미 등록된 솝티클입니다. URL: %s", url));
    }
}