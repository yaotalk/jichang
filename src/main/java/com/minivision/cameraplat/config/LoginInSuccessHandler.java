package com.minivision.cameraplat.config;

import com.minivision.cameraplat.domain.SysLog;
import com.minivision.cameraplat.domain.User;
import com.minivision.cameraplat.repository.UserRepository;
import com.minivision.cameraplat.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

@Component
public class LoginInSuccessHandler implements AuthenticationSuccessHandler
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SysLogService sysLogService;

    @Override public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, Authentication authentication)
        throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user =null;
        if (username != null){
            user = userRepository.findByUsername(username);
        }
        SysLog sysLog =
            new SysLog(user, httpServletRequest.getRemoteAddr(), "登录", "登录",
                Calendar.getInstance().getTime(),username);
        sysLogService.save(sysLog);
        httpServletResponse.sendRedirect("/faceset");
    }
}

@Component
class LoginOutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SysLogService sysLogService;

    @Override public void onLogoutSuccess(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, Authentication authentication)
        throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user =null;
        if (username != null){
            user = userRepository.findByUsername(username);
        }
        SysLog sysLog =
            new SysLog(user, httpServletRequest.getRemoteAddr(), "登录", "登出",
                Calendar.getInstance().getTime(),username);
        sysLogService.save(sysLog);
        httpServletResponse.sendRedirect("/login");
    }
}
