package com.braintech.nimbitelegram.repository;

import com.braintech.nimbitelegram.domain.PendingPurchaseOrders;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface PendingPurchaseOrdersRepository extends ReactiveSortingRepository<PendingPurchaseOrders, String> {
}
