package com.fiseq.truckcompany.utilities;


import com.fiseq.truckcompany.constants.FreightTerminals;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class SameRegionDistanceCalculatorTest {

    @Test
    public void testDistanceCalculator_whenBothCountryInSameRegion_thenReturnDistance() {
        SameRegionDistanceCalculator distanceCalculator = new SameRegionDistanceCalculator(FreightTerminals.FRANCE, FreightTerminals.BELGIUM);
        double distance = distanceCalculator.calculateRoute();
        System.out.println(distance);
        Assertions.assertNotNull(distance);
        Assertions.assertEquals((int)distance, 473);
    }
    @Test
    public void testDistanceCalculator_whenSameCountries_thenReturnZeroDistance() {
        SameRegionDistanceCalculator distanceCalculator = new SameRegionDistanceCalculator(FreightTerminals.FRANCE, FreightTerminals.FRANCE);
        double distance = distanceCalculator.calculateRoute();
        System.out.println(distance);
        Assertions.assertNotNull(distance);
        Assertions.assertEquals((int)distance, 0);
    }
}
