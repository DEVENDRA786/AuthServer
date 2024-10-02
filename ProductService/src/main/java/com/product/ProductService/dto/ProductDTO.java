package com.product.ProductService.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDTO implements Serializable {
    private Long id;
    private String name;
    private String price;
    private String description;
}
