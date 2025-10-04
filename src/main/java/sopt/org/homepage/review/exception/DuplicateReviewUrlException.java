package sopt.org.homepage.review.exception;

/**
 * 중복된 리뷰 URL 예외
 */
public class DuplicateReviewUrlException extends RuntimeException {
    public DuplicateReviewUrlException(String message) {
        super(message);
    }
}