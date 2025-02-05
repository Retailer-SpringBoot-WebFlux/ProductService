package com.example.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Product>> createProduct(@RequestBody Product product) {
        logger.info("Received request to create product: {}", product);
        return service.saveProduct(product)
                .doOnSuccess(savedProduct -> logger.info("Successfully created product with ID: {}", savedProduct.getId()))
                .doOnError(error -> logger.error("Error creating product", error))
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Product> getAllOrders() {
        logger.info("Fetching all products");
        return service.getProducts()
                .doOnComplete(() -> logger.info("Successfully fetched all products"))
                .doOnError(error -> logger.error("Error fetching products", error));
    }

    @GetMapping("/{id}")
    public Mono<Product> getProductById(@PathVariable("id") Long id) {
        logger.info("Fetching product details for ID: {}", id);
        return service.getProductById(id)
                .doOnSuccess(product -> {
                    if (product != null) {
                        logger.info("Successfully fetched product: {}", product);
                    } else {
                        logger.warn("Product with ID {} not found", id);
                    }
                })
                .doOnError(error -> logger.error("Error fetching product with ID: {}", id, error));
    }
}
