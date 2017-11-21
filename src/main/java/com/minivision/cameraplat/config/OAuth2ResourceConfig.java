package com.minivision.cameraplat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {
  
  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/api/v2/**")
    .authorizeRequests()
    .anyRequest().authenticated()
    .and()
    .httpBasic();
  }
}
