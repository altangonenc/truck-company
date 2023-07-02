package com.fiseq.truckcompany.utilities;

import com.fiseq.truckcompany.constants.FreightTerminals;


public class SameRegionDistanceCalculator extends DistanceCalculator{

    public SameRegionDistanceCalculator(FreightTerminals from, FreightTerminals to) {
        super(from, to);
    }

    @Override
    public double calculateRoute() {
        FreightTerminals originationTerminal = getFrom();
        FreightTerminals destinationTerminal = getTo();
        return calculateDistance(originationTerminal.getLatitude(),originationTerminal.getLongitude(),
                destinationTerminal.getLatitude(),destinationTerminal.getLongitude());
    }
}
