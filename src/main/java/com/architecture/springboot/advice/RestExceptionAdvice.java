package com.architecture.springboot.advice;

import com.architecture.springboot.response.ApiResponse;
import com.architecture.springboot.response.ResMessage;
import com.architecture.springboot.response.ResponseError;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.SizeLimitExceededException;

@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionAdvice extends ResponseEntityExceptionHandler {
    private final Environment environment;

    @ExceptionHandler({SizeLimitExceededException.class, MaxUploadSizeExceededException.class})
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ApiResponse<ResMessage> handleSizeLimitExceededException(Exception e) {
        log.error("File Size Limit Over : {}", e.getLocalizedMessage());
        return ApiResponse.fail(new ResponseError("F01", "업로드 한 파일 용량이 너무 큽니다. - " + e.getMessage()));
    }
}
