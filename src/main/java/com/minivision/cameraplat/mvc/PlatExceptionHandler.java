package com.minivision.cameraplat.mvc;

import com.minivision.cameraplat.faceplat.ex.FacePlatException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class PlatExceptionHandler {
  @ExceptionHandler(FacePlatException.class)
  @ResponseBody
  public String handleFacePlatException(HttpServletRequest request, FacePlatException ex) {
    //TODO JSON
      return "failure";
  }
}
