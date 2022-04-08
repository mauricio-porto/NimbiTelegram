package com.braintech.nimbitelegram.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderPartnerSupplierDTO {

    private PurchaseOrderRegisterDTO register;
    private PurchaseOrderAddressDTO address;
    private PurchaseOrderContactDTO contact;

}
