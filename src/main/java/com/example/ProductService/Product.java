package com.example.ProductService;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "products")
@Data
public class Product {
    @Id
    private Long id;
    private String name;
    private Double price;
}