package com.anton.tsarenko.shortener.exceptions;

import com.anton.tsarenko.shortener.auth.dto.ExceptionResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for API exceptions.
 */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final String REASON_DELIMITER = ": ";
    private static final String MULTIPLE_ERRORS_DELIMITER = ", ";

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_MAPPING = Map.of(
            HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE
    );

    /**
     * Handler for HTTP status code exceptions.
     *
     * @param exception {@link HttpStatusCodeException} thrown by service
     * @return {@link ResponseEntity} with status, header and body
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<ExceptionResponse> handleBindException(
            HttpStatusCodeException exception
    ) {
        return map((HttpStatus) exception.getStatusCode(), exception.getStatusText(), exception);
    }

    /**
     * Binding results for Request Body exception handler.
     *
     * @param exception {@link MethodArgumentNotValidException} to catch and extract error messages
     *                                                         from fields
     * @return {@link ResponseEntity} with status {@link HttpStatus#BAD_REQUEST},
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<ExceptionResponse> handleBindException(
            MethodArgumentNotValidException exception
    ) {
        return map(
                HttpStatus.BAD_REQUEST,
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError ->
                                fieldError.getField()
                                        + Optional.ofNullable(fieldError.getDefaultMessage())
                                                .map(REASON_DELIMITER::concat)
                                                .orElse("")
                        )
                        .distinct()
                        .sorted(),
                exception
        );
    }

    /**
     * Exception handler for empty results in storage.
     *
     * @param exception {@link EmptyResultDataAccessException} to catch and extract error messages
     *                                                        from fields
     * @return {@link ResponseEntity} with status {@link HttpStatus#NOT_FOUND},
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<ExceptionResponse> handleBindException(
            EmptyResultDataAccessException exception
    ) {
        return map(
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.name(),
                exception
        );
    }

    /**
     * Exception handler for empty results in storage.
     *
     * @param exception {@link UserAlreadyExistsException} to catch and extract error messages
     *                                                        from fields
     * @return {@link ResponseEntity} with status {@link HttpStatus#NOT_FOUND},
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<ExceptionResponse> handleBindException(
            UserAlreadyExistsException exception
    ) {
        return map(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                exception
        );
    }

    /**
     * Exception handler for empty results in storage.
     *
     * @param exception {@link BadCredentialsException} to catch and extract error messages
     *                                                        from fields
     * @return {@link ResponseEntity} with status {@link HttpStatus#NOT_FOUND},
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<ExceptionResponse> handleBindException(
            BadCredentialsException exception
    ) {
        return map(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                exception
        );
    }

    /**
     * Method argument mismatch exception handler.
     *
     * @param exception {@link MethodArgumentTypeMismatchException} to catch and extract
     *                                                             meaningful response
     * @return {@link ResponseEntity} with status {@link HttpStatus#BAD_REQUEST}
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<ExceptionResponse> handleBindException(
            MethodArgumentTypeMismatchException exception
    ) {
        return map(
                HttpStatus.BAD_REQUEST,
                Stream.concat(
                        Stream.of(exception.getName() + REASON_DELIMITER + "provided wrong type"),
                        Stream.ofNullable(exception.getRequiredType())
                                .filter(Objects::nonNull)
                                .map(Class::getSimpleName)
                                .map("expected type is "::concat)
                ),
                exception
        );
    }


    /**
     * Not readable request data exception handler.
     *
     * @param exception {@link HttpMessageNotReadableException} to catch and extract
     *                                                         meaningful response
     * @return {@link ResponseEntity} with status {@link HttpStatus#BAD_REQUEST},
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<ExceptionResponse> handleBindException(
            HttpMessageNotReadableException exception
    ) {
        return map(HttpStatus.BAD_REQUEST, "Invalid request body received", exception);
    }

    /**
     * Global exception Handler.
     *
     * @param exception - exception
     * @return {@link ResponseEntity} with status and body
     */
    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<ExceptionResponse> handleThrowable(Throwable exception) {
        return map(
                EXCEPTION_MAPPING.getOrDefault(
                        exception.getClass(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                ),
                exception.getMessage(),
                exception
        );
    }

    /**
     * Converts specific exceptions to meaningful response.
     *
     * @param httpStatus generic error code
     * @param messages   stream of messages
     * @param throwable  cause
     * @return {@link ResponseEntity} with status, header and body
     */
    private ResponseEntity<ExceptionResponse> map(HttpStatus httpStatus,
                                                              Stream<String> messages,
                                                              Throwable throwable) {
        return map(
                httpStatus,
                messages.collect(Collectors.joining(MULTIPLE_ERRORS_DELIMITER)),
                throwable);
    }

    /**
     * Converts specific exceptions to meaningful response.
     *
     * @param httpStatus generic error code
     * @param message    descriptive message of an error
     * @param throwable  cause
     * @return {@link ResponseEntity} with status, header and body
     */
    private ResponseEntity<ExceptionResponse> map(
            HttpStatus httpStatus,
            String message,
            Throwable throwable) {
        var reasonPhrase = httpStatus.getReasonPhrase();
        log.error("Error: {}, Message: {}, Cause: {}", reasonPhrase, message, throwable.toString());
        return ResponseEntity.status(httpStatus)
                .body(ExceptionResponse.builder()
                        .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                        .error(reasonPhrase)
                        .message(message)
                        .build());
    }
}
