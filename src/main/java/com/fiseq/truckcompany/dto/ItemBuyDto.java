package com.fiseq.truckcompany.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemBuyDto {
    private Long itemId;
    private String errorMessage;
    private String successMessage;
}
