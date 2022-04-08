package com.braintech.nimbitelegram.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum OrderStatus {
    DRAFT(1),
    PENDING_APPROVAL(2),
    APPROVED_WAITING_ACCEPT(3),
    RETURNED(4),
    CANCELED(5),
    APPROVED_ACCEPTED(6),
    RETURNED_SUPPLIER(7),
    WAITING_ERP(8),
    CHANGED_WAITING_ACCEPTANCE(9),
    CHANGED_WAITING_ERP(10),
    CHANGING_ORDER(11),
    APPROVED_PARTIALLY_ACCEPTED(12),
    NONE(0);

    private int status;

    public static OrderStatus from(int code) {
        return Arrays.asList(OrderStatus.values()).stream().filter(orderStatus -> orderStatus.getStatus() == code).findFirst().orElse(NONE);
    }
}
