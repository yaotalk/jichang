package com.minivision.cameraplat.config;

import com.minivision.cameraplat.domain.SysLog;
import com.minivision.cameraplat.domain.User;
import com.minivision.cameraplat.repository.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.minivision.cameraplat.service.SysLogService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LogAspect {
  
  private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private UserRepository userRepository;

//    @After("within(@org.springframework.web.bind.annotation.RestController *)")
//    public void doLog(JoinPoint joinPoint) throws Throwable {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//
//
//        logger.info("URL : " + request.getRequestURL().toString());
//        logger.info("HTTP_METHOD : " + request.getMethod());
//        logger.info("IP : " + request.getRemoteAddr());
//        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
//        logger.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
//
//        //TODO save in DB
//    }

    @AfterReturning(value = "within(@org.springframework.web.bind.annotation.RestController *)",returning = "obj")
    public void dbLog(JoinPoint joinPoint,Object obj) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes!=null) {
            HttpServletRequest request = attributes.getRequest();
            logger.info("URL : " + request.getRequestURL().toString());
            logger.info("HTTP_METHOD : " + request.getMethod());
            logger.info("IP : " + request.getRemoteAddr());
            logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            logger.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
            String username;
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            User user = null;
            if (username != null) {
                user = userRepository.findByUsername(username);
            }
            try {
                if (obj != null && "success".equals(obj)) {
                    String target_name = joinPoint.getSignature().getName();
                    Class<?> target = joinPoint.getTarget().getClass();
                    Class<?> target_class = Class.forName(target.getName());
                    Method[] methods = target_class.getDeclaredMethods();
                    //Object[] args = joinPoint.getArgs();
                    for (Method method : methods) {
                        if (method.getName().equals(target_name)
                            && method.getAnnotation(OpAnnotation.class) != null) {
                            logger.error("annotation is " + method.getAnnotation(PostMapping.class));
                            String model_name = method.getAnnotation(OpAnnotation.class).modelName();
                            String operation = method.getAnnotation(OpAnnotation.class).opration();
                            String details;
                            if ("updateAnaCamera".equals(method.getName())) {
                                details =
                                    "{分析仪ID: " + joinPoint.getArgs()[0] + ",摄像机ID: " + joinPoint.getArgs()[1]
                                        + " }";
                            } else if ("bindwithEntrance".equals(method.getName())) {
                                details = "{摄像机ID: " + joinPoint.getArgs()[0] + ",门ID: " + joinPoint
                                    .getArgs()[1] + " }";
                            } else
                                details = (joinPoint.getArgs()[0]).toString();
                            SysLog sysLog =
                                new SysLog(user, request.getRemoteAddr(), model_name, operation,
                                    Calendar.getInstance().getTime(), details);
                            sysLogService.save(sysLog);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                SysLog sysLog = new SysLog(user, request.getRemoteAddr(), "", "",
                    Calendar.getInstance().getTime(), "日志异常" + e.getMessage());
                sysLogService.save(sysLog);
                logger.error(e.getMessage());
            }
        }
    }
}
