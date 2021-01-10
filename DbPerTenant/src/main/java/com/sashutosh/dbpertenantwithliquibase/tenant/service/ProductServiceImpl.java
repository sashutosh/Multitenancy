package com.sashutosh.dbpertenantwithliquibase.tenant.service;

import com.sashutosh.dbpertenantwithliquibase.tenant.domain.entity.Product;
import com.sashutosh.dbpertenantwithliquibase.tenant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Service
public class ProductServiceImpl implements ProductService {

    final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public List<ProductValue> getProducts() {
        Iterable<Product> products = productRepository.findAll();
        return StreamSupport.stream(products.spliterator(), false)
                .map(ProductValue::fromEntity)
                .collect(toList());
    }

    @Override
    @Transactional
    public ProductValue getProduct(long productId) {
        return ProductValue.fromEntity(productRepository.findById(productId).
                orElseThrow(() -> new EntityNotFoundException("Product not found id = " + productId)));
    }

    @Override
    @Transactional
    public ProductValue createProduct(ProductValue productValue) {
        return ProductValue.fromEntity(
                productRepository.save(ProductValue.fromValue(productValue)));
    }

    @Override
    @Transactional
    public ProductValue updateProduct(ProductValue productValue) {
        Product product = productRepository.findById(productValue.getProductId()).
                orElseThrow(() -> new EntityNotFoundException("Product not found id = " + productValue.getProductId()));
        product.setName(productValue.getName());
        product.setVersion(product.getVersion() + 1);
        return ProductValue.fromEntity(product);
    }

    @Override
    @Transactional
    public void deleteProductById(long productId) {
        productRepository.findById(productId).
                orElseThrow(() -> new EntityNotFoundException("Product not found id = " + productId));
        productRepository.deleteById(productId);

    }
}
