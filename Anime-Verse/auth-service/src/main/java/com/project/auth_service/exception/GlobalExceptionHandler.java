package com.project.auth_service.exception;

import com.project.auth_service.exception.customException.UserAlreadyExistsException;
import com.project.auth_service.exception.customException.UsernameOrEmailAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ApiError>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception){
        List<ApiError> errors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                {
                    ApiError errorResponse = ApiError.builder()
                            .keyError(error.getField())
                            .valueError(error.getDefaultMessage())
                            .timeStamp(LocalDateTime.now())
                            .build();
                    errors.add(errorResponse);
                }
        );

        return new ResponseEntity<>(errors , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameOrEmailAlreadyExistsException.class)
    public ResponseEntity<List<ApiError>> handleUserAlreadyExists(UsernameOrEmailAlreadyExistsException exception){
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Username or Email already Exists")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<List<ApiError>> handleAccessDenied(AccessDeniedException exception){
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Username or Email already Exists")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error , HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<List<ApiError>> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        Throwable rootCause = exception.getRootCause();
        String message = rootCause != null ? rootCause.getMessage() : exception.getMessage();
        if(message.contains("email")){
            message = "email";
        } else if (message.contains("username")) {
            message = "username";
        }

        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(message)
                        .valueError("Already exists")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
