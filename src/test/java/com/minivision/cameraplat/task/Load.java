package com.minivision.cameraplat.task;

import  com.minivision.cameraplat.App;
import com.minivision.cameraplat.rest.AlarmApi;
import com.minivision.cameraplat.rest.FacesetApi;
import com.minivision.cameraplat.service.CameraService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=App.class)
public class Load {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AlarmApi alarmApi;

    @Autowired
    private FacesetApi facesetApi;

    @Test
    public void aop() throws MalformedURLException {

        URL url = new URL("file:D:\\jarexample\\out\\artifacts\\jarexample_jar\\jarexample.jar");
        URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method add = ReflectionUtils.findMethod(URLClassLoader.class, "addURL", URL.class);
            ReflectionUtils.makeAccessible(add);
            ReflectionUtils.invokeMethod(add,loader,url);
        try {
            try {
                MethodBeforeAdvice advice = (MethodBeforeAdvice) ClassLoader.getSystemClassLoader().loadClass("Main").newInstance();
                for(String beanName : context.getBeanNamesForAnnotation(RestController.class)){
                    Object object  = context.getBean(beanName);
                    Advised object1 = (Advised) object;
                    object1.addAdvice(advice);
                }
                System.out.println(facesetApi.toString());
                System.out.println(alarmApi.toString());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

@Test
public void  log(){
    String a = "abc";
//   PrintStream printStream = new PrintStream(System.out){
//       @Override public void print(String s) {
//           super.print(s+"xx");
//       }
//   };
 //   System.setOut(printStream);
    System.out.println(a);
}
}
