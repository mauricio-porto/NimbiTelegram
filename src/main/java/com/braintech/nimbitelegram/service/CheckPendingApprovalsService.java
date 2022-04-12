package com.braintech.nimbitelegram.service;

import com.braintech.nimbitelegram.commons.OrderStatus;
import com.braintech.nimbitelegram.domain.PendingPurchaseOrders;
import com.braintech.nimbitelegram.payload.*;
import com.braintech.nimbitelegram.repository.PendingPurchaseOrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Component
public class CheckPendingApprovalsService {

    private final BotTelegramClient botTelegramClient;

    private final PendingPurchaseOrdersRepository pendingPurchaseOrdersRepository;
    private final NimbiComprasClient nimbiComprasClient;

    private final Long recordId = 0L;

    private final int MAX_WAIT_HOURS = 12;


    @Scheduled(cron = "0 0 8-20/2 * * MON-FRI")
//    @Scheduled(cron = "0 */2 * * * MON-FRI")
    public void checkPendingApprovals() {
        var pendingApprovalList = fetchPurchaseOrdersFromNimbi(OrderStatus.PENDING_APPROVAL);

        List<PurchaseOrderDetailedDTO> detailedFiltered = getPurchaseOrdersDetailedFiltered(pendingApprovalList);

        if (!detailedFiltered.isEmpty()) {
            executeSendMessage(detailedFiltered);
            savePendingPurchaserOrders(detailedFiltered);
        } else {
            log.info("No pending approvals this time.");
        }
    }

    private void executeSendMessage(List<PurchaseOrderDetailedDTO> detailedFiltered) {
        StringBuilder sb = new StringBuilder("Pedidos pendentes há mais de 12h: ");
        sb.append(detailedFiltered.stream().map(PurchaseOrderDetailedDTO::getId).map(String::valueOf).collect(Collectors.joining(",")));
        var responseSendMessageTelegramDTO = botTelegramClient.sendMessageToMonitores(sb.toString()).block();
        log.debug(responseSendMessageTelegramDTO.toString());
    }

    private List<PurchaseOrderDTO> fetchPurchaseOrdersFromNimbi(OrderStatus orderStatus) {

        List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<>();

        int offset = 0;
        int blockSize = 100;
        var now = ZonedDateTime.now();
        PurchaseOrdersRequestDTO purchaseOrdersRequestDTO = PurchaseOrdersRequestDTO.builder()
                .initialDate(LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .finalDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .orderStatusId(orderStatus.getStatus())
                .offset(offset)
                .limit(blockSize)
                .build();

        ResponsePurchaseOrders responsePurchaseOrders = nimbiComprasClient.findPurchaseOrders(purchaseOrdersRequestDTO).block();

        List<PurchaseOrderDTO> purchaseOrders = responsePurchaseOrders.getPurchaseOrders()
                .stream()
                .filter(purchaseOrderDTO -> {
                    return Duration.between(ZonedDateTime.parse(purchaseOrderDTO.getUpdateDate()), now).toHours() > MAX_WAIT_HOURS;
                }).collect(Collectors.toList());
        if (!purchaseOrders.isEmpty()) {
            purchaseOrderDTOList.addAll(purchaseOrders);
        }

        Metadata metadata = responsePurchaseOrders.getMetadata();
        int totalRows = Integer.parseInt(metadata.getTotalRows());
        int nrIteracoes = totalRows / 100;  // A primeira iteração já foi

        for (int iteracao = 1; iteracao <= nrIteracoes; iteracao++) {
            purchaseOrdersRequestDTO.setOffset(offset += blockSize);
            purchaseOrders = nimbiComprasClient.findPurchaseOrders(purchaseOrdersRequestDTO).block().getPurchaseOrders()
                    .stream()
                    .filter(purchaseOrderDTO -> {
                        return Duration.between(ZonedDateTime.parse(purchaseOrderDTO.getUpdateDate()), now).toHours() > MAX_WAIT_HOURS;
                    }).collect(Collectors.toList());
            if (!purchaseOrders.isEmpty()) {
                purchaseOrderDTOList.addAll(purchaseOrders);
            }
        }

        return purchaseOrderDTOList;
    }

    private List<PurchaseOrderDetailedDTO> getPurchaseOrdersDetailedFiltered(List<PurchaseOrderDTO> purchaseOrderDTOList) {
        List<PurchaseOrderDetailedDTO> purchaseOrderDetailedDTOList = new ArrayList<>();
        purchaseOrderDTOList.forEach(purchaseOrderDTO -> {
            purchaseOrderDetailedDTOList.add(nimbiComprasClient.findPurchaseOrder(purchaseOrderDTO.getId()).block().getPurchaseOrder());
        });
        return purchaseOrderDetailedDTOList
                .stream()
                .filter(purchaseOrderDetailedDTO -> !purchaseOrderDetailedDTO.getDocumentFormCode().contains("_FORM_"))
                .filter(purchaseOrderDetailedDTO -> !purchaseOrderDetailedDTO.getDocumentFormCode().contains("REGULARIZA"))
                .filter(purchaseOrderDetailedDTO -> !purchaseOrderDetailedDTO.getDocumentFormCode().contains("PCNA_"))
                .collect(Collectors.toList());
    }

    private void savePendingPurchaserOrders(List<PurchaseOrderDetailedDTO> purchaseOrderDetailedDTOList) {
        var idSupplier = getIdSequenceSupplier();

        Flux.fromStream(
                purchaseOrderDetailedDTOList.stream().map(purchaseOrderDetailedDTO -> {
                    String id;
                    var pendingApprovalPurchaseOrder = pendingPurchaseOrdersRepository
                            .findByPurchaseOrderDetailedDTO_Id(purchaseOrderDetailedDTO.getId()).block();
                    if (Objects.nonNull(pendingApprovalPurchaseOrder)) {
                        id = pendingApprovalPurchaseOrder.getId();
                    } else {
                        id = idSupplier.get();
                    }
                    return pendingPurchaseOrdersRepository
                            .save(new PendingPurchaseOrders(id, new Date(), purchaseOrderDetailedDTO));
                })
        ).subscribe(m -> log.info("Pending purchase order saved: {}", m.block()));
        log.info("Repository contains now {} entries.", pendingPurchaseOrdersRepository.count().block());
    }

    private Supplier<String> getIdSequenceSupplier() {
        return new Supplier<>() {
            Long l = pendingPurchaseOrdersRepository.count().block();

            @Override
            public String get() {
                return String.format("%06d", l++);
            }
        };
    }
}
