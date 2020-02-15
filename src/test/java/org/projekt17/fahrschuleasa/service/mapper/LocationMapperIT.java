package org.projekt17.fahrschuleasa.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.service.dto.LocationDTO;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class LocationMapperIT {

    private Long id = 0L;
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private String town = "sb";
    private String street = "brauerstrasse";
    private String postal = "66692";
    private Integer houseNumber = 51;
    private String country = "germany";
    private String additional = "test";

    private Location location;
    private LocationDTO locationDTO;

    private Location locationTest;
    private LocationDTO locationDTOTest;

    @BeforeEach
    public void init(){
        location = new Location();
        location.setId(id);
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        location.setTown(town);
        location.setStreet(street);
        location.setPostal(postal);
        location.setHouseNumber(houseNumber);
        location.setCountry(country);
        location.setAdditional(additional);
        locationDTO = new LocationDTO(location);
    }

    @Test
    public void simpleTestLocationtoLocatioDto(){

        locationDTOTest = LocationMapper.INSTANCE.toDto(location);

        assertThat(locationDTOTest).isNotNull();
        assertThat(locationDTOTest.getId()).isEqualTo(id);
        assertThat(locationDTOTest.getLatitude()).isEqualTo(latitude);
        assertThat(locationDTOTest.getLongitude()).isEqualTo(longitude);
        assertThat(locationDTOTest.getTown()).isEqualTo(town);
        assertThat(locationDTOTest.getStreet()).isEqualTo(street);
        assertThat(locationDTOTest.getPostal()).isEqualTo(postal);
        assertThat(locationDTOTest.getHouseNumber()).isEqualTo(houseNumber);
        assertThat(locationDTOTest.getCountry()).isEqualTo(country);
        assertThat(locationDTOTest.getAdditional()).isEqualTo(additional);
    }

    @Test
    public void simpleTestLocationDtoToLocation(){

        locationTest = LocationMapper.INSTANCE.toEntity(locationDTO);

        assertThat(locationTest).isNotNull();
        assertThat(locationTest.getId()).isEqualTo(id);
        assertThat(locationTest.getLatitude()).isEqualTo(latitude);
        assertThat(locationTest.getLongitude()).isEqualTo(longitude);
        assertThat(locationTest.getTown()).isEqualTo(town);
        assertThat(locationTest.getStreet()).isEqualTo(street);
        assertThat(locationTest.getPostal()).isEqualTo(postal);
        assertThat(locationTest.getHouseNumber()).isEqualTo(houseNumber);
        assertThat(locationTest.getCountry()).isEqualTo(country);
        assertThat(locationTest.getAdditional()).isEqualTo(additional);
    }
}
