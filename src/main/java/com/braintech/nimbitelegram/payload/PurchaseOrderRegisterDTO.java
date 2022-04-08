package com.braintech.nimbitelegram.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderRegisterDTO {
    private String externalCode;
    private String companyName;
    private String taxNumber;
    private String sefazInscription;
}
