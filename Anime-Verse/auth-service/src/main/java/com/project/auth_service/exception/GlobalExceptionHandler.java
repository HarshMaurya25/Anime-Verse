package com.project.auth_service.exception;

import com.project.auth_service.exception.customException.*;
import io.jsonwebtoken.JwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<List<ApiError>> handleAuthenticationException(AuthenticationException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Authenication Failed")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<List<ApiError>> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Authentication Credentials Not Found")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<List<ApiError>> handleJwtException(JwtException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Invalid JWT token")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<List<ApiError>> handleBadCredentialsException(BadCredentialsException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Bad Credential")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<List<ApiError>> handleDisabledException(DisabledException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Disable User")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error , HttpStatus.UNAUTHORIZED);
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<List<ApiError>> handleGenericException(RuntimeException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Error Occured")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExpireOrWrongRefreshTokenException.class)
    public ResponseEntity<List<ApiError>> handleExpireOrWrongRefreshTokenException(ExpireOrWrongRefreshTokenException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Expire O rWrong Refresh Token")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<List<ApiError>> handleUserNotFoundException(UserNotFoundException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("User Not Found")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<List<ApiError>> handleVerificationCodeExpiredException(VerificationCodeExpiredException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Verification Code Expired")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<List<ApiError>> handleInvalidVerificationCodeException(InvalidVerificationCodeException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Invalid Verification Code")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadySendException.class)
    public ResponseEntity<List<ApiError>> handleEmailAlreadySendException(EmailAlreadySendException exception) {
        List<ApiError> error = List.of(
                ApiError.builder()
                        .keyError(exception.getMessage())
                        .valueError("Email Already Send")
                        .timeStamp(LocalDateTime.now())
                        .build()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


}
