package com.example.datn_sevenstrike.controller.client;

import com.example.datn_sevenstrike.dto.client.*;
import com.example.datn_sevenstrike.service.client.ClientOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientOrderService service;

    @GetMapping("/products")
    public ResponseEntity<List<ProductClientDTO>> getProducts() {
        return ResponseEntity.ok(service.getProducts());
    }

    @GetMapping("/products/best-selling")
    public ResponseEntity<List<ProductClientDTO>> bestSelling() {
        return ResponseEntity.ok(service.getBestSellingProducts());
    }

    @GetMapping("/products/new-arrivals")
    public ResponseEntity<List<ProductClientDTO>> newArrivals() {
        return ResponseEntity.ok(service.getNewArrivalProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDetailClientDTO> getProductDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getProductDetail(id));
    }

    @GetMapping("/vouchers")
    public ResponseEntity<List<VoucherClientDTO>> getVouchers() {
        return ResponseEntity.ok(service.getVouchers());
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest req) {
        return ResponseEntity.ok(service.createOrder(req));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<ClientOrderHistoryDTO>> getOrders(@RequestParam(required = false) Integer customerId) {
        return ResponseEntity.ok(service.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ClientOrderDetailDTO> getOrderDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getOrderDetail(id));
    }
}