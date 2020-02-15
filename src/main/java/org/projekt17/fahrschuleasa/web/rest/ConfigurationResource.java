package org.projekt17.fahrschuleasa.web.rest;

import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.service.AdministratorService;
import org.projekt17.fahrschuleasa.service.dto.SchoolConfigurationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

/**
 * REST controller for managing {@link org.projekt17.fahrschuleasa.config.SchoolConfiguration}.
 */
@RestController
@RequestMapping("/api")
public class ConfigurationResource {

    private final Logger logger = LoggerFactory.getLogger(ConfigurationResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdministratorService administratorService;

    public ConfigurationResource(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    /**
     * {@code GET /configuration} : returns the schoolConfiguration
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the schoolConfiguration.
     */
    @GetMapping("/configuration")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public SchoolConfigurationDTO getConfiguration() {
        logger.debug("REST request to get configuration");
        return new SchoolConfigurationDTO();
    }

    /**
     * {@code GET /driving-categories} : returns all in the School available drivingCategories
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the Set<AvailableCategories>.
     */
    @GetMapping("/driving-categories")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public Set<DrivingCategory> getAvailableDrivingCategories() {
        logger.debug("REST request to get configuration");
        return SchoolConfiguration.getAvailableCategories();
    }

    /**
     * {@code GET /configuration/max-inactive} : returns the maximal amount of time a user cann be inactive
     *
     * @return
     */
    @GetMapping("/configuration/max-inactive")
    public Integer getMaxInactive() {
        logger.debug("REST request to get maxInactive value");
        return SchoolConfiguration.getMaxInactive();
    }

    /**
     * {@code PUT /configuration} : updates the schoolConfiguration
     *
     * @param schoolConfigurationDTO, the dto containing the new values
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated schoolConfiguration.
     */
    @PutMapping("/configuration")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<SchoolConfigurationDTO> updateConfiguration(@Valid @RequestBody SchoolConfigurationDTO schoolConfigurationDTO) {
        logger.debug("REST request to update configuration : {}", schoolConfigurationDTO);
        SchoolConfigurationDTO newConfig = administratorService.updateConfiguration(schoolConfigurationDTO);

        return ResponseEntity.ok().body(newConfig);
    }
}
