package com.braintech.nimbitelegram.service;

import com.braintech.nimbitelegram.commons.OrderStatus;
import com.braintech.nimbitelegram.domain.PendingPurchaseOrders;
import com.braintech.nimbitelegram.payload.*;
import com.braintech.nimbitelegram.repository.PendingPurchaseOrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Component
/**
 * Atenção: um registro PurchaseOrder (MongoDB) possui o campo 'purchaseOrderDTO' que não possui detalhes do pedido.
 * Quem possui detalhes é o PurchaseOrderDetailedDTO (que tem o campo 'documentFormCode' (enum já declarado)
 * O método que busca um 'purchaseOrder' específico é o NimbiComprasClient::findPurchaseOrder(Long purchaseOrderId)
 *
 * Guardar cada PurchaseOrder pendente e filtrado, para poder enriquecer as notificações.
 *
 *
 * Lógica:
 * + Desmarca todos os registros do repositório local de pendências como PENDING = false
 * + Faz a carga dos purchaseOrders pendentes (PENDING_APPROVAL) desde a data inicial (Jan/2022)
 * + Filtra essa lista de pendentes (updateDate maior que o MAX_WAIT)
 * - Busca cada pedido da lista no repositório local de pendências
 *   - Se encontra, marca como PENDING
 *   - Se não encontra, busca detalhes do pedido, filtra por DocumentFormCode e guarda no repositório local de pendências, marcando como PENDING
 * - Remove todos os registros que restaram com PENDING = false
 *
 * - Envia notificação com a lista de pedidos de compra pendentes
 *
 */
public class CheckPendingApprovalsService implements ApplicationRunner {

    private final BotTelegramClient botTelegramClient;

    private final PendingPurchaseOrdersRepository pendingPurchaseOrdersRepository;
    private final NimbiComprasClient nimbiComprasClient;

    private final Long recordId = 0L;

    private final int MAX_WAIT_HOURS = 12;


//    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
//    @Scheduled(cron = "0 0 8-20/2 * * MON-FRI")
    private void executeSendMessage() {
        var message = "Pedidos assistenciais pendentes há mais de 12h: ";
        // .collect(Collectors.joining(',')); OU String.join(",", lista);
        var responseSendMessageTelegramDTO = botTelegramClient.sendMessageToMonitores(message).block();
        log.debug(responseSendMessageTelegramDTO.toString());
    }

//    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.MINUTES)
//    @Scheduled(cron = "0 0 8-20/2 * * MON-FRI")
//    private void executeLoadFromNimbi() {
//        var lista = loadFromNimbi(OrderStatus.PENDING_APPROVAL);
//        log.info(String.valueOf(lista.isEmpty()));
//    }

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
                        return Duration.between(now, LocalDateTime.parse(purchaseOrderDTO.getUpdateDate())).toHours() > MAX_WAIT_HOURS;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var pendingApprovalList = fetchPurchaseOrdersFromNimbi(OrderStatus.PENDING_APPROVAL);
        if (pendingPurchaseOrdersRepository.count().block() > 0) {
//            pendingPurchaseOrdersRepository.findAll().subscribe(pendingPurchaseOrder -> {
//                pendingPurchaseOrder.setPending(false);
//                pendingPurchaseOrdersRepository.save(pendingPurchaseOrder);
//            });
            // Remove da 'pendingApprovalList' todos os que já estão em pendingPurchaseOrdersRepository
            pendingApprovalList.forEach(purchaseOrderDTO -> {
                var pendingApprovalPurchaseOrder = pendingPurchaseOrdersRepository.findById(purchaseOrderDTO.getId()).block();
                if (Optional.ofNullable(pendingApprovalPurchaseOrder).isPresent()) {
                    pendingApprovalList.remove(purchaseOrderDTO);
                }
            });
        }

        List<PurchaseOrderDetailedDTO> detailedFiltered = getPurchaseOrdersDetailedFiltered(pendingApprovalList);

        if (!detailedFiltered.isEmpty()) {
            executeSendMessage();
            savePendingPurchaserOrders(detailedFiltered);
        }
    }

    private void savePendingPurchaserOrders(List<PurchaseOrderDetailedDTO> purchaseOrderDetailedDTOList) {
        var idSupplier = getIdSequenceSupplier();

        Flux.fromStream(
                purchaseOrderDetailedDTOList.stream().map(purchaseOrderDetailedDTO -> {
                    return pendingPurchaseOrdersRepository
                            .save(new PendingPurchaseOrders(idSupplier.get(), new Date(), purchaseOrderDetailedDTO));
                })
        ).subscribe(m -> log.info("New pending purchase order saved: {}", m.block()));
        log.info("Repository contains now {} entries.", pendingPurchaseOrdersRepository.count().block());
    }

    private Supplier<String> getIdSequenceSupplier() {
        return new Supplier<>() {
            Long l = 0L;

            @Override
            public String get() {
                // adds padding zeroes
                return String.format("%06d", l++);
            }
        };
    }

}
