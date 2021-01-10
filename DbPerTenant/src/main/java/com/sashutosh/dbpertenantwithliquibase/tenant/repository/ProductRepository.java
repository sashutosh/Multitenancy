package com.sashutosh.dbpertenantwithliquibase.tenant.repository;

import com.sashutosh.dbpertenantwithliquibase.tenant.domain.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
