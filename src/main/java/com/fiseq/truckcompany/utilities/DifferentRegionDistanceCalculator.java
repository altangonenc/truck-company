package com.fiseq.truckcompany.utilities;

import com.fiseq.truckcompany.constants.FreightTerminals;

public class DifferentRegionDistanceCalculator extends DistanceCalculator{

    public DifferentRegionDistanceCalculator(FreightTerminals from, FreightTerminals to) {
        super(from, to);
    }

    @Override
    public double calculateRoute() {
        return 0;
    }
}
