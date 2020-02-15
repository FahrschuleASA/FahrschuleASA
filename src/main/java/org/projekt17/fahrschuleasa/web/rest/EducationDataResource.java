package org.projekt17.fahrschuleasa.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.EducationDataService;
import org.projekt17.fahrschuleasa.service.dto.EducationDataDTO;
import org.projekt17.fahrschuleasa.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EducationDataResource {

    private final Logger log = LoggerFactory.getLogger(EducationDataResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EducationDataService educationDataService;

    public EducationDataResource(EducationDataService educationDataService) {
        this.educationDataService = educationDataService;
    }

    /**
     * {@code GET /education-data} : returns the educational data of the currently logged in student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the EducationDataDTO,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/education-data")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<EducationDataDTO> getEducationDataForStudent() {
        log.debug("REST request to get education data for current logged in student");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(educationDataService::getEducationDataForStudent));
    }

    /**
     * {@code GET /education-data/{id}} : get the educational data of a student
     *
     * @param id the student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the EducationDataDTO,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/education-data/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<EducationDataDTO> getEducationDataByStudentId(@PathVariable Long id) {
        log.debug("REST request to get education data for Student {}", id);
        return ResponseUtil.wrapOrNotFound(educationDataService.getEducationDataByStudentId(id));
    }

    /**
     * {@code PUT /education-data} : updates the education data of an existing student
     *
     * @param educationDataDTO the educationDTO containing the updates
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated EducationDataDTO,
     *         or with status {@code 400 (BadRequest)},
     *         or with status {@code 404 (Not Found)}.
     */
    @PutMapping("/education-data")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<EducationDataDTO> updateEducationData(@RequestBody EducationDataDTO educationDataDTO) {
        log.debug("REST request to update education data for Student {}", educationDataDTO.getStudentId());
        if (educationDataDTO.getStudentId() == null)
            throw new BadRequestAlertException("Id of Student must not be null", "educationData", "idisnull");
        if (educationDataDTO.getTeacherId() == null)
            throw new BadRequestAlertException("Id of Teacher must not be null", "educationData", "idisnull");
        return ResponseUtil.wrapOrNotFound(educationDataService.updateEducationData(educationDataDTO));
    }
}
