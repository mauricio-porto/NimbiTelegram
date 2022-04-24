package com.braintech.nimbitelegram.service;

import com.braintech.nimbitelegram.domain.PendingPurchaseOrders;
import com.braintech.nimbitelegram.repository.PendingPurchaseOrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class NimbiComprasService {

    private static final long FIVE_MINUTES = 5 * 60 * 1000;
    private final PendingPurchaseOrdersRepository pendingPurchaseOrdersRepository;
    public final PendingPurchaseOrders findLatest() {
        return pendingPurchaseOrdersRepository.findLatest().blockFirst();
    }

    public final Flux<PendingPurchaseOrders> getPendingApprovalsForUpdateTime(Date updateTime) {
        Date startTime = new Date(updateTime.getTime() - FIVE_MINUTES);

        Flux<PendingPurchaseOrders> purchaseOrdersRepositoryByCreatedDate = pendingPurchaseOrdersRepository.findByCreatedDate(updateTime);

        return pendingPurchaseOrdersRepository.findByCreatedDateBetween(startTime, updateTime);
    }
}
