package com.sashutosh.dbpertenantwithliquibase.service;

public class TenantCreationException extends Throwable {
    public TenantCreationException(String message, Exception throwable) {
        super(message, throwable);
    }

    public TenantCreationException(String s) {
        super(s);
    }

    public TenantCreationException(Exception throwable) {
        super(throwable);
    }
}
