package org.projekt17.fahrschuleasa.planner.problemFacts;

import org.projekt17.fahrschuleasa.domain.Location;

import java.io.Serializable;

/**
 * This class shall mimic the location class used by the main repository.
 * This is exclusively used for planning.
 */
public class PlanningLocation implements Serializable {

    // measured in degrees
    private double latitude;
    private double longitude;

    // measured in km
    private final static double EARTH_RADIUS = 6371.785;
    // TODO latitude der Fahrschule benutzen
    private final static double LOCAL_RADIUS = computeRadiusOfLatitude(49.2577); // radius of latitude in Saarbrücken

    public PlanningLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlanningLocation(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    private static double computeRadiusOfLatitude(double latitude) {
        return 2.0 * Math.PI * EARTH_RADIUS * Math.sin(90.0 - latitude);
    }

    /**
     * Computes the distance in km between two points on the earth given their coordinates.
     */
    public double distanceTo(PlanningLocation o) {
        double differenceLatitudes = Math.abs(this.latitude - o.latitude);
        double differenceLongitudes = Math.abs(this.longitude - o.longitude);
        double differenceLatitudesKM = (LOCAL_RADIUS / 360.0) * differenceLatitudes;
        double differenceLongitudesKM = (EARTH_RADIUS / 360.0) * differenceLongitudes;
        return Math.sqrt(differenceLatitudesKM * differenceLatitudesKM + differenceLongitudesKM * differenceLongitudesKM);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningLocation)) return false;
        PlanningLocation other = (PlanningLocation) o;
        return this.latitude == other.latitude && this.longitude == other.longitude;
    }

}
