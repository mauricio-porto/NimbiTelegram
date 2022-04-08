package com.braintech.nimbitelegram.domain;

import com.braintech.nimbitelegram.payload.PurchaseOrderDetailedDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingPurchaseOrders {

    @Id
    private String id;
    private Date createdDate;
    private PurchaseOrderDetailedDTO purchaseOrderDetailedDTO;
//    private Boolean pending;
}
