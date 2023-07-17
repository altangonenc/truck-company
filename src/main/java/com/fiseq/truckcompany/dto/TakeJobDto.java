package com.fiseq.truckcompany.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TakeJobDto {
    private String[] route;
    private Long truckId;
}
