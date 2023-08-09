package com.fiseq.truckcompany.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiseq.truckcompany.constants.TruckModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TruckDto extends DtoBase{
    private ArrayList<TruckModel> truckModels;
    private TruckModel truckModel;
    private HashMap<String,Object> truckModelAttributes;
    private String successMessage;
    private String errorMessage;
    private Long truckId;
    private boolean onTheJob;
    private double moneyLeftInAccount;
}