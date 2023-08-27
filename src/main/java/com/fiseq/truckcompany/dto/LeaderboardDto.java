package com.fiseq.truckcompany.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderboardDto {
    private String username;
    private double totalMoney;

    public LeaderboardDto(String username, double totalMoney) {
        this.username = username;
        this.totalMoney = totalMoney;
    }
}
