package com.fiseq.truckcompany.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DtoBase implements Serializable {
    private static final long serialVersionUID = -1451448999611432894L;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException | NullPointerException e) {
            return "ERROR MAPPING OBJECT";
        }
    };
}
