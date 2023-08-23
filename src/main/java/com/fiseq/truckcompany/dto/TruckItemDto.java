package com.fiseq.truckcompany.dto;

import com.fiseq.truckcompany.constants.TruckModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TruckItemDto {
    private TruckModel truckModel;
    private Long truckId;
    private double price;
}
