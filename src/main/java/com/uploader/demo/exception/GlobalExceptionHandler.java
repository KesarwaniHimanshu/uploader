package com.uploader.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Bean validation errors (@NotNull, @NotBlank, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Request validation failed",
                errors
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    // ✅ Illegal argument (wrong enum value etc.)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request",
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    // ✅ All uncaught exceptions
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
//
//        ApiError apiError = new ApiError(
//                LocalDateTime.now(),
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Internal Server Error",
//                ex.getMessage(),
//                null
//        );
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAll(Exception ex) {

        ex.printStackTrace(); // 🔥 critical for debugging

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail(ex.getMessage());
        problem.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problem);
    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
//        ex.printStackTrace(); // VERY IMPORTANT in dev
//        return ResponseEntity.status(500)
//                .body(new ErrorResponse("Internal error", ex.getMessage()));
//    }
}
