package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Location;

public class LocationDTO {

    private Long id;

    private Double longitude;

    private Double latitude;

    private String town;

    private String street;

    private String postal;

    private Integer houseNumber;

    private String country;

    private String additional;

    public LocationDTO(){}

    public LocationDTO(Location location){
        id = location.getId();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        town = location.getTown();
        street = location.getStreet();
        postal = location.getPostal();
        houseNumber = location.getHouseNumber();
        country = location.getCountry();
        additional = location.getAdditional();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    @Override
    public String toString() {
        return "LocationDTO{" +
            "id=" + id +
            ", longitude=" + longitude +
            ", latitude=" + latitude +
            ", town='" + town + '\'' +
            ", street='" + street + '\'' +
            ", postal='" + postal + '\'' +
            ", houseNumber=" + houseNumber +
            ", country='" + country + '\'' +
            ", additional='" + additional + '\'' +
            '}';
    }
}
