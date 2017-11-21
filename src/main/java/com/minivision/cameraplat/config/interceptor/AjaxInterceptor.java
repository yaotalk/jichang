package com.minivision.cameraplat.config.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class AjaxInterceptor extends HandlerInterceptorAdapter {
  
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    if(modelAndView!=null && isPjaxRequest(request)){
      if(!modelAndView.getViewName().startsWith("login")){
        modelAndView.setViewName(modelAndView.getViewName()+" :: content");
      }
    }
    
    super.postHandle(request, response, handler, modelAndView);
  }
  
  
  @SuppressWarnings("unused")
  private boolean isAjaxRequest(HttpServletRequest request){
    return (request.getHeader("x-requested-with") != null) && (request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest"));
  }
  
  private boolean isPjaxRequest(HttpServletRequest request){
    return request.getHeader("x-pjax") != null;
  }
}
