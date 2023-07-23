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
    DAF_XF(0, "Daf", "XF", 0.05, 50, 10, 10),
    VOLVO_FH(1, "Volvo", "FH", 0.05, 60, 9, 11);
    private Integer truckId;
    private String brand;
    private String model;
    private double fuelConsumingPerformance;
    private double speedPerformance;
    private int crashRisk;
    private Integer price;

}
