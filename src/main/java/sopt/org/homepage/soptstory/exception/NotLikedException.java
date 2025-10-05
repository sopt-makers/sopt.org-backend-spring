package sopt.org.homepage.soptstory.exception;

/**
 * 좋아요를 누르지 않은 상태에서 좋아요 취소를 시도할 때 발생하는 예외
 */
public class NotLikedException extends RuntimeException {

    public NotLikedException(Long soptStoryId, String ip) {
        super(String.format(
                "좋아요를 누르지 않은 상태입니다. SoptStory ID: %d, IP: %s",
                soptStoryId,
                ip
        ));
    }
}