package com.sashutosh.dbpertenantwithliquibase.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.sashutosh.dbpertenantwithliquibase.db.DbInitializer;
import com.sashutosh.dbpertenantwithliquibase.db.PostgresqlTestContainer;
import com.sashutosh.dbpertenantwithliquibase.tenant.config.TenantContext;
import com.sashutosh.dbpertenantwithliquibase.tenant.domain.entity.Product;
import com.sashutosh.dbpertenantwithliquibase.tenant.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@DBRider(dataSourceBeanName = "masterDataSource")
@Tag("integration")
@ActiveProfiles("integration-test")
public class ProductRepositoryTest {

    @Container
    private static final PostgresqlTestContainer POSTGRESQL_CONTAINER = PostgresqlTestContainer.getInstance();

    @Autowired
    private DbInitializer databaseInitializer;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    @DataSet(value = {"tenants.yml"})
    public void initialize() throws Exception {
        databaseInitializer.ensureInitialized();
    }

    @Test
    public void findByIdForTenant1() {
        TenantContext.setTenantId("tenant1");
        Optional<Product> product = productRepository.findById(1L);
        assertThat(product).isPresent();
        assertThat(product.get().getName()).isEqualTo("Product 1");
        TenantContext.clear();
    }


}
