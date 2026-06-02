package com.minimarket.controller;

import com.minimarket.dto.CreateProductRequest;
import com.minimarket.dto.ProductResponse;
import com.minimarket.dto.UpdateProductRequest;
import com.minimarket.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody @Valid CreateProductRequest request) {
        return productService.create(request);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/search")
    public List<ProductResponse> findByName(@RequestParam String name, Pageable pageable) {
        return productService.findByName(name, pageable);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @GetMapping
    public Page<ProductResponse> findAll(Pageable pageable){
        return productService.findAll(pageable);
    }
}
