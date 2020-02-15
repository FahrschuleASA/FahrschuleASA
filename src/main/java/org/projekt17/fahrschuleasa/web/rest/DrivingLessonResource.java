package org.projekt17.fahrschuleasa.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.DrivingLessonService;
import org.projekt17.fahrschuleasa.service.dto.DrivingLessonDTO;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.projekt17.fahrschuleasa.domain.DrivingLesson}.
 */
@RestController
@RequestMapping("/api")
public class DrivingLessonResource {

    private final Logger log = LoggerFactory.getLogger(DrivingLessonResource.class);

    private static final String ENTITY_NAME = "drivingLesson";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DrivingLessonService drivingLessonService;

    public DrivingLessonResource(DrivingLessonService drivingLessonService) {
        this.drivingLessonService = drivingLessonService;
    }

    /**
     * {@code PUT /driving-lesson/book} : books a not booked driving lesson for a student
     *
     * @param drivingLessonDTO the drivingLesson to book
     *
     * @return  the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated DrivingLessonDTO,
     *          or with status {@code 400 (BadRequest)},
     *          or with status {@code 404 (NotFound)}.
     */
    @PutMapping("/driving-lesson/book")
    public ResponseEntity<DrivingLessonDTO> bookDrivingLesson(@RequestBody DrivingLessonDTO drivingLessonDTO) {
        log.debug("REST request to book DrivingLesson : {}", drivingLessonDTO);

        if (drivingLessonDTO.getId() == null)
            throw new BadRequestAlertException("We cannot book driving lesson 'null'", ENTITY_NAME, "idisnull");

        DrivingLessonDTO bookedLesson;

        try {
            if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT)) {
                Optional<String> login = SecurityUtils.getCurrentUserLogin();
                if (login.isPresent())
                    bookedLesson = drivingLessonService.bookDrivingLesson(drivingLessonDTO, login.get(), false);
                else
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else
                bookedLesson = drivingLessonService.bookDrivingLesson(drivingLessonDTO, "", true);
        } catch (IllegalArgumentException ignored) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException ignored) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().headers(HeaderUtil.createAlert(applicationName,
            ENTITY_NAME + ".booked", drivingLessonDTO.getId().toString())).body(bookedLesson);
    }

    /**
     * {@code PUT /driving-lesson/cancel} : cancels an currently booked drivingLesson, and if bookable is true
     *                                      message possible students of a new bookable driving lesson
     *
     * @param drivingLessonId the driving lesson to cancel
     * @param bookable whether possible students should get messaged
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated DrivingLessonDTO,
     *         or with status {@code 400 (BadRequest)},
     *         or with status {@code 404 (NotFound)}.
     */
    @PutMapping("/driving-lesson/cancel")
    public ResponseEntity<Void> cancelDrivingLesson(@RequestParam(name = "driving_lesson_id") Long drivingLessonId,
                                                    @RequestParam(name = "bookable", defaultValue = "true") boolean bookable) {
        log.debug("REST request to cancel DrivingLesson : {}", drivingLessonId);

        if (drivingLessonId == null)
            throw new BadRequestAlertException("We cannot cancel driving lesson 'null'", ENTITY_NAME, "idisnull");

        try {
            if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT)) {
                Optional<String> login = SecurityUtils.getCurrentUserLogin();
                if (login.isPresent())
                    drivingLessonService.cancelDrivingLesson(drivingLessonId, login.get(), false, bookable);
                else
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else
                drivingLessonService.cancelDrivingLesson(drivingLessonId, "", true, bookable);
        } catch (IllegalArgumentException ignored) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException ignored) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName,
            ENTITY_NAME + ".canceled", drivingLessonId.toString())).build();
    }

    /**
     * {@code POST  /driving-lessons} : Create a new drivingLesson.
     *
     * @param drivingLessonDTO the drivingLesson to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new drivingLesson, or with status {@code 400 (Bad Request)} if the drivingLesson has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/driving-lessons")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<DrivingLessonDTO> createDrivingLesson(@RequestBody DrivingLessonDTO drivingLessonDTO) throws URISyntaxException {
        log.debug("REST request to save DrivingLesson : {}", drivingLessonDTO);
        if (drivingLessonDTO.getId() != null) {
            throw new BadRequestAlertException("A new drivingLesson cannot already have an ID", ENTITY_NAME, "idexists");
        } else if (drivingLessonDTO.getBegin() == null || drivingLessonDTO.getEnd() == null) {
            throw new BadRequestAlertException("A driving lesson needs a begin and an end", ENTITY_NAME, "beginendnotexist");
        } else {
            LocalTime cutTime = LocalTime.of(6,0);
            long duration = drivingLessonDTO.getBegin().until(drivingLessonDTO.getEnd(), ChronoUnit.MINUTES);
            if (drivingLessonDTO.getBegin().toLocalTime().isBefore(cutTime) && drivingLessonDTO.getEnd().toLocalTime().isAfter(cutTime))
                throw new BadRequestAlertException("Bad begin and end time of DrivingLesson", "drivingLesson", "badbeginend");
            if (duration < 45 || duration > 180)
                throw new BadRequestAlertException(
                    String.format("Duration of a DrivingLesson should be between 45 and 180 minutes, but was %d", duration),
                    "timeSlot", "badduration");
            Optional<DrivingLessonDTO> createdDrivingLesson;
            if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.TEACHER))
                createdDrivingLesson = SecurityUtils.getCurrentUserLogin().flatMap(login -> drivingLessonService.addDrivingLesson(drivingLessonDTO, login));
            else {
                if (drivingLessonDTO.getTeacherId() == null)
                    throw new BadRequestAlertException("Id of teacher is missing", ENTITY_NAME, "idmissing");
                createdDrivingLesson = drivingLessonService.addDrivingLesson(drivingLessonDTO);
            }

            if (createdDrivingLesson.isPresent())
                return ResponseEntity.created(new URI("/api/driving-lessons/" + createdDrivingLesson.get().getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        createdDrivingLesson.get().getId().toString()))
                    .body(createdDrivingLesson.get());
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * {@code PUT  /driving-lessons} : Updates {@link DrivingLessonType}, pickup and destination {@link Location} of an
     * existing drivingLesson. If id of pickup/destination is not null, pickup/destination will not get updated.
     *
     * @param drivingLessonDTO the drivingLesson to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated drivingLesson,
     * or with status {@code 400 (Bad Request)} if the drivingLesson is not valid,
     * or with status {@code 500 (Internal Server Error)} if the drivingLesson couldn't be updated.
     */
    @PutMapping("/driving-lessons")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<DrivingLessonDTO> updateDrivingLesson(@RequestBody DrivingLessonDTO drivingLessonDTO) {
        log.debug("REST request to update DrivingLesson : {}", drivingLessonDTO);
        if (drivingLessonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        return ResponseUtil.wrapOrNotFound(drivingLessonService.updateDrivingLesson(drivingLessonDTO),
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, drivingLessonDTO.getId().toString()));
    }

    /**
     * {@code GET  /driving-lessons} : get all the drivingLessons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of drivingLessons in body.
     */
    @GetMapping("/driving-lessons")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public List<DrivingLessonDTO> getAllAssignedDrivingLessons() {
        log.debug("REST request to get all DrivingLessons");
        return drivingLessonService.getAllAssignedDrivingLessons();
    }

    /**
     * {@code GET  /driving-lessons/:id} : get the "id" drivingLesson.
     *
     * @param id the id of the drivingLesson to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the drivingLesson, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/driving-lessons/{id}")
    public ResponseEntity<DrivingLessonDTO> getDrivingLesson(@PathVariable Long id) {
        log.debug("REST request to get DrivingLesson : {}", id);

        //if a student requests this, check if the requested driving lesson belongs to the student
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT)) {
            Optional<Boolean> isDrivingLesson = SecurityUtils.getCurrentUserLogin().map(login -> drivingLessonService.isDrivingLessonOfStudent(login, id));
            if (isDrivingLesson.isPresent()) {
                if (!isDrivingLesson.get())
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseUtil.wrapOrNotFound(drivingLessonService.getDrivingLessonById(id));
    }

    /**
     * {@code GET /teacher/:id} : get the drivingLessons regarding to teacher "id"
     *
     * @param id of the teacher
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the Set<DrivingLessonDTO> of
     * the teacher with id, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/driving-lesson/teacher/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public List<DrivingLessonDTO> getAllAssignedDrivingLessonsByTeacherId(@PathVariable Long id) {
        log.debug("REST request to get all DrivingLessons of Teacher : {}", id);
        return drivingLessonService.getAllAssignedDrivingLessonsByTeacherId(id);
    }

    /**
     *
     * {@code GET /driving-lessons/student/{id}} : returns all drivingLessons of a student
     *
     * @param id of the student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the Set<DrivingLessonDTO> of the student,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/driving-lesson/student/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<DrivingLessonDTO>> getAllDrivingLessonsByStudentId(@PathVariable Long id) {
        log.debug("REST request to get all DrivingLessons of Student : {}", id);
        return ResponseUtil.wrapOrNotFound(drivingLessonService.getAllDrivingLessonsByStudentId(id));
    }

    /**
     * {@code GET /driving-lessons/student} : returns all drivingLessons of the currently logged in student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<DrivingLessonDTO> of the student,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/driving-lesson/student")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<List<DrivingLessonDTO>> getAllDrivingLessonsForStudent() {
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(drivingLessonService::getAllDrivingLessonsByStudentLogin));
    }

    /**
     * {@code GET /driving-lessons/teacher} : returns all assigned drivingLessons of the currently logged in teacher
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<DrivingLessonDTO> of the
     *         currently logged in teacher, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/driving-lesson/teacher")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<DrivingLessonDTO>> getAllAssignedDrivingLessonsForTeacher() {
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .map(drivingLessonService::getAllAssignedDrivingLessonsByTeacherLogin));
    }

    /**
     * {@code GET /driving-lessons/unassigned/{id}} : get all unassigned drivingLessons of a teacher
     *
     * @param id of the teacher
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<DrivingLessonDTO> of the teacher.
     */
    @GetMapping("/driving-lesson/unassigned/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<DrivingLessonDTO>> getAllUnassignedDrivingLessonsByTeacherId(@PathVariable Long id) {
        return ResponseEntity.ok(drivingLessonService.getAllUnassignedDrivingLessonsByTeacherId(id));
    }

    /**
     * {@code GET /driving-lessons/unassigned} : returns all unassigned drivingLessons,
     *                                           or all unassigned drivingLessons of a teacher,
     *                                           or all unassigned drivingLessons of a students teacher.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<DrivingLessonDTO> with unassigned drivingLessons,
     *         or with status {@code 404 (NotFound)},
     *         or with status {@code 400 (BadRequest)}.
     */
    @GetMapping("/driving-lesson/unassigned")
    public ResponseEntity<List<DrivingLessonDTO>> getAllUnassignedDrivingLessons() {
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))
            return ResponseEntity.ok(drivingLessonService.getAllUnassignedDrivingLessons());

        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.TEACHER))
            return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
                .map(drivingLessonService::getAllUnassignedDrivingLessonsByTeacherLogin));

        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT))
            return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
                .map(drivingLessonService::getAllUnassignedDrivingLessonsByStudentLogin));

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * {@code GET /driving-lesson/canceled} : returns all canceled drivingLessons or all too late canceled drivingLessons
     *                                        of the currently logged in student
     *
     * @param late, whether only too late canceled drivingLessons should be returned
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<DrivingLessonDTO> with canceled drivingLessons,
     *         or with status {@code 404 (NotFound)}.
     */
    @GetMapping("/driving-lesson/canceled")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<List<DrivingLessonDTO>> getAllCanceledDrivingLessonsByLogin(@RequestParam(name = "late") boolean late) {
        if (late)
            return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
                .flatMap(drivingLessonService::getAllLateCanceledDrivingLessonsByStudentLogin));
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(drivingLessonService::getAllCanceledDrivingLessonsByStudentLogin));
    }

    /**
     * {@code GET /driving-lessons/canceled/{id}} : returns all canceled drivingLessons of a student or all too late
     *                                              canceled drivingLessons of a student
     *
     * @param late, whether only too late canceled drivingLessons should be returned
     * @param id of the student
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<DrivingLessonDTO> with canceled drivingLessons,
     *         or with status {@code 404 (NotFound)}.
     */
    @GetMapping("/driving-lesson/canceled/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<DrivingLessonDTO>> getAllCanceledDrivingLessonsById(@RequestParam(name = "late") boolean late,
                                                                                   @PathVariable Long id) {
        if (late)
            return ResponseUtil.wrapOrNotFound(drivingLessonService.getAllLateCanceledDrivingLessonsByStudentId(id));
        return ResponseUtil.wrapOrNotFound(drivingLessonService.getAllCanceledDrivingLessonsByStudentId(id));
    }
}
