package org.projekt17.fahrschuleasa.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.StudentService;
import org.projekt17.fahrschuleasa.service.dto.SmallStudentDTO;
import org.projekt17.fahrschuleasa.service.dto.StudentDTO;
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

/**
 * REST controller for managing {@link org.projekt17.fahrschuleasa.domain.Student}.
 */
@RestController
@RequestMapping("/api")
public class StudentResource {

    private final Logger log = LoggerFactory.getLogger(StudentResource.class);

    private static final String ENTITY_NAME = "student";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StudentService studentService;

    public StudentResource(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * {@code POST  /students/create} : Create a new student.
     *
     * @param studentDTO the student to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new student,
     *         or with status {@code 400 (Bad Request)} if the student has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/students/create")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) throws URISyntaxException {
        log.debug("REST request to save Student : {}", studentDTO);
        if (studentDTO.getId() != null) {
            throw new BadRequestAlertException("A new student cannot already have an ID", ENTITY_NAME, "idexists");
        } else if (studentDTO.getUser().getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
        } else {
            try {
                StudentDTO newStudent = studentService.createStudent(studentDTO);
                return ResponseEntity.created(new URI("/api/students/" + newStudent.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        newStudent.getUser().getFirstName() + " " + newStudent.getUser().getLastName()))
                    .body(newStudent);
            } catch (IllegalArgumentException ignore) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * {@code PUT  /students} : Updates personal information of an existing student.
     *
     * @param studentDTO the student to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated student,
     *         or with status {@code 400 (Bad Request)} if the student is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the student couldn't be updated.
     */
    @PutMapping("/students")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<StudentDTO> updateStudent(@Valid @RequestBody StudentDTO studentDTO) {
        log.debug("REST request to update Student : {}", studentDTO);
        if (studentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        try {
            Optional<StudentDTO> updatedStudent = studentService.updateStudent(studentDTO);
            return ResponseUtil.wrapOrNotFound(updatedStudent, HeaderUtil.createEntityUpdateAlert(applicationName,
                true, ENTITY_NAME,
                studentDTO.getUser().getFirstName() + " " + studentDTO.getUser().getLastName()));
        } catch (IllegalArgumentException ignore) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * {@code PUT /students/settings} : updates the settings of the currently logged in student
     *
     * @param studentDTO the studentDTO containing the new settings
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated student,
     *         or with status {@code 404 (NotFound)} if the student is not valid.
     */
    @PutMapping("/students/settings")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<StudentDTO> updateStudentSettings(@RequestBody StudentDTO studentDTO) {
        log.debug("REST request to update settings of student");

        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> studentService.updateStudentSettings(studentDTO, login)));
    }

    /**
     * {@code GET  /students} : get all the students.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of students in body.
     */
    @GetMapping("/students")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public List<StudentDTO> getAllStudents(@RequestParam(name = "active_only", defaultValue = "true") boolean activeOnly) {
        log.debug("REST request to get all Students");
        return studentService.getAllStudents(activeOnly);
    }

    /**
     * {@code GET  /students/small} : get all the students.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of students in body.
     */
    @GetMapping("/students/small")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public List<SmallStudentDTO> getAllStudentsSmall(@RequestParam(name = "active_only", defaultValue = "true") boolean activeOnly) {
        log.debug("REST request to get all Students(small)");
        return studentService.getAllStudentsSmall(activeOnly);
    }

    /**
     * {@code GET /students/teacher} : returns all active or not active students of the currently logged in teacher
     *
     * @param activeOnly whether you only want to receive activated users or not
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the List<StudentDTO>containing the students in body,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/students/teacher")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<StudentDTO>> getAllStudentsForTeacher(@RequestParam(name = "active_only", defaultValue = "true") boolean activeOnly) {
        log.debug("REST request to get all Students for a Teacher");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .map(login -> studentService.getAllStudentsForTeacher(login, activeOnly)));
    }

    /**
     * {@code GET  /students/:id} : get the "id" student.
     *
     * @param id the id of the student to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the student,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/students/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable Long id) {
        log.debug("REST request to get Student : {}", id);
        return ResponseUtil.wrapOrNotFound(studentService.getStudentById(id));
    }

    /**
     * {@code GET /student} : returns the currently logged in student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the student,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/student")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<StudentDTO> getStudentForStudent() {
        log.debug("REST request to get current Student");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin().flatMap(studentService::getStudentByLogin));
    }

    /**
     * {@code DELETE  /students/:id} : delete the "id" student.
     *
     * @param id the id of the student to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/students/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.debug("REST request to delete Student : {}", id);
        String name = studentService.deleteStudent(id);
        return ResponseEntity.noContent().headers(HeaderUtil
            .createEntityDeletionAlert(applicationName, true, ENTITY_NAME, name)).build();
    }
}
