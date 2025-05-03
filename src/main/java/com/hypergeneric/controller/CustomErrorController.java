package com.hypergeneric.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String uri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                logger.error("404 ERROR - Path not found: {}", uri);
                logger.debug("Request details: Method={}, Query={}, RemoteAddr={}", 
                        request.getMethod(),
                        request.getQueryString(),
                        request.getRemoteAddr());
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                logger.error("500 ERROR - Server error for path: {}", uri);
            }
            
            logger.info("Error status code: {} for URI: {}", statusCode, uri);
        }
        
        return "error_view";
    }
}