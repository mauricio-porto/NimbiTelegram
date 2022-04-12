package com.braintech.nimbitelegram.service;

import com.braintech.nimbitelegram.commons.ErrorDTO;
import com.braintech.nimbitelegram.configuration.BotTelegramClientConfig;
import com.braintech.nimbitelegram.payload.ResponseSendMessageTelegramDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class BotTelegramClient {

    @NonNull
    private final BotTelegramClientConfig botTelegramClientConfig;

    public Mono<ResponseSendMessageTelegramDTO> sendMessageToMonitores(String message) {
        return botTelegramClientConfig.getBotTelegramWebClient()
                .post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(botTelegramClientConfig.getBotToken())
                                .path(botTelegramClientConfig.getSendMessageUri())
                                .queryParam("chat_id", botTelegramClientConfig.getMonitoresUnimedChatId())
                                .queryParam("text", message)
                                .queryParam("protect_content", true)
                                .build())
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
                .bodyToMono(new ParameterizedTypeReference<ResponseSendMessageTelegramDTO>() {
                })
                .doOnError(e -> log.error("DEU MERDA:  " + e.getMessage()));
    }

}
