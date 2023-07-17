package com.fiseq.truckcompany.utilities;

import com.fiseq.truckcompany.constants.CountryRegion;
import com.fiseq.truckcompany.constants.FreightTerminals;
import com.fiseq.truckcompany.exception.DifferentRegionDistanceCalculationException;

public class DifferentRegionDistanceCalculator extends DistanceCalculator {

    public DifferentRegionDistanceCalculator(FreightTerminals from, FreightTerminals to) {
        super(from, to);
    }

    @Override
    public double calculateRoute() throws DifferentRegionDistanceCalculationException {

        if (((getFrom().getRegion() == CountryRegion.SOUTH_AMERICA || getFrom().getRegion() == CountryRegion.NORTH_AMERICA)
                && (getTo().getRegion() != CountryRegion.SOUTH_AMERICA || getTo().getRegion() != CountryRegion.NORTH_AMERICA))
                || ((getTo().getRegion() == CountryRegion.SOUTH_AMERICA || getTo().getRegion() == CountryRegion.NORTH_AMERICA)
                && (getFrom().getRegion() != CountryRegion.SOUTH_AMERICA || getFrom().getRegion() != CountryRegion.NORTH_AMERICA))) {

            throw new DifferentRegionDistanceCalculationException();
        }

        if ((getTo().getRegion() == CountryRegion.EUROPE && getFrom().getRegion() != CountryRegion.EUROPE) ||
                (getFrom().getRegion() == CountryRegion.EUROPE && getTo().getRegion() != CountryRegion.EUROPE)) {

            if (getFrom().getRegion() == CountryRegion.AFRICA) {
                double distanceBetweenFromCountryAndIsrael = calculateDistance(getFrom().getLatitude(), getFrom().getLongitude(), FreightTerminals.ISRAEL.getLatitude(), FreightTerminals.ISRAEL.getLongitude());
                double distanceBetweenIsraelAndTurkey = calculateDistance(FreightTerminals.ISRAEL.getLatitude(), FreightTerminals.ISRAEL.getLongitude(), FreightTerminals.TURKEY.getLatitude(), FreightTerminals.TURKEY.getLongitude());
                double distanceBetweenTurkeyAndToCountry = calculateDistance(FreightTerminals.TURKEY.getLatitude(), FreightTerminals.TURKEY.getLongitude(), getTo().getLatitude(), getTo().getLongitude());
                return distanceBetweenFromCountryAndIsrael + distanceBetweenIsraelAndTurkey + distanceBetweenTurkeyAndToCountry;

            }

            if (getTo().getRegion() == CountryRegion.AFRICA) {
                double distanceBetweenToCountryAndIsrael = calculateDistance(getTo().getLatitude(), getTo().getLongitude(), FreightTerminals.ISRAEL.getLatitude(), FreightTerminals.ISRAEL.getLongitude());
                double distanceBetweenIsraelAndTurkey = calculateDistance(FreightTerminals.ISRAEL.getLatitude(), FreightTerminals.ISRAEL.getLongitude(), FreightTerminals.TURKEY.getLatitude(), FreightTerminals.TURKEY.getLongitude());
                double distanceBetweenTurkeyAndFromCountry = calculateDistance(FreightTerminals.TURKEY.getLatitude(), FreightTerminals.TURKEY.getLongitude(), getFrom().getLatitude(), getFrom().getLongitude());
                return distanceBetweenToCountryAndIsrael + distanceBetweenIsraelAndTurkey + distanceBetweenTurkeyAndFromCountry;

            }

            if (getTo().getRegion() == CountryRegion.MIDDLE_EAST || getTo().getRegion() == CountryRegion.ASIA
                    || getFrom().getRegion() == CountryRegion.MIDDLE_EAST || getFrom().getRegion() == CountryRegion.ASIA) {

                double distanceBetweenTurkeyAndFromCountry = calculateDistance(FreightTerminals.TURKEY.getLatitude(), FreightTerminals.TURKEY.getLongitude(), getFrom().getLatitude(), getFrom().getLongitude());
                double distanceBetweenTurkeyAndToCountry = calculateDistance(FreightTerminals.TURKEY.getLatitude(), FreightTerminals.TURKEY.getLongitude(), getTo().getLatitude(), getTo().getLongitude());
                return distanceBetweenTurkeyAndFromCountry + distanceBetweenTurkeyAndToCountry;
            }
        }

        if ((getTo().getRegion() == CountryRegion.MIDDLE_EAST && getFrom().getRegion() == CountryRegion.ASIA)
                || (getFrom().getRegion() == CountryRegion.MIDDLE_EAST && getTo().getRegion() == CountryRegion.ASIA)) {
            return calculateDistance(getFrom().getLatitude(),getFrom().getLongitude(),getTo().getLatitude(),getTo().getLongitude());
        }

        if ((getTo().getRegion() == CountryRegion.AFRICA && (getFrom().getRegion() == CountryRegion.MIDDLE_EAST || getFrom().getRegion() == CountryRegion.ASIA))
                || (getFrom().getRegion() == CountryRegion.AFRICA && (getTo().getRegion() == CountryRegion.MIDDLE_EAST || getTo().getRegion() == CountryRegion.ASIA))) {
            double distanceBetweenFromCountryAndIsrael = calculateDistance(getFrom().getLatitude(), getFrom().getLongitude(), FreightTerminals.ISRAEL.getLatitude(), FreightTerminals.ISRAEL.getLongitude());
            double distanceBetweenToCountryAndIsrael = calculateDistance(getTo().getLatitude(), getTo().getLongitude(), FreightTerminals.ISRAEL.getLatitude(), FreightTerminals.ISRAEL.getLongitude());
            return distanceBetweenToCountryAndIsrael + distanceBetweenFromCountryAndIsrael;
        }


        throw new IllegalArgumentException();
    }
}
