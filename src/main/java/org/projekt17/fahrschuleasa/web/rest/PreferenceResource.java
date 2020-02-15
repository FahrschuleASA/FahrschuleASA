package org.projekt17.fahrschuleasa.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.PreferenceService;
import org.projekt17.fahrschuleasa.service.dto.PreferenceDTO;
import org.projekt17.fahrschuleasa.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.projekt17.fahrschuleasa.domain.Preference}.
 */
@RestController
@RequestMapping("/api")
public class PreferenceResource {

    private final Logger log = LoggerFactory.getLogger(PreferenceResource.class);

    private static final String ENTITY_NAME = "preference";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PreferenceService preferenceService;

    public PreferenceResource(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    /**
     * {@code POST  /preferences} : Create a new preference.
     *
     * @param preferenceDTO the preference to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new preference, or with status {@code 400 (Bad Request)} if the preference has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/preferences")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<PreferenceDTO> createPreference(@RequestBody PreferenceDTO preferenceDTO) throws URISyntaxException {
        log.debug("REST request to save a new Preference : {}", preferenceDTO);
        if (preferenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new preference cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (preferenceDTO.getTimeSlotId() == null) {
            throw new BadRequestAlertException("Id for TimeSlot is missing", ENTITY_NAME, "idmissing");
        }
        Optional<PreferenceDTO> createdPreference = SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> preferenceService.createPreference(preferenceDTO, login));
        if (createdPreference.isPresent())
            return ResponseEntity.created(new URI("/api/preferences/" + createdPreference.get().getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                    createdPreference.get().getId().toString()))
                .body(createdPreference.get());

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * {@code PUT  /preferences} : Updates an existing preference.
     *
     * @param preferenceDTO the preference to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated preference,
     * or with status {@code 400 (Bad Request)} if the preference is not valid,
     * or with status {@code 500 (Internal Server Error)} if the preference couldn't be updated.
     */
    @PutMapping("/preferences")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<PreferenceDTO> updatePreference(@RequestBody PreferenceDTO preferenceDTO) {
        log.debug("REST request to update Preference : {}", preferenceDTO);
        if (preferenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Optional<Boolean> isPreferenceOfStudent = SecurityUtils.getCurrentUserLogin()
            .map(login -> preferenceService.isPreferenceOfStudent(preferenceDTO.getId(), login));

        if (isPreferenceOfStudent.isPresent()) {
            if (!isPreferenceOfStudent.get())
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseUtil.wrapOrNotFound(preferenceService.updatePreference(preferenceDTO),
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                preferenceDTO.getId().toString()));
    }

    /**
     * {@code GET  /preferences} : get all the preferences.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of preferences in body.
     */
    @GetMapping("/preferences")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<List<PreferenceDTO>> getAllPreferencesForStudent() {
        log.debug("REST request to get all Preferences");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .map(preferenceService::getAllPreferencesForStudentLogin));
    }

    @GetMapping("/preferences/teacher/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public List<PreferenceDTO> getAllPreferencesForStudentId(@PathVariable Long id) {
        log.debug("REST request to get all Preferences for Student : {}", id);
        return preferenceService.getAllPreferencesForStudentId(id);
    }

    /**
     * {@code GET  /preferences/:id} : get the "id" preference.
     *
     * @param id the id of the preference to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the preference, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/preferences/{id}")
    public ResponseEntity<PreferenceDTO> getPreference(@PathVariable Long id) {
        log.debug("REST request to get Preference : {}", id);
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT)) {
            Optional<Boolean> isPreferenceOfStudent = SecurityUtils.getCurrentUserLogin()
                .map(login -> preferenceService.isPreferenceOfStudent(id, login));

            if (isPreferenceOfStudent.isPresent()) {
                if (!isPreferenceOfStudent.get())
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                else
                    return ResponseUtil.wrapOrNotFound(preferenceService.getPreference(id));
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseUtil.wrapOrNotFound(preferenceService.getPreference(id));
    }

    /**
     * {@code DELETE  /preferences/:id} : delete the "id" preference.
     *
     * @param id the id of the preference to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/preferences/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        log.debug("REST request to delete Preference : {}", id);
        Optional<Boolean> isPreferenceOfStudent = SecurityUtils.getCurrentUserLogin()
            .map(login -> preferenceService.isPreferenceOfStudent(id, login));

        if (isPreferenceOfStudent.isPresent()) {
            if (!isPreferenceOfStudent.get())
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        preferenceService.deletePreference(id, SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new IllegalArgumentException("Current login not found")));

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName,
            true, ENTITY_NAME, id.toString())).build();
    }
}
