package com.minivision.cameraplat.config;

import javax.annotation.PostConstruct;

import com.minivision.cameraplat.config.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.minivision.cameraplat.config.interceptor.AjaxInterceptor;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.mediaType("json", MediaType.APPLICATION_JSON);
    configurer.mediaType("text", MediaType.APPLICATION_JSON);
    configurer.mediaType("xml", MediaType.APPLICATION_XML);
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
    configurer.ignoreAcceptHeader(false);
  }

  @PostConstruct
  public void init() {
    templateEngine.addDialect(new SpringSecurityDialect());
  }

  @Autowired
  private SpringTemplateEngine templateEngine;

  @Autowired
  private ThymeleafViewResolver thymeleafViewResolver;

  @Autowired
  private AjaxInterceptor ajaxInterceptor;

  @Autowired
  private LoginInterceptor loginInterceptor;

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.enableContentNegotiation(new MappingJackson2JsonView());
    registry.viewResolver(thymeleafViewResolver);
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("login/login");
    registry.addViewController("/").setViewName("redirect:/faceset");
    registry.addViewController("/registry").setViewName("login/registry");
    super.addViewControllers(registry);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

      registry.addInterceptor(loginInterceptor).addPathPatterns("/*")
      .excludePathPatterns("/api/v1*").excludePathPatterns("/swagger*")
      .excludePathPatterns("/v2*");
      registry.addInterceptor(ajaxInterceptor);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**").allowedOrigins("*");
    super.addCorsMappings(registry);
  }

}
