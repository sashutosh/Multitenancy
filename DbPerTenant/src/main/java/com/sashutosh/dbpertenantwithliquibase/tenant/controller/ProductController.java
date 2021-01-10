package com.sashutosh.dbpertenantwithliquibase.tenant.controller;

import com.sashutosh.dbpertenantwithliquibase.tenant.service.ProductService;
import com.sashutosh.dbpertenantwithliquibase.tenant.service.ProductValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/")
public class ProductController extends BaseController {
    final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "/products", produces = {ContentType.PRODUCTS_1_0})
    public ResponseEntity<List<ProductValue>> getProducts() {
        List<ProductValue> productValues = productService.getProducts();
        return new ResponseEntity<>(productValues, HttpStatus.OK);
    }

    @GetMapping(value = "/products/{productId}", produces = {ContentType.PRODUCT_1_0})
    public ResponseEntity<ProductValue> getProduct(@PathVariable("productId") long productId) {
        try {
            ProductValue branch = productService.getProduct(productId);
            return new ResponseEntity<>(branch, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    @PostMapping(value = "/products",
            consumes = {ContentType.PRODUCT_1_0},
            produces = {ContentType.PRODUCT_1_0})
    public ResponseEntity<ProductValue> createProduct(@Valid @RequestBody ProductValue productValue) {
        ProductValue product = productService.createProduct(productValue);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.LOCATION, "/products/" + product.getProductId());
        return new ResponseEntity<>(product, headers, HttpStatus.CREATED);
    }


}
