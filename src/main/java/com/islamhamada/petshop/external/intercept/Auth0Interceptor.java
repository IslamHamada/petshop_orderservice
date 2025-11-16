package com.islamhamada.petshop.external.intercept;

import com.islamhamada.petshop.service.TokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Auth0Interceptor implements RequestInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = tokenService.extractToken();

        if(token != null){
            requestTemplate.header("Authorization", "Bearer " + token);
        }
    }
}
