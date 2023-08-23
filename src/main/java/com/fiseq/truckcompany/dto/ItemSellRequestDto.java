package com.fiseq.truckcompany.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSellRequestDto {
    private double price;
    private Long itemId;
}
