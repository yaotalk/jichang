package com.minivision.cameraplat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthServerConfig extends AuthorizationServerConfigurerAdapter {
  
  @Value("${security.oauth2.client.client-id}")
  private String clientId;
  @Value("${security.oauth2.client.client-secret}")
  private String clientSecret;
  
  @Autowired
  @Qualifier("authenticationManagerBean")
  private AuthenticationManager authenticationManager;
  
  @Autowired
  private ClientDetailsService clientDetailsService;

  @Override
  public void configure(
    AuthorizationServerSecurityConfigurer oauthServer) 
    throws Exception {
      oauthServer
        .tokenKeyAccess("permitAll()")
        .checkTokenAccess("isAuthenticated()");
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) 
    throws Exception {
      clients.inMemory().withClient(clientId)
      .secret(clientSecret)
      .authorizedGrantTypes("password")
      .scopes("read");
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) 
    throws Exception {
    endpoints.authenticationManager(authenticationManager);
  }
  
  @Bean
  public TokenStore tokenStore(){
    return new InMemoryTokenStore();
  }
  
  @Bean
  @Autowired
  public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
      TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
      handler.setTokenStore(tokenStore);
      handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
      handler.setClientDetailsService(clientDetailsService);
      return handler;
  }

  @Bean
  @Autowired
  public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
      TokenApprovalStore store = new TokenApprovalStore();
      store.setTokenStore(tokenStore);
      return store;
  }
}
