package com.braintech.nimbitelegram.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSinglePurchaseOrder {

    private PurchaseOrderDetailedDTO purchaseOrder;
    private ErrorsFieldResponse errors;

}
