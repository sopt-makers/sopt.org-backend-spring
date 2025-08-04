package sopt.org.homepage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 추가: 파일 크기 초과 에러 처리
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<String> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
		log.error("File upload size exceeded: {}", ex.getMessage());
		return ResponseEntity
				.status(HttpStatus.PAYLOAD_TOO_LARGE)
				.body("파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.");
	}

	@ExceptionHandler(BusinessLogicException.class)
	public ResponseEntity<String> businessLogicException(BusinessLogicException ex) {
		log.error(ex.getMessage());
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error(ex.getMessage());
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ex.getMessage());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<String> entityNotfoundException(EntityNotFoundException ex) {
		log.error(ex.getMessage());
		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(ex.getMessage());
	}

	@ExceptionHandler(ClientBadRequestException.class)
	public ResponseEntity<String> clientBadRequestException(ClientBadRequestException ex) {
		log.error(ex.getMessage());
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ex.getMessage());
	}

	@ExceptionHandler(ForbiddenClientException.class)
	public ResponseEntity<String> forbiddenClientException(ForbiddenClientException ex) {
		log.error(ex.getMessage());
		return ResponseEntity
			.status(HttpStatus.FORBIDDEN)
			.body(ex.getMessage());
	}

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<String> feignClientException(FeignException ex) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(ex.getMessage());
	}

	@ExceptionHandler(TokenException.class)
	public ResponseEntity<String> tokenException(TokenException ex) {
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ex.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> unknownException(RuntimeException ex) {
		log.error("[Unknown Error] : " + ex.getMessage());
		ex.printStackTrace();
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ex.getMessage());
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
		log.error(ex.getMessage());
		return ResponseEntity
			.status(ex.getStatusCode())
			.body(ex.getReason() != null ? ex.getReason() : "Unknown error occurred");
	}

	@ExceptionHandler({HttpMessageNotReadableException.class})
	public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		if (ex.getMessage() != null && ex.getMessage().contains("ClientBadRequestException")) {
			String errorMessage = ex.getMessage();

			int start =
				errorMessage.indexOf("[ClientBadRequestException] : ") + "[ClientBadRequestException] : ".length();
			int end = errorMessage.length();
			String clientErrorMessage = errorMessage.substring(start, end);

			return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(clientErrorMessage);
		}

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body("Invalid request format: " + ex.getMessage());
	}
}
