package com.braintech.nimbitelegram.controller;

import com.braintech.nimbitelegram.domain.PendingPurchaseOrders;
import com.braintech.nimbitelegram.repository.PendingPurchaseOrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@CrossOrigin(value = { "http://localhost:4200" }, allowedHeaders = { "*" }, maxAge = 900)
@RestController
public class PurchaseOrdersController {

    private static final int DELAY_PER_ITEM_MS = 100;

    private final PendingPurchaseOrdersRepository pendingPurchaseOrdersRepository;

    @GetMapping("/api/pendingPurchaseOrders")
    public Flux<PendingPurchaseOrders> getPendingPurchaseOrdersFlux() {
        return pendingPurchaseOrdersRepository.findAll();
    }
}
