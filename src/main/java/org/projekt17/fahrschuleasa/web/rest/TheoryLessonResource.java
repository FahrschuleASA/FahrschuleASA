package org.projekt17.fahrschuleasa.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.TheoryLessonService;
import org.projekt17.fahrschuleasa.service.dto.TheoryLessonDTO;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link org.projekt17.fahrschuleasa.domain.TheoryLesson}.
 */
@RestController
@RequestMapping("/api")
public class TheoryLessonResource {

    private final Logger log = LoggerFactory.getLogger(TheoryLessonResource.class);

    private static final String ENTITY_NAME = "theoryLesson";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TheoryLessonService theoryLessonService;

    public TheoryLessonResource(TheoryLessonService theoryLessonService) {
        this.theoryLessonService = theoryLessonService;
    }

    /**
     * {@code POST  /theory-lessons} : Create a new theoryLesson.
     *
     * @param theoryLessonDTO the theoryLesson to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new theoryLesson, or with status {@code 400 (Bad Request)} if the theoryLesson has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/theory-lessons")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TheoryLessonDTO> createTheoryLesson(@RequestBody TheoryLessonDTO theoryLessonDTO) throws URISyntaxException {
        log.debug("REST request to save TheoryLesson : {}", theoryLessonDTO);
        if (theoryLessonDTO.getId() != null) {
            throw new BadRequestAlertException("A new theoryLesson cannot already have an ID", ENTITY_NAME, "idexists");
        } else if(theoryLessonDTO.getBegin() == null || theoryLessonDTO.getEnd() == null){
            throw new BadRequestAlertException("For creating a new theoryLesson their must exist a begin and end", ENTITY_NAME, "beginendnotexist");
        } else {
            Optional<TheoryLessonDTO> result;
            if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.TEACHER)) {
                result = SecurityUtils.getCurrentUserLogin()
                    .flatMap(login -> theoryLessonService.createTheoryLesson(theoryLessonDTO, login));
            } else {
                if (theoryLessonDTO.getTeacherId() == null) {
                    throw new BadRequestAlertException("If an administrator wants to create a theoryLesson, the teacher must have an id", ENTITY_NAME, "idnotexists");
                }
                result = theoryLessonService.createTheoryLesson(theoryLessonDTO);
            }
            if (result.isPresent()) {
                return ResponseEntity.created(new URI("/api/theory-lessons/" + result.get().getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.get().getId().toString()))
                    .body(result.get());
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * {@code PUT /theory-lessons/change-subject} : chenge the subject of the theoryLesson with id lessonId
     *
     * @param lessonId the theoryLesson to change the subject
     * @param subject the new subject of the theoryLesson
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the new theoryLesson containing a new subject,
     *         or with status {@code 400 (Bad Request)} if lessonId was null,
     *         or with status {@code 404 (NotFound)} if the theoryLesson with lessonId does not exist.
     */
    @PutMapping("/theory-lessons/change-subject")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TheoryLessonDTO> changeTheoryLessonSubject(@RequestParam(value = "lessonId") Long lessonId,
                                                                     @RequestParam(value = "subject") String subject) {
        log.debug("REST request to change theoryLesson {} subject", lessonId);
        if (lessonId == null) {
            throw new BadRequestAlertException("Invalid theoryLessonId", ENTITY_NAME, "idnull");
        }
        return ResponseUtil.wrapOrNotFound(theoryLessonService.changeTheoryLessonSubject(lessonId, subject),
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonId.toString()));
    }

    /**
     * {@code PUT /theory-lessons/add-student} : Add an existing student to an existing theoryLesson.
     *
     * @param lessonId  the theoryLesson to which the student will be added
     * @param studentId the student to add to the theoryLesson
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the new theoryLesson,
     *         or with status {@code 400 (Bad Request)} if the theoryLesson or the student had no ID.
     */
    @PutMapping("/theory-lessons/add-student")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TheoryLessonDTO> addStudentToTheoryLesson(@RequestParam(value = "lessonId") Long lessonId,
                                                                    @RequestParam(value = "studentId") Long studentId) {
        log.debug("REST request to add Student : {} to TheoryLesson {}", studentId, lessonId);
        if (lessonId == null || studentId == null) {
            throw new BadRequestAlertException("Invalid studentId or theoryLessonId", ENTITY_NAME + " student", "idnull");
        }
        return ResponseUtil.wrapOrNotFound(theoryLessonService.addStudentToTheoryLesson(lessonId, studentId));
    }

    /**
     * {@code PUT /theory-lessons/add-student} : Remove an existing student from an existing theoryLesson.
     *
     * @param lessonId  the theoryLesson from which the student will be removed
     * @param studentId the student to remove from the theoryLesson
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the new theoryLesson, or with status {@code 400 (Bad Request)} if the theoryLesson or the student had no ID.
     */
    @PutMapping("/theory-lessons/remove-student")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TheoryLessonDTO> removeStudentFromTheoryLesson(@RequestParam(value = "lessonId") Long lessonId,
                                                                         @RequestParam(value = "studentId") Long studentId) {
        log.debug("REST request to remove Student : {} from TheoryLesson {}", studentId, lessonId);
        if (lessonId == null || studentId == null) {
            throw new BadRequestAlertException("Invalid studentId or theoryLessonId", ENTITY_NAME + " student", "idnull");
        }
        return ResponseUtil.wrapOrNotFound(theoryLessonService.removeStudentFromTheoryLessson(lessonId, studentId));
    }

    /**
     * {@code GET  /theory-lessons} : get all the theoryLessons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of theoryLessons in body.
     */
    @GetMapping("/theory-lessons")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public List<TheoryLessonDTO> getAllTheoryLessons() {
        log.debug("REST request to get all TheoryLessons");
        return theoryLessonService.getAllTheoryLessons();
    }

    /**
     * {@code GET  /lessons/theory-lessons/{id}} : get the theoryLesson with given id.
     *
     * @param id of the wanteed theoryLesson.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the theoryLesson in body.
     */
    @GetMapping("/theory-lessons/{id}")
    public ResponseEntity<TheoryLessonDTO> getTheoryLesson(@PathVariable Long id) {
        log.debug("REST request to get TheoryLesson : {}", id);
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT))
            return ResponseUtil.wrapOrNotFound(theoryLessonService.getTheoryLesson(id).map(theoryLessonDTO -> {
                theoryLessonDTO.setStudents(new HashSet<>());
                return theoryLessonDTO;
            }));
        return ResponseUtil.wrapOrNotFound(theoryLessonService.getTheoryLesson(id));
    }

    /**
     * {@code GET  /lessons/theory-lessons/:id} : get all theoryLessons with Teacher "id".
     *
     * @param id the id of the Teacher.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the theoryLesson,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/theory-lessons/teacher/{id}")
    public List<TheoryLessonDTO> getAllTheoryLessonsByTeacherId(@PathVariable Long id) {
        log.debug("REST request to get all TheoryLessons by Teacher : {}", id);
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT))
            return theoryLessonService.getAllTheoryLessonsByTeacherId(id).stream()
                .peek(theoryLessonDTO -> theoryLessonDTO.setStudents(new HashSet<>())).collect(Collectors.toList());
        return theoryLessonService.getAllTheoryLessonsByTeacherId(id);
    }

    @GetMapping("/theory-lessons/teacher")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<TheoryLessonDTO>> getAllTheoryLessonsByTeacherLogin() {
        log.debug("REST request to get all TheoryLessons by current logged in Teacher");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .map(theoryLessonService::getAllTheoryLessonsByTeacherLogin));
    }

    /**
     * {@code GET /theory-lessons/student} : returns the theoryLessons of the currently logged in student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<TheoryLessonDTO> containing no students,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/theory-lessons/student")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<List<TheoryLessonDTO>> getAllTheoryLessonsByStudentLogin() {
        log.debug("REST request to get all TheoryLessons visited by current logged in Student");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> theoryLessonService.getAllTheoryLessonsByStudentLogin(login).map(theoryLessonDTOS -> {
                theoryLessonDTOS.forEach(theoryLessonDTO -> theoryLessonDTO.setStudents(new HashSet<>()));
                return theoryLessonDTOS;
            })));
    }

    /**
     * {@code GET /theory-lessons/future/{id}} : get all future theoryLessons of teacher with id
     *
     * @param id the teacher
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<TheoryLessonDTO>,
     *         (with each lesson in list containing the students of the lesson if the current logged in user is a teacher
     *         or no students if the current logged in user is a student),
     *         or with status {@code 404 (Not Found)}
     */
    @GetMapping("/theory-lessons/future/{id}")
    public List<TheoryLessonDTO> getAllFutureTheoryLessonsByTeacherId(@PathVariable Long id) {
        log.debug("REST request to get all TheoryLessons visited by Student {}", id);
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT))
            return theoryLessonService.getAllFutureTheoryLessonsByTeacherId(id).stream()
                .peek(theoryLessonDTO -> theoryLessonDTO.setStudents(new HashSet<>())).collect(Collectors.toList());
        return theoryLessonService.getAllFutureTheoryLessonsByTeacherId(id);
    }

    /**
     * {@code GET /theory-lessons/student/{id}} : get all theoryLessons of student with id
     *
     * @param id the student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<TheoryLessonDTO>,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/theory-lessons/student/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<TheoryLessonDTO>> getAllTheoryLessonsByStudentId(@PathVariable Long id) {
        log.debug("REST request to get all TheoryLessons visited by Student {}", id);
        return ResponseUtil.wrapOrNotFound(theoryLessonService.getAllTheoryLessonsByStudentId(id));
    }

    /**
     * {@code DELETE  /theory-lessons/:id} : delete the "id" theoryLesson.
     *
     * @param id the id of the theoryLesson to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/theory-lessons/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<Void> deleteTheoryLesson(@PathVariable Long id) {
        log.debug("REST request to delete TheoryLesson : {}", id);
        theoryLessonService.deleteTheoryLesson(id);
        return ResponseEntity.noContent().headers(HeaderUtil
            .createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
