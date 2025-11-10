package sopt.org.homepage.review.exception;

/**
 * 유효하지 않은 리뷰 세부 주제 예외
 */
public class InvalidReviewSubjectException extends RuntimeException {
    public InvalidReviewSubjectException(String message) {
        super(message);
    }
}