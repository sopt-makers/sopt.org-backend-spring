package sopt.org.homepage.review.exception;

/**
 * 유효하지 않은 리뷰 카테고리 예외
 */
public class InvalidReviewCategoryException extends RuntimeException {
    public InvalidReviewCategoryException(String message) {
        super(message);
    }
}