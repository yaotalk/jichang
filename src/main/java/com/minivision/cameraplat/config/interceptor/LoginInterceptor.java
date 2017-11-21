package com.minivision.cameraplat.config.interceptor;

import com.minivision.cameraplat.domain.User;
import com.minivision.cameraplat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserRepository userRepository;

    public LoginInterceptor() {
        super();
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        List<User> users = userRepository.findAll();
        if(users.size() == 0) {
            if(request.getRequestURI().endsWith("registry") || request.getRequestURI().endsWith("initUser")){
                return true;
            }
            response.sendRedirect("/registry");
            return true;
        }
        else if(request.getRequestURI().endsWith("registry")){
            response.sendRedirect("/login");
            return false;
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
