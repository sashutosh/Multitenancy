package com.sashutosh.dbpertenantwithliquibase.controller;

import com.sashutosh.dbpertenantwithliquibase.service.TenantCreationException;
import com.sashutosh.dbpertenantwithliquibase.service.TenantManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class TenantsApiController {

    @Autowired
    TenantManagementService tenantManagementService;

    @PostMapping("/tenants")
    public ResponseEntity<Void> createTenant(@RequestParam String tenantId, @RequestParam String db, @RequestParam String password) {
        try {
            tenantManagementService.createTenant(tenantId, db, password);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (TenantCreationException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
