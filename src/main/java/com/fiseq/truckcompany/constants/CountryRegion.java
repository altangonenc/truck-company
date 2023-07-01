package com.fiseq.truckcompany.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountryRegion {
    EUROPE("Europe"),
    MIDDLE_EAST("Middle East"),
    ASIA("Asia"),
    AFRICA("Africa"),
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America");

    private final String name;
}
