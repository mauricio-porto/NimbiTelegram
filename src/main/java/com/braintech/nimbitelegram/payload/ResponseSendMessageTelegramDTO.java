package com.braintech.nimbitelegram.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSendMessageTelegramDTO {
    // {
    //  "ok": true,
    //  "result": {
    //    "message_id": 6,
    //    "sender_chat": {
    //      "id": -1001525001107,
    //      "title": "MonitoresUnimedPOA",
    //      "username": "monitoresUnimedPOA",
    //      "type": "channel"
    //    },
    //    "chat": {
    //      "id": -1001525001107,
    //      "title": "MonitoresUnimedPOA",
    //      "username": "monitoresUnimedPOA",
    //      "type": "channel"
    //    },
    //    "date": 1649253912,
    //    "text": "Testiculo"
    //  }
    //}

    private Boolean ok;
    private ResultData result;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class ResultData {
        @JsonProperty("message_id")
        private String messageId;
        @JsonProperty("sender_chat")
        private ChatData senderChat;
        private ChatData chat;
        private String date;
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class ChatData {
        private String id;
        private String title;
        private String username;
        private String type;
    }
}
