package com.braintech.nimbitelegram.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderAddressDTO {
    private String address;
    private Long number;
    private String complement;
    private String city;
    private String zipCode;
    private String state;
    private String country;
    private String neighborhood;
    private String building;
    private String room;
    private String floor;
}
