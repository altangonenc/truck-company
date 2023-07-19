package com.fiseq.truckcompany.utilities;

import com.fiseq.truckcompany.constants.FreightTerminals;
import com.fiseq.truckcompany.exception.DifferentRegionDistanceCalculationException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class DifferentRegionDistanceCalculatorTest {
    @Test
    public void testDistanceCalculator_whenBothCountrysInDifferentRegion_thenReturnDistance() throws DifferentRegionDistanceCalculationException {
        DifferentRegionDistanceCalculator distanceCalculator = new DifferentRegionDistanceCalculator(FreightTerminals.TURKEY, FreightTerminals.FRANCE);
        double distance = distanceCalculator.calculateRoute();
        System.out.println(distance);
        Assertions.assertNotNull(distance);
        Assertions.assertEquals((int)distance, 2827);
    }
    @Test
    public void testDistanceCalculator_whenBothCountrysInDifferentRegionAndShouldTranspassInTurkey_thenReturnDistance() throws DifferentRegionDistanceCalculationException {
        DifferentRegionDistanceCalculator distanceCalculator = new DifferentRegionDistanceCalculator(FreightTerminals.MOROCCO, FreightTerminals.FRANCE);
        double distance = distanceCalculator.calculateRoute();
        System.out.println(distance);
        Assertions.assertNotNull(distance);
        Assertions.assertEquals((int)distance, 7664);
    }

}
