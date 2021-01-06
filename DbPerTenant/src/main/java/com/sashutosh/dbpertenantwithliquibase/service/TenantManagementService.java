package com.sashutosh.dbpertenantwithliquibase.service;

public interface TenantManagementService {
    void createTenant(String tenantId, String db, String password) throws TenantCreationException;
}
