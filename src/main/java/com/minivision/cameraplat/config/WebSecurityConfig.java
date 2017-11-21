package com.minivision.cameraplat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.sql.DataSource;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private DataSource dataSource;
  
  @Autowired
  private StandardPasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationSuccessHandler authenticationSuccessHandler;

  @Autowired
  private LogoutSuccessHandler logoutSuccessHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable()
          .rememberMe().key("camera")
              .and().authorizeRequests()
              .antMatchers("/assets/**",
                  "/login", "/test/**","/api/v1/**","/webjars/**","/swagger-ui.html","/swagger-resources","/v2/api-docs","/c/**","/client","/snapshot/**","/people/**","/client.exe","/pingan.apk","/registry").permitAll()
              .antMatchers(HttpMethod.GET,"/user/initUser").authenticated()
              .antMatchers(HttpMethod.POST,"/user/initUser").permitAll()
              .antMatchers("/user").fullyAuthenticated()
              .and().authorizeRequests()
              .anyRequest().hasRole("USER")
              .and().formLogin().loginPage("/login").failureUrl("/login?error").permitAll().successHandler(authenticationSuccessHandler)
    //.defaultSuccessUrl("/faceset")
              .and().logout().permitAll()
              .and().logout().logoutSuccessHandler(logoutSuccessHandler);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
      //TODO auth.userDetailsService()?
      auth.inMemoryAuthentication().withUser("hello").password("hello").roles("USER","ADMIN");
      auth.jdbcAuthentication().passwordEncoder(passwordEncoder).dataSource(this.dataSource)
          .usersByUsernameQuery("select username, password, enabled from users where username = ?")
          .authoritiesByUsernameQuery("select u.username,r.role from users u,user_role ur,roles r  " +
            "where  u.id = ur.user_id " +
            "and ur.role_id = r.id  " +
            "and u.username = ? ");
          //.withUser("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN");
  }
  
  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() 
    throws Exception {
      return super.authenticationManagerBean();
  }
  
/*  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable()
          .rememberMe().key("camera")
              .and().authorizeRequests()
              .antMatchers("/assets/**", "/login", "/oauth/**", "/api/v1/**").permitAll()          //TODO 静态资源过滤
              .antMatchers("/admin/**").hasRole("ADMIN")
              .and().authorizeRequests()
              .anyRequest().hasRole("USER")
              .and().formLogin().loginPage("/login").failureUrl("/login?error").permitAll()
              .and().logout().permitAll();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
      //TODO auth.userDetailsService()?
    
      auth.jdbcAuthentication().passwordEncoder(passwordEncoder)
          .dataSource(this.dataSource);
          //.withUser("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN");
  }*/
  
  @Bean
  public StandardPasswordEncoder passwordEncoder(){
    return new StandardPasswordEncoder("test");
  }

}
