package com.payment.service.controller;


import com.payment.service.exception.InvalidInputException;
import com.payment.service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/route")
    public ResponseEntity<String> getCheapestRoute(@RequestParam String origin, @RequestParam String destination) {
        if (origin == null || origin.isEmpty() || destination == null || destination.isEmpty()) {
            throw new InvalidInputException("Origin and destination branches must not be empty.");
        }

        String path = paymentService.processPayment(origin, destination);

        if (path == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(path);
    }
}