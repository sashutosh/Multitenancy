package com.sashutosh.dbpertenantwithliquibase.tenant.interceptor;

import com.sashutosh.dbpertenantwithliquibase.tenant.config.TenantContext;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Component
public class TenantRequestInterceptor implements WebRequestInterceptor {
    @Override
    public void preHandle(WebRequest webRequest) throws Exception {
        String tenantId;
        if (webRequest.getHeader("X-TENANT-ID") != null) {
            tenantId = webRequest.getHeader("X-TENANT-ID");
        } else {
            tenantId = ((ServletWebRequest) webRequest).getRequest().getServerName().split("\\.")[0];
        }
        TenantContext.setTenantId(tenantId);
    }

    @Override
    public void postHandle(WebRequest webRequest, ModelMap modelMap) throws Exception {
        TenantContext.clear();
    }

    @Override
    public void afterCompletion(WebRequest webRequest, Exception e) throws Exception {

    }
}
