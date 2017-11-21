package com.minivision.cameraplat.config;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@EnableOAuth2Client
@Configuration
public class OAuth2ClientConfig {
  @Value("${faceservice.oauth.apiKey}")
  private String apiKey;
  @Value("${faceservice.oauth.apiSecret}")
  private String apiSecret;
  @Value("${faceservice.oauth.authorize:${faceservice.bathPath}/oauth/authorize}")
  private String authorizeUrl;
  @Value("${faceservice.oauth.token:${faceservice.bathPath}/oauth/token}")
  private String tokenUrl;
  @Value("${faceservice.oauth.username}")
  private String username;
  @Value("${faceservice.oauth.password}")
  private String password;
  @Value("${faceservice.maxConcurrent:32}")
  private int maxConcurrent;
  
  @Bean
  protected OAuth2ProtectedResourceDetails resource() {

      ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();

      List<String> scopes = new ArrayList<>();
      scopes.add("face");
      scopes.add("faceset");
      resource.setAccessTokenUri(tokenUrl);
      resource.setClientId(apiKey);
      resource.setClientSecret(apiSecret);
      resource.setGrantType("password");
      resource.setScope(scopes);
      resource.setUsername(username);
      resource.setPassword(password);

      return resource;
  }

  @Bean
  public OAuth2RestTemplate restTemplate() throws Exception {
      AccessTokenRequest atr = new DefaultAccessTokenRequest();

      OAuth2RestTemplate template = new OAuth2RestTemplate(resource(), new DefaultOAuth2ClientContext(atr));
      
      template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient()));
      
      return template;
  }
  
  private HttpClient httpClient() throws Exception {
    SSLContext sslcontext =
        SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
    SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
        sslcontext, new String[] {"TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);

    Registry<ConnectionSocketFactory> socketFactoryRegistry =
        RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", sslConnectionSocketFactory).build();

    PoolingHttpClientConnectionManager cm =
        new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    cm.setMaxTotal(maxConcurrent);
    cm.setDefaultMaxPerRoute(maxConcurrent);// 单路由最大并发数

    return HttpClients.custom().setConnectionManager(cm).build();
  }
}
