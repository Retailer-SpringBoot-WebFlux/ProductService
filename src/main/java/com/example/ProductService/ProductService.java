package com.example.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public Mono<Product> saveProduct(Product product) {
        return repository.save(product)
                .doOnSuccess(savedProduct -> System.out.println("Product saved with ID: " + savedProduct.getId()));
    }
    public Flux<Product> getProducts() {
        return repository.findAll();
    }
    public Mono<Product> getProductById(Long id) {
        return repository.findById(id);
    }
}