package com.sashutosh.dbpertenantwithliquibase.repository;

import com.sashutosh.dbpertenantwithliquibase.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {
}
