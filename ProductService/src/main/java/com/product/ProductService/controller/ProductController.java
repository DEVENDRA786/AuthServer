package com.product.ProductService.controller;

import com.product.ProductService.dto.ProductDTO;
import com.product.ProductService.entity.Product;
import com.product.ProductService.serviceImpl.ProductService;
import com.product.ProductService.utility.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @PostMapping("/save")
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productService.addProduct(product);
        return productMapper.toDTO(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product productDetails = productMapper.toEntity(productDTO);
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
    }

    @GetMapping("/getAllProduct")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts().stream().map(productMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> ResponseEntity.ok(productMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

