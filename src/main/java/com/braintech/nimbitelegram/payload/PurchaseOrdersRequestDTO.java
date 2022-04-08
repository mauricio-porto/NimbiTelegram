package com.braintech.nimbitelegram.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseOrdersRequestDTO {
    // https://api001.nimbi.com.br/CompraAPI/rest/PurchaseOrders/v1?codeERP={codeERP}&initialDate={initialDate}&finalDate={finalDate}
    // &orderStatusId={orderStatusId}&supplierTaxnumber={supplierTaxnumber}
    // &buyerTaxNumber={buyerTaxNumber}&sort={sort}&offset={offset}&limit={limit}
    private String codeERP;
    private String initialDate;
    private String finalDate;
    private int orderStatusId;
    private  String supplierTaxnumber;
    private String buyerTaxNumber;
    private String sort;
    private int offset;
    private int limit;
}
