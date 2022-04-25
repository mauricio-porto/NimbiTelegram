package com.braintech.nimbitelegram.repository;

import com.braintech.nimbitelegram.domain.PurchaseOrders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface PurchaseOrdersRepository extends ReactiveSortingRepository<PurchaseOrders, String> {
}
