package com.example.ProductService;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("products")  // ✅ Removed `name = "products"` since it's not needed
@Data
@NoArgsConstructor  // ✅ Fix for JSON deserialization issues
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // ✅ Prevents `null` fields from appearing in JSON
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price; // ✅ Changed from `Double` to `BigDecimal` for accuracy
    private Integer stockQuantity;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Transient
    private String error;

    public Product(String error) {
        this.error = error;
    }
}
