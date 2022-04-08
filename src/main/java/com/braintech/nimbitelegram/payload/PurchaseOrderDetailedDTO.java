package com.braintech.nimbitelegram.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetailedDTO {
    private Long id;
    private String title;
    private String priorityCode;
    private Boolean isAddressByOrder;
    private String orderStatusCode;
    private String deliveryAddressExternalCode;
    private String deliveryAddressDescription;
    private String paymentAddressExternalCode;
    private String paymentAddressDescription;
    private String supplierCompanyTaxNumber;
    private Double totalPrice;
    private String totalQtdItems;
    private String createdBy;
    private String createdDate; // ZonedDateTime
    private String updatedBy;
    private String updatedDate; // ZonedDateTime
    private String paymentTypeCode;
    private String paymentTypeDescription;
    private String documentFormCode;
    private String incotermCode;
    private String incotermDescription;
    private String incotermExtraInfoCode;
    private String incotermExtraInfoDescription;
    private String incotermLocation;
    private String purchasingOrganizationCode;
    private String purchasingOrganizationDescription;
    private String permissionGroupCode;
    private String permissionGroupName;
    private String filialCode;
    private String filialName;
    private String codeERP;
    private String createdDateERP; // DateTime 2014-12-31
    private String referenceContract;
    private String supplierRefCode;
    private String companyCurrencyISO;
    private String buyerCountryCode;
    private String buyerTaxNumber;
    private String buyerContact;
    private String rejectedReasonCode;
    private String rejectedReasonDescription;
    private String supplierCompanyName;
    private String supplierCompanyCountryCode;
    private String supplyConditions;
    private PurchaseOrderPartnerSupplierDTO partner;
    private PurchaseOrderPartnerSupplierDTO goodsSupplier;

}
