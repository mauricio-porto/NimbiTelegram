package com.braintech.nimbitelegram.domain;

import com.braintech.nimbitelegram.commons.OrderStatus;
import com.braintech.nimbitelegram.payload.PurchaseOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrders {

    @Id
    private String id;
    private Date createdDate;
    private String user;
    private PurchaseOrderDTO purchaseOrderDTO;
    private OrderStatus orderStatus;
}
