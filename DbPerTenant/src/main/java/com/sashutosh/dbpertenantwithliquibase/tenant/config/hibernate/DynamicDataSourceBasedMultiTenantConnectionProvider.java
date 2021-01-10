package com.sashutosh.dbpertenantwithliquibase.tenant.config.hibernate;

import com.sashutosh.dbpertenantwithliquibase.domain.entity.Tenant;
import com.sashutosh.dbpertenantwithliquibase.repository.TenantRepository;
import com.sashutosh.dbpertenantwithliquibase.tenant.exception.InvalidTenantIdException;
import com.sashutosh.dbpertenantwithliquibase.util.EncryptionService;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

@Component
public class DynamicDataSourceBasedMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final String TENANT_POOL_NAME_SUFFIX = "Tenant_Pool";
    @Autowired
    TenantRepository tenantRepository;
    @Autowired
    EncryptionService encryptionService;
    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;
    @Value("${encryption.secret}")
    private String secret;
    @Value("${encryption.salt}")
    private String salt;
    @Autowired
    @Qualifier("masterDataSourceProperties")
    private DataSourceProperties dataSourceProperties;

    @Override
    protected DataSource selectAnyDataSource() {
        return masterDataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantId) {
        //1. Get the tenantId details from the repo
        Optional<Tenant> currentTenant = tenantRepository.findById(tenantId);
        if (currentTenant.isPresent()) {
            //2. Create DataSource based for the tenant
            return createTenantDataSource(currentTenant.get());
        } else {
            throw new InvalidTenantIdException("Unable to resolve tenant with id" + tenantId);
        }
    }

    private DataSource createTenantDataSource(Tenant tenant) {
        String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        ds.setUsername(tenant.getDb());
        ds.setPassword(decryptedPassword);
        ds.setJdbcUrl(tenant.getUrl());

        ds.setPoolName(tenant.getTenantId() + TENANT_POOL_NAME_SUFFIX);

        //log.info("Configured datasource: {}", ds.getPoolName());
        return ds;
    }
}
