package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;


@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    // ResponseStatus 404 must be specified, else returns status 200 because method is more granular level than this annotation on the custom exception class
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception e) {
        log.error(getExceptionLogMessage(e));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", e.getMessage());
        modelAndView.setViewName("errors/error404");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(HttpServletRequest request, Exception e) {
        log.error(getExceptionLogMessage(e) + ", from Request: " + getRequestInfo(request));
        return "errors/error";
    }

    private String getExceptionLogMessage(Exception e) {
        return String.format("Handling %s: %s", e.getClass().getSimpleName(), e.getMessage());
    }

    private String getRequestInfo(HttpServletRequest request) {
        return request.getRequestURI() + (request.getQueryString() != null ? request.getQueryString() : "");
    }
}
