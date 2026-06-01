package com.zerock.driveu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {
    private String receiveLocation;
    private String receiveDate;
    private String merchant_uid;
    private String imp_uid;
    private int amount;
}