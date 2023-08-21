package com.architecture.springboot.advice;

import com.architecture.springboot.response.ApiResponse;
import com.architecture.springboot.response.ResMessage;
import com.architecture.springboot.response.ResponseError;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView handleDefaultException(Exception e) {
        log.error(e);
//        for(StackTraceElement traceElement : e.getStackTrace()) {
//            log.error(traceElement);
//        }
        return new ModelAndView("error/error");
    }
}
