package com.fiseq.truckcompany.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public enum TruckModel {
    /*
    * Consider fuelConsumingPerformance and speedPerformance like placement of truck
    * in the ranking table with others. If a truck's fuelConsumingPerformance is 1,
    * it means this truck has the most efficient fuel performance when compared
    *  with others.
    *
    * crashRisk value 0<x<=10 for trucks. It means that if you spend more money to your
    * investments it will returns you back :)
    *
    */
    //TO DO :::::: price degerleri ayarlanmali
    DAF_XF(1, "Daf", "XF", 2, 2, 3, new BigInteger("10000")),
    VOLVO_FH(2, "Volvo", "FH", 4, 3, 5, new BigInteger("15000"));
    private Integer truckId;
    private String brand;
    private String model;
    private Integer fuelConsumingPerformance;
    private Integer speedPerformance;
    private Integer crashRisk;
    private BigInteger price;

}
