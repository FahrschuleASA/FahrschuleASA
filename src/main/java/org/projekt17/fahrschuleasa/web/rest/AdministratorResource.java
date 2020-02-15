package org.projekt17.fahrschuleasa.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.AdministratorService;
import org.projekt17.fahrschuleasa.service.UserService;
import org.projekt17.fahrschuleasa.service.dto.AdministratorDTO;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AdministratorResource {

    private final Logger log = LoggerFactory.getLogger(AdministratorResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final String ENTITY_NAME = "adminManagement";

    private final AdministratorService administratorService;

    private final UserService userService;

    public AdministratorResource(AdministratorService administratorService, UserService userService) {
        this.administratorService = administratorService;
        this.userService = userService;

    }

    /**
     * {@code POST  /admins/create} : Create a new administrator.
     *
     * @param administratorDTO the administrator to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new administrator,
     *         or with status {@code 400 (Bad Request)} if the teacher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/admins/create")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdministratorDTO> createAdministrator(@Valid @RequestBody AdministratorDTO administratorDTO)
        throws URISyntaxException {

        log.debug("REST request to create Administrator : {}", administratorDTO);
        if (administratorDTO.getId() != null) {
            throw new BadRequestAlertException("A new administrator cannot already have an ID", "administrator", "idexists");
        } else if (administratorDTO.getUser().getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "administrator", "idexists");
        } else {
            AdministratorDTO newAdministrator = administratorService.createAdministrator(administratorDTO);
            return ResponseEntity.created(new URI("/api/admins/create" + newAdministrator.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true,
                    ENTITY_NAME, newAdministrator.getUser().getFirstName() + " " + newAdministrator.getUser().getLastName()))
                .body(newAdministrator);
        }
    }

    /**
     * {@code PUT /administrators} : updates an existing administrator
     *
     * @param myAccountDTO containing the new data about the administrator
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the updated administrator,
     *         or with status {@code 400 (Bad Request)},
     *         or with status {@code 404 (NotFound}).
     */
    @PutMapping("/administrators")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<MyAccountDTO> updateAdministrator(@Valid @RequestBody MyAccountDTO myAccountDTO) {
        log.debug("REST request to update Administrator : {}", myAccountDTO);
        if (myAccountDTO.getId() == null)
            throw new BadRequestAlertException("Id of Administrator is missing", ENTITY_NAME, "idmissing");
        return ResponseUtil.wrapOrNotFound(userService.updateMyAccountAdmin(myAccountDTO),
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                myAccountDTO.getUser().getFirstName() + " " + myAccountDTO.getUser().getLastName()));
    }

    /**
     * {@code GET  /administrators} : get all the administrators.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of administrators in body.
     */
    @GetMapping("/administrators")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<AdministratorDTO> getAllAdministrators(@RequestParam(name = "active_only", defaultValue = "true") boolean activeOnly){
        log.debug("REST request to get all Administrators");
        return administratorService.getAllAdministrators(activeOnly);
    }

    /**
     * {@code GET /administrators/{id}} : returns a administrator
     *
     * @param id of the administrator
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the administrator,
     *         or with status {@code 404 (NotFound}).
     */
    @GetMapping("/administrators/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdministratorDTO> getAdministratorById(@PathVariable Long id){
        log.debug("REST request to get Administrator {}", id);
        return ResponseUtil.wrapOrNotFound(administratorService.getAdministratorById(id));
    }

    /**
     * {@code GET /administrator} : returns the currently logged in administrator
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the administrator,
     *         or with status {@code 404 (NotFound}).
     */
    @GetMapping("/administrator")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdministratorDTO> getCurrentAdministrator() {
        log.debug("REST request to get current Administrator");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin().flatMap(administratorService::getAdministratorByLogin));
    }

    /**
     * {@code DELETE  /administrators/:id} : delete the "id" administrator.
     *
     * @param id the id of the administrator to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/administrators/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<?> deleteAdministrator(@PathVariable Long id){
        log.debug("REST request to delete Administrator : {}", id);
        Optional<String> name;
        try {
            name = SecurityUtils.getCurrentUserLogin().map(login -> administratorService.deleteAdministrator(id, login));
        } catch (IllegalStateException ignore) {
            throw new BadRequestAlertException("Administrator could not delete himself!", "adminManagement", "admindeleteself");
        }
        return name.map(adminName -> ResponseEntity.noContent().headers(HeaderUtil
            .createEntityDeletionAlert(applicationName, true, ENTITY_NAME, adminName)
        ).build()).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
