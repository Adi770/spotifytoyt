package pl.lepa.spotifytoyt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.lepa.spotifytoyt.exceptions.UrlException;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UrlException.class)
    public String wrongUrl(Model model){
        return "error/error400";
    }
}
