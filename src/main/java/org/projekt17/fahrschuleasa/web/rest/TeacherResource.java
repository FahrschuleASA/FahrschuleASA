package org.projekt17.fahrschuleasa.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.TeacherService;
import org.projekt17.fahrschuleasa.service.UserService;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.service.dto.TeacherDTO;
import org.projekt17.fahrschuleasa.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.projekt17.fahrschuleasa.domain.Teacher}.
 */
@RestController
@RequestMapping("/api")
public class TeacherResource {

    private final Logger log = LoggerFactory.getLogger(TeacherResource.class);

    private static final String ENTITY_NAME = "teacher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeacherService teacherService;

    private final UserService userService;

    public TeacherResource(TeacherService teacherService, UserService userService) {
        this.teacherService = teacherService;
        this.userService = userService;
    }

    /**
     * {@code POST  /teachers} : Create a new teacher.
     *
     * @param teacherDTO the teacher to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teacher,
     *         or with status {@code 400 (Bad Request)} if the teacher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/teachers/create")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<TeacherDTO> createTeacher(@Valid @RequestBody TeacherDTO teacherDTO) throws URISyntaxException {
        log.debug("REST request to create Teacher : {}", teacherDTO);
        if (teacherDTO.getId() != null) {
            throw new BadRequestAlertException("A new teacher cannot already have an ID", ENTITY_NAME, "idexists");
        } else if (teacherDTO.getUser().getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
        } else {
            TeacherDTO newTeacher = teacherService.createTeacher(teacherDTO);
            return ResponseEntity.created(new URI("/api/teachers/create" + newTeacher.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true,
                    ENTITY_NAME, newTeacher.getUser().getFirstName() + " " + newTeacher.getUser().getLastName()))
                .body(newTeacher);
        }
    }

    /**
     * {@code PUT /teachers} : updates the personal information of a teacher
     *
     * @param myAccountDTO the myAccountDTO containing the new personal information
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the updated teacher,
     *         or with status {@code 404 (NotFound)} if the teacher doesn't exist.
     */
    @PutMapping("/teachers")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<MyAccountDTO> updateTeacher(@Valid @RequestBody MyAccountDTO myAccountDTO) {
        log.debug("REST request to update Teacher : {}", myAccountDTO);
        if (myAccountDTO.getId() == null)
            throw new BadRequestAlertException("Id of Teacher is missing", ENTITY_NAME, "idmissing");
        return ResponseUtil.wrapOrNotFound(userService.updateMyAccountTeacher(myAccountDTO),
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                myAccountDTO.getUser().getFirstName() + " " + myAccountDTO.getUser().getLastName()));
    }

    /**
     * {@code PUT /teachers} : updates a teachers settings, of the current logged in teacher,
     *                         or the teacher with given id
     *
     * @param myAccountDTO the myAccountDTO containing the new settings
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the updated teacher,
     *         or with status {@code 404 (NotFound)} if the teacher doesn't exist,
     *         or with status {@code 400 (Bad Request)} if the teacher has no ID and the current user is admin.
     */
    @PutMapping("/teachers/settings")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<MyAccountDTO> updateTeacherSettings(@Valid @RequestBody MyAccountDTO myAccountDTO) {
        log.debug("REST request to update settings of Teacher : {}", myAccountDTO);
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.TEACHER))
            return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
                .flatMap(login -> teacherService.updateTeacherSettings(myAccountDTO, login)));

        if (myAccountDTO.getId() == null)
            throw new BadRequestAlertException("Id of Teacher is missing", ENTITY_NAME, "idmissing");

        return ResponseUtil.wrapOrNotFound(teacherService.updateTeacherSettings(myAccountDTO));
    }

    /**
     * {@code GET  /teachers} : get all the teachers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teachers in body.
     */
    @GetMapping("/teachers")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<TeacherDTO>> getAllTeachers(@RequestParam(name = "active_only", defaultValue = "true") boolean activeOnly) {
        log.debug("REST request to get all Teachers");
        return ResponseUtil.wrapOrNotFound(Optional.of(teacherService.getAllTeachers(activeOnly)));
    }

    /**
     * {@code GET  /teachers/:id} : get the "id" teacher.
     *
     * @param id the id of the teacher to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teacher,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/teachers/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TeacherDTO> getTeacher(@PathVariable Long id) {
        log.debug("REST request to get Teacher : {}", id);
        return ResponseUtil.wrapOrNotFound(teacherService.getTeacher(id));
    }

    /**
     * {@code GET /teacher} : returns the currently logged in teacher
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teacher,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/teacher")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TeacherDTO> getCurrentTeacher() {
        log.debug("REST request to get current Teacher");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(teacherService::getTeacherByLogin));
    }

    /**
     * {@code GET /teacher/student} : returns the teacher of the currently logged in student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personal Information from
     *         the teacher of the student,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/teacher/student")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<MyAccountDTO> getTeacherForStudent() {
        log.debug("REST request to get Teacher of current logged in Student");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(teacherService::getTeacherForStudent));
    }

    /**
     * {@code DELETE  /teachers/:id} : delete the "id" teacher.
     *
     * @param id the id of the teacher to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/teachers/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        log.debug("REST request to delete Teacher : {}", id);
        String name = this.teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().headers(HeaderUtil
            .createEntityDeletionAlert(applicationName, true, ENTITY_NAME, name)).build();
    }
}
