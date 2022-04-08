package com.braintech.nimbitelegram.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public
class Metadata {
    private String totalRows;
    private String offset;
    private String limit;
    private String next;
    private String last;
}
