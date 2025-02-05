package com.example.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // Enables structured logging
public class ProductService {
    private final ProductRepository repository;

    public Mono<Product> saveProduct(Product product) {
        log.info("Attempting to save product: {}", product);
        return repository.save(product)
                .doOnSuccess(savedProduct -> log.info("Product saved successfully with ID: {}", savedProduct.getId()))
                .doOnError(error -> log.error("Failed to save product: {}", product, error));
    }

    public Flux<Product> getProducts() {
        log.info("Fetching all products...");
        return repository.findAll()
                .doOnComplete(() -> log.info("Successfully retrieved all products"))
                .doOnError(error -> log.error("Error retrieving products", error));
    }

    public Mono<Product> getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        return repository.findById(id)
                .doOnSuccess(product -> Optional.ofNullable(product)
                        .ifPresentOrElse(
                                p -> log.info("Product found: {}", p),
                                () -> log.warn("No product found with ID: {}", id)
                        ))
                .doOnError(error -> log.error("Error fetching product with ID: {}", id, error));
    }
}
