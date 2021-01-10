package com.sashutosh.dbpertenantwithliquibase.tenant.exception;

public class InvalidTenantIdException extends RuntimeException {
    public InvalidTenantIdException(String s) {
        super(s);
    }
}
