package sopt.org.homepage.soptstory.exception;

/**
 * 이미 좋아요를 누른 상태에서 다시 좋아요를 시도할 때 발생하는 예외
 */
public class AlreadyLikedException extends RuntimeException {

    public AlreadyLikedException(Long soptStoryId, String ip) {
        super(String.format(
                "이미 좋아요를 누른 상태입니다. SoptStory ID: %d, IP: %s",
                soptStoryId,
                ip
        ));
    }
}
