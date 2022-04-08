package com.braintech.nimbitelegram.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDTO {
    private HttpStatus status;
    private Long errorCode;
    private String message;
}
