package com.braintech.nimbitelegram.repository;

import com.braintech.nimbitelegram.domain.PendingPurchaseOrders;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PendingPurchaseOrdersRepository extends ReactiveSortingRepository<PendingPurchaseOrders, String> {
    Flux<PendingPurchaseOrders> findAllByIdNotNullOrderByIdAsc(final Pageable page);

    Mono<PendingPurchaseOrders> findById(Long id);

    Mono<PendingPurchaseOrders> findByPurchaseOrderDetailedDTO_Id(Long id);
}
