package com.braintech.nimbitelegram.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "nimbi-compras")
public class NimbiComprasClientConfig {

    private String serverUrl;

    private String clientId;

    private String clientKey;

    private String companyTaxNumber;

    private String companyCountryCode;

    private String purchaseOrdersUri;

    private String purchaseOrderUri;

    private String requisitionsUri;

    private Integer connectTimeout;

    private Integer readTimeout;

    private static final String SERVICE = "COMPRAS";

    @Bean("nimbiComprasWebClient")
    @Qualifier("nimbiComprasWebClient")
    public WebClient getNimbiComprasWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs())
                .exchangeStrategies(ExchangeStrategies.withDefaults())
                .baseUrl(serverUrl)
                .build();
    }

}
