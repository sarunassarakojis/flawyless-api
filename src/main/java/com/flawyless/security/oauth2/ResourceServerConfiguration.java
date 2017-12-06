package com.flawyless.security.oauth2;

import com.flawyless.controller.ControllerConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final String FLAWYLESS_RESOURCE_ID = "flawyless_api";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(FLAWYLESS_RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers(ControllerConstants.CARD_API_URL + "/**",
                ControllerConstants.USER_API_URL + "/**")
                .and()
                .authorizeRequests().antMatchers(ControllerConstants.CARD_API_URL + "/**",
                ControllerConstants.USER_API_URL + "/**")
                .access("hasRole('ROLE_USER')")
                .and()
                .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
