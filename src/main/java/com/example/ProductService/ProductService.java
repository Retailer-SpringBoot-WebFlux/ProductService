package com.example.ProductService;

import com.example.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    @KafkaListener(topics = "order-topic", groupId = "product-group")
    public void consumeOrderEvent(OrderEvent event) {
        System.out.println("🔄 Received Order Event: " + event);
        updateStock(event.getProductId(), event.getQuantity())
                .doOnSuccess(updated -> System.out.println("Stock updated for product: " + event.getProductId()))
                .doOnError(error -> System.err.println("Failed to update stock: " + error.getMessage()))
                .subscribe();
    }
    public Mono<Void> updateStock(Long productId, int quantity) {
        return repository.findById(productId)
                .flatMap(product -> {
                    if (product.getStockQuantity() >= quantity) {
                        product.setStockQuantity(product.getStockQuantity() - quantity);
                        return repository.save(product);
                    } else {
                        return Mono.error(new RuntimeException("Not enough stock for product: " + productId));
                    }
                })
                .then();
    }

}
