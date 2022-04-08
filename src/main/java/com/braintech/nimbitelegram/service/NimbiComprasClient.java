package com.braintech.nimbitelegram.service;

import com.braintech.nimbitelegram.commons.ErrorDTO;
import com.braintech.nimbitelegram.commons.OrderStatus;
import com.braintech.nimbitelegram.configuration.NimbiComprasClientConfig;
import com.braintech.nimbitelegram.payload.PurchaseOrdersRequestDTO;
import com.braintech.nimbitelegram.payload.ResponsePurchaseOrders;
import com.braintech.nimbitelegram.payload.ResponseSinglePurchaseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NimbiComprasClient {

    private static final String CLIENT_API_ID_PARAM_NAME = "ClientAPI_ID";
    private static final String CLIENT_API_KEY_PARAM_NAME = "ClientAPI_Key";
    private static final String CLIENT_COMPANY_TAX_NUMBER_PARAM_NAME = "companyTaxNumber";
    private static final String CLIENT_COMPANY_COUNTRY_PARAM_NAME = "companyCountryCode";

    @NonNull
    private final NimbiComprasClientConfig nimbiComprasClientConfig;

    public Mono<ResponsePurchaseOrders> findPurchaseOrders(PurchaseOrdersRequestDTO purchaseOrdersRequestDTO) {
        return nimbiComprasClientConfig.getNimbiComprasWebClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(nimbiComprasClientConfig.getPurchaseOrdersUri())
                                .queryParams(buildParams(purchaseOrdersRequestDTO))
                                .build())
                .header(CLIENT_API_ID_PARAM_NAME, nimbiComprasClientConfig.getClientId())
                .header(CLIENT_API_KEY_PARAM_NAME, nimbiComprasClientConfig.getClientKey())
                .header(CLIENT_COMPANY_TAX_NUMBER_PARAM_NAME, nimbiComprasClientConfig.getCompanyTaxNumber())
                .header(CLIENT_COMPANY_COUNTRY_PARAM_NAME, nimbiComprasClientConfig.getCompanyCountryCode())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.empty())
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response ->
                        response.bodyToMono(ErrorDTO.class)
                                .flatMap(s -> Mono.error(new Exception(s.getMessage())))
                )
                .onStatus(HttpStatus.BAD_REQUEST::equals, response ->
                        response.bodyToMono(ErrorDTO.class)
                                .switchIfEmpty(Mono.error(new Exception("")))
                                .flatMap(error -> Mono.error(new Exception(error.getMessage())))
                )
                .bodyToMono(new ParameterizedTypeReference<ResponsePurchaseOrders>() {
                })
                .doOnError(e -> log.error("DEU MERDA:  " + e.getMessage()));
    }

    public Mono<ResponseSinglePurchaseOrder> findPurchaseOrder(Long purchaseOrderId) {
        return nimbiComprasClientConfig.getNimbiComprasWebClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(nimbiComprasClientConfig.getPurchaseOrderUri())
                                .build(purchaseOrderId))
                .header(CLIENT_API_ID_PARAM_NAME, nimbiComprasClientConfig.getClientId())
                .header(CLIENT_API_KEY_PARAM_NAME, nimbiComprasClientConfig.getClientKey())
                .header(CLIENT_COMPANY_TAX_NUMBER_PARAM_NAME, nimbiComprasClientConfig.getCompanyTaxNumber())
                .header(CLIENT_COMPANY_COUNTRY_PARAM_NAME, nimbiComprasClientConfig.getCompanyCountryCode())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.empty())
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response ->
                        response.bodyToMono(ErrorDTO.class)
                                .flatMap(s -> Mono.error(new Exception(s.getMessage())))
                )
                .onStatus(HttpStatus.BAD_REQUEST::equals, response ->
                        response.bodyToMono(ErrorDTO.class)
                                .switchIfEmpty(Mono.error(new Exception("")))
                                .flatMap(error -> Mono.error(new Exception(error.getMessage())))
                )
                .bodyToMono(new ParameterizedTypeReference<ResponseSinglePurchaseOrder>() {
                })
                .doOnError(e -> log.error("DEU MERDA:  " + e.getMessage()));
    }

    private MultiValueMap<String, String> buildParamsDefaultValues() {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("initialDate", "2022-01-02T23:59:59.938Z");
        params.add("finalDate", "2022-02-10T23:59:59.938Z");
        params.add("orderStatusId", String.valueOf(OrderStatus.APPROVED_ACCEPTED.getStatus()));

        return params;
    }

    private MultiValueMap<String, String> buildParams(PurchaseOrdersRequestDTO purchaseOrdersRequestDTO) {
        if (purchaseOrdersRequestDTO.getInitialDate().isEmpty()) {
            return buildParamsDefaultValues();
        }
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        Optional.ofNullable(purchaseOrdersRequestDTO.getCodeERP()).ifPresent(codeErp -> params.add("codeERP", codeErp));

        Optional.ofNullable(purchaseOrdersRequestDTO.getInitialDate()).ifPresent(initialDate -> params.add("initialDate", initialDate));
        Optional.ofNullable(purchaseOrdersRequestDTO.getFinalDate()).ifPresent(finalDate -> params.add("finalDate", finalDate));
        Optional.of(purchaseOrdersRequestDTO.getOrderStatusId()).ifPresent(orderStatusId -> params.add("orderStatusId", String.valueOf(orderStatusId)));

        Optional.ofNullable(purchaseOrdersRequestDTO.getSupplierTaxnumber()).ifPresent(supplierTaxnumber -> params.add("supplierTaxnumber", supplierTaxnumber));
        Optional.ofNullable(purchaseOrdersRequestDTO.getBuyerTaxNumber()).ifPresent(buyerTaxNumber -> params.add("buyerTaxNumber", buyerTaxNumber));
        Optional.ofNullable(purchaseOrdersRequestDTO.getSort()).ifPresent(sort -> params.add("sort", sort));
        Optional.of(purchaseOrdersRequestDTO.getOffset()).ifPresent(offset -> params.add("offset", String.valueOf(offset)));
        Optional.of(purchaseOrdersRequestDTO.getLimit()).ifPresent(limit -> params.add("limit", String.valueOf(limit)));

        return params;
    }
}
