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
@ConfigurationProperties(prefix = "bot-telegram")
public class BotTelegramClientConfig {

    private String serverUrl;

    private String botToken;

    private String sendMessageUri;

    private String monitoresUnimedChatId;

    private static final String SERVICE = "BOT-TELEGRAM";

    @Bean("botTelegramWebClient")
    @Qualifier("botTelegramWebClient")
    public WebClient getBotTelegramWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs())
                .exchangeStrategies(ExchangeStrategies.withDefaults())
                .baseUrl(serverUrl)
                .build();
    }

}
