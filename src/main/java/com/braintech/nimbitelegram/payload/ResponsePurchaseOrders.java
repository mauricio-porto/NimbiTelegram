package com.braintech.nimbitelegram.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePurchaseOrders {

    private Metadata metadata;
    private List<PurchaseOrderDTO> purchaseOrders;
    private ErrorsFieldResponse errors;

}
