package org.projekt17.fahrschuleasa.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimDTO {

    private double lat;

    private double lon;

    private String display_name;

    public NominatimDTO() {}

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    @Override
    public String toString() {
        return "NominatimDTO{" +
            "lat=" + lat +
            ", lon=" + lon +
            ", display_name='" + display_name + '\'' +
            '}';
    }
}
