package com.sashutosh.dbpertenantwithliquibase.tenant.config;

public class TenantContext {

    private static final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

    public static String getTenantId() {
        return currentTenant.get();
    }

    public static void setTenantId(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static void clear() {
        currentTenant.remove();
    }
}
