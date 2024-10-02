package com.product.ProductService.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String price;
    private String description;
}

