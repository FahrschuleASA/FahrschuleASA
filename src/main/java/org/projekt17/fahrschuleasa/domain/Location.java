package org.projekt17.fahrschuleasa.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.projekt17.fahrschuleasa.service.dto.LocationDTO;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A Location.
 */
@Entity
@Table(name = "location")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "town")
    private String town;

    @Column(name = "street")
    private String street;

    @Column(name = "postal")
    private String postal;

    @Column(name = "house_number")
    private Integer houseNumber;

    @Column(name = "country")
    private String country;

    @Column(name = "additional")
    private String additional;

    public Location() {}

    public Location(LocationDTO locationDTO) {
        this.longitude = locationDTO.getLongitude();
        this.latitude = locationDTO.getLatitude();
        this.town = locationDTO.getTown();
        this.street = locationDTO.getStreet();
        this.postal = locationDTO.getPostal();
        this.houseNumber = locationDTO.getHouseNumber();
        this.country = locationDTO.getCountry();
        this.additional = locationDTO.getAdditional();
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Location longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Location latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getTown() {
        return town;
    }

    public Location town(String town) {
        this.town = town;
        return this;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public Location street(String street) {
        this.street = street;
        return this;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostal() {
        return postal;
    }

    public Location postal(String postal) {
        this.postal = postal;
        return this;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public Location houseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public Location country(String country) {
        this.country = country;
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdditional() {
        return additional;
    }

    public Location additional(String additional) {
        this.additional = additional;
        return this;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Location)) {
            return false;
        }
        return id != null && id.equals(((Location) o).id);
    }

    public boolean checkSimilar(Location location){
        if (this == location) {
            return true;
        }
        return this.additional.equals(location.additional)
            && this.country.equals(location.country)
            && this.houseNumber.equals(location.houseNumber)
            && this.postal.equals(location.postal)
            && this.street.equals(location.street)
            && this.town.equals(location.town);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Location{" +
            "id=" + getId() +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", town='" + getTown() + "'" +
            ", street='" + getStreet() + "'" +
            ", postal='" + getPostal() + "'" +
            ", houseNumber=" + getHouseNumber() +
            ", country='" + getCountry() + "'" +
            ", additional='" + getAdditional() + "'" +
            "}";
    }
}
