package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.repository.LocationRepository;
import org.projekt17.fahrschuleasa.service.dto.NominatimDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class LocationService {

    Logger log = LoggerFactory.getLogger(LocationService.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location createLocation(Location location) {
        if (location == null)
            return null;

        List<Location> locations = locationRepository.findAll();
        for(Location l : locations) {
            try {
                if (l.checkSimilar(location)) {
                    return l;
                }
            } catch (NullPointerException ignore) {}
        }

        Location newLocation = new Location();

        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", applicationName);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        String search = location.getCountry() + " " + location.getPostal() + " " + location.getTown() + " " +
            location.getStreet() + " " + location.getHouseNumber();
        ResponseEntity<NominatimDTO[]> response = restTemplate.exchange(
            "https://nominatim.openstreetmap.org/search?q={search}&format=json", HttpMethod.GET, entity, NominatimDTO[].class, search);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().length == 0) {
            newLocation.setLatitude(49.343059);
            newLocation.setLongitude(6.923742);
            log.error("No information for location {} found! Fall back to default values {} {}",
                location, newLocation.getLatitude(), newLocation.getLongitude());
        } else {
            newLocation.setLongitude(response.getBody()[0].getLon());
            newLocation.setLatitude(response.getBody()[0].getLat());
        }

        newLocation.setStreet(location.getStreet());
        newLocation.setTown(location.getTown());
        newLocation.setPostal(location.getPostal());
        newLocation.setHouseNumber(location.getHouseNumber());
        newLocation.setCountry(location.getCountry());
        newLocation.setAdditional(location.getAdditional());
        locationRepository.save(newLocation);

        return newLocation;
    }
}
