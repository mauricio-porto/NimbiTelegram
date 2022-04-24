package com.braintech.nimbitelegram.repository;

import com.braintech.nimbitelegram.domain.PendingPurchaseOrders;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.stream.Stream;

public interface PendingPurchaseOrdersRepository extends ReactiveSortingRepository<PendingPurchaseOrders, String> {
    Flux<PendingPurchaseOrders> findAllByIdNotNullOrderByIdAsc(final Pageable page);

    Mono<PendingPurchaseOrders> findById(Long id);

    Mono<PendingPurchaseOrders> findByPurchaseOrderDetailedDTO_Id(Long id);

    @Query(value = "{createdDate: {'$ne':  null}}", sort = "{$natural : -1}")
    Flux<PendingPurchaseOrders> findLatest();

    @Query("{'createdDate' :  { $gt:  ?0, $lt:  ?1 } }")
    Flux<PendingPurchaseOrders> findByCreatedDateBetween(Date initialDate, Date finalDate);


    Flux<PendingPurchaseOrders> findByCreatedDate(Date updateTime);

}
