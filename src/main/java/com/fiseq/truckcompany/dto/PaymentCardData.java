package com.fiseq.truckcompany.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCardData {
    private String cardHolderName;
    private String cardNumber;
    private String expireMonth;
    private String expireYear;
    private String cvc;
}
