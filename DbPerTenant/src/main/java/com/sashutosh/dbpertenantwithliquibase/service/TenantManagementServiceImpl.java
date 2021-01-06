package com.sashutosh.dbpertenantwithliquibase.service;


import com.sashutosh.dbpertenantwithliquibase.domain.entity.Tenant;
import com.sashutosh.dbpertenantwithliquibase.repository.TenantRepository;
import com.sashutosh.dbpertenantwithliquibase.util.EncryptionService;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
@EnableConfigurationProperties(LiquibaseProperties.class)
public class TenantManagementServiceImpl implements TenantManagementService {

    private static final String VALID_DATABASE_NAME_REGEXP = "[A-Za-z0-9_]*";
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final LiquibaseProperties liquibaseProperties;
    private final ResourceLoader resourceLoader;
    private final TenantRepository tenantRepository;
    private final String urlPrefix;
    private final String salt;
    private final String secret;
    private final EncryptionService encryptionService;


    @Autowired
    public TenantManagementServiceImpl(DataSource dataSource,
                                       JdbcTemplate jdbcTemplate,
                                       @Qualifier("tenantLiquibaseProperties")
                                               LiquibaseProperties liquibaseProperties,
                                       ResourceLoader resourceLoader,
                                       TenantRepository tenantRepository,
                                       EncryptionService encryptionService,
                                       @Value("${multitenancy.tenant.datasource.url-prefix}")
                                               String urlPrefix,
                                       @Value("${encryption.secret}")
                                               String secret,
                                       @Value("${encryption.salt}")
                                               String salt) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.liquibaseProperties = liquibaseProperties;
        this.resourceLoader = resourceLoader;
        this.tenantRepository = tenantRepository;
        this.urlPrefix = urlPrefix;
        this.salt = salt;
        this.secret = secret;
        this.encryptionService = encryptionService;
    }

    @Override
    public void createTenant(String tenantId, String dbName, String password) throws TenantCreationException {

        if (!dbName.matches(VALID_DATABASE_NAME_REGEXP)) {
            throw new TenantCreationException("Invalid db name: " + dbName);
        }

        String dbUrl = urlPrefix + dbName;
        String encryptedPassword = encryptionService.encrypt(password, secret, salt);
        try {
            createDB(dbName, password);
        } catch (DataAccessException ex) {
            throw new TenantCreationException("Failed to create db " + dbName, ex);
        }
        try (Connection connection = DriverManager.getConnection(dbUrl, dbName, password)) {
            DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
            runLiquibase(tenantDataSource);

        } catch (SQLException | LiquibaseException throwable) {
            throw new TenantCreationException("Error while creating tables ", throwable);
        }

        Tenant tenant = new Tenant();
        tenant.setTenantId(tenantId);
        tenant.setDb(dbName);
        tenant.setUrl(dbUrl);
        tenant.setPassword(encryptedPassword);
        tenantRepository.save(tenant);
    }

    private void runLiquibase(DataSource tenantDataSource) throws LiquibaseException {
        SpringLiquibase liquibase = getSpringLiquiBase(tenantDataSource);
        liquibase.afterPropertiesSet();

    }

    private SpringLiquibase getSpringLiquiBase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        liquibase.setLabels(liquibaseProperties.getLabels());
        liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
        liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());

        return liquibase;
    }

    private void createDB(String dbName, String password) {
        jdbcTemplate.execute((StatementCallback<Object>) statement -> statement.execute("CREATE DATABASE " + dbName));
        jdbcTemplate.execute((StatementCallback<Object>) statement -> statement.execute("CREATE USER " + dbName + " WITH ENCRYPTED PASSWORD '" + password + "'"));
        jdbcTemplate.execute((StatementCallback<Object>) statement -> statement.execute("GRANT ALL PRIVILEGES ON DATABASE " + dbName + " TO " + dbName));
    }
}
