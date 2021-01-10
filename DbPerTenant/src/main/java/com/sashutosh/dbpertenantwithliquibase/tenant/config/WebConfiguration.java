package com.sashutosh.dbpertenantwithliquibase.tenant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final WebRequestInterceptor webRequestInterceptor;

    @Autowired
    public WebConfiguration(WebRequestInterceptor webRequestInterceptor) {
        this.webRequestInterceptor = webRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(webRequestInterceptor);
    }

}
