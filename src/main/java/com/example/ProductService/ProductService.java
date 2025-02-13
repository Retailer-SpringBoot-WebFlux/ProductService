package com.example.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No product found with ID: {}", id);
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
                }))
                .doOnError(error -> {
                    if (!(error instanceof ResponseStatusException)) {
                        log.error("Unexpected error fetching product with ID: {}", id, error);
                    }
                });
    }

}
