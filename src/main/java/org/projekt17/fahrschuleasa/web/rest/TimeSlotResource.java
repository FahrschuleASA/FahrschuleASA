package org.projekt17.fahrschuleasa.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.TimeSlotService;
import org.projekt17.fahrschuleasa.service.dto.TimeSlotDTO;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.projekt17.fahrschuleasa.domain.TimeSlot}.
 */
@RestController
@RequestMapping("/api")
public class TimeSlotResource {

    private final Logger log = LoggerFactory.getLogger(TimeSlotResource.class);

    private static final String ENTITY_NAME = "timeSlot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimeSlotService timeSlotService;

    public TimeSlotResource(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    /**
     * {@code POST  /time-slots} : Create a new timeSlot.
     *
     * @param timeSlotDTO the timeSlot to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timeSlot, or with
     * status {@code 400 (Bad Request)} if the timeSlot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/time-slots")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER+ "\")")
    public ResponseEntity<TimeSlotDTO> createTimeSlot(@RequestBody TimeSlotDTO timeSlotDTO) throws URISyntaxException {
        log.debug("REST request to save TimeSlot : {}", timeSlotDTO);
        if (timeSlotDTO.getId() != null) {
            throw new BadRequestAlertException("A new timeSlot cannot already have an ID", ENTITY_NAME, "idexists");
        } else if (timeSlotDTO.getBegin() == null || timeSlotDTO.getEnd() == null || timeSlotDTO.getDay() == null){
            throw new BadRequestAlertException("A time slot needs a begin and an end and a day", ENTITY_NAME, "endbegindaynotexist");
        } else if(timeSlotDTO.getPreferredCategories() == null || timeSlotDTO.getPreferredCategories().size() == 0) {
            throw new BadRequestAlertException("A timeSlot needs at least one preferred category", ENTITY_NAME, "nopreferredcategorie");
        } else {
            int duration;
            if (timeSlotDTO.getBegin() < timeSlotDTO.getEnd())
                duration = timeSlotDTO.getEnd() - timeSlotDTO.getBegin();
            else
                duration = 1440 - timeSlotDTO.getBegin() + timeSlotDTO.getEnd();
            if (duration < 45 || duration > 180)
                throw new BadRequestAlertException(
                    String.format("Duration of a TimeSlot should be between 45 and 180 minutes, but was %d", duration),
                    "timeSlot", "badduration");
            if (timeSlotDTO.getBegin() < 360 && timeSlotDTO.getEnd() > 360)
                throw new BadRequestAlertException("Bad begin and end times of TimeSlot", "timeSlot", "badbeginend");

            Optional<TimeSlotDTO> createdTimeSlot = SecurityUtils.getCurrentUserLogin()
                .flatMap(login -> timeSlotService.createTimeSlot(timeSlotDTO, login));

            if (createdTimeSlot.isPresent())
                return ResponseEntity.created(new URI("/api/time-slots/" + createdTimeSlot.get().getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        createdTimeSlot.get().getId().toString()))
                    .body(createdTimeSlot.get());

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * {@code PUT  /time-slots} : Updates an existing timeSlot.
     *
     * @param timeSlotDTO the timeSlot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeSlot,
     * or with status {@code 400 (Bad Request)} if the timeSlot is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeSlot couldn't be updated.
     */
    @PutMapping("/time-slots")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TimeSlotDTO> updateTimeSlot(@RequestBody TimeSlotDTO timeSlotDTO) {
        log.debug("REST request to update TimeSlot : {}", timeSlotDTO);
        if (timeSlotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Optional<Boolean> isTimeSlotOfTeacher = SecurityUtils.getCurrentUserLogin()
            .map(login -> timeSlotService.isTimeSlotOfTeacher(timeSlotDTO.getId(), login));

        if (isTimeSlotOfTeacher.isPresent()) {
            if (!isTimeSlotOfTeacher.get())
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseUtil.wrapOrNotFound(timeSlotService.updateTimeSlot(timeSlotDTO),
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                timeSlotDTO.getId().toString()));
    }

    /**
     * @param timeSlotId the timeSlot to check whether it is a timeSlot of the current logged in teacher is
     *
     * @return true if timeSlot with timeSlotId is a timeSlot of the current logged in teacher is, else false
     */
    private Optional<ResponseEntity<TimeSlotDTO>> checkIfTimeSlotOfTeacher(Long timeSlotId) {
        Optional<Boolean> isTimeSlotOfTeacher = SecurityUtils.getCurrentUserLogin()
            .map(login -> timeSlotService.isTimeSlotOfTeacher(timeSlotId, login));

        if (isTimeSlotOfTeacher.isPresent()) {
            if (!isTimeSlotOfTeacher.get())
                return Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        } else
            return Optional.of(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        return Optional.empty();
    }

    /**
     *  {@code POST  /time-slots/blocked-dates} : adds a blocked date to an existing timeSlot.
     *
     * @param timeSlotId the timeSlot to add a blocked date
     * @param blockedDate the date to add to the timeSlot
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} with body the updated time slot
     *         containing the currently added blockedDate
     *         or {@code 404 (NotFound)} if the timeSlot does not exist
     *         or {@code 400 (Bad Request)} if the timeSlot or blockedDate are not valid
     */
    @PostMapping("/time-slots/blocked-dates")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TimeSlotDTO> addBlockedDate(@RequestParam(name = "time_slot_id") Long timeSlotId,
                                                      @RequestParam(name = "blocked_date") LocalDate blockedDate) {
        log.debug("REST request to block {} of TimeSlot {}", blockedDate, timeSlotId);

        if (timeSlotId == null || blockedDate == null)
            throw new BadRequestAlertException("TimeSlot Id and blocked date must be not null", ENTITY_NAME, "null");

        return checkIfTimeSlotOfTeacher(timeSlotId)
            .orElse(ResponseUtil.wrapOrNotFound(timeSlotService.addBlockedDate(timeSlotId, blockedDate)));
    }

    /**
     * {@code DELETE /time-slots/blocked-dates} : removes a blocked date of an existing timeSlot.
     *
     * @param timeSlotId the timeSlot to remove a blockedDate
     * @param blockedDate the date to remove from the timeSlot
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} with body the updated time slot not longer
     *         containing the currently removed blockedDate
     *         or {@code 404 (NotFound)} if the timeSlot does not exist
     *         or {@code 400 (Bad Request)} if the timeSlot or blockedDate are not valid
     */
    @DeleteMapping("/time-slots/blocked-dates")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<TimeSlotDTO> removeBlockedDate(@RequestParam(name = "time_slot_id") Long timeSlotId,
                                                         @RequestParam(name = "blocked_date") LocalDate blockedDate) {
        log.debug("REST request to remove block {} of TimeSlot {}", blockedDate, timeSlotId);

        if (timeSlotId == null || blockedDate == null)
            throw new BadRequestAlertException("TimeSlot Id and blocked date must be not null", ENTITY_NAME, "null");

        return checkIfTimeSlotOfTeacher(timeSlotId)
            .orElse(ResponseUtil.wrapOrNotFound(timeSlotService.removeBlockedDate(timeSlotId, blockedDate)));
    }

    /**
     * {@code POST /time-slots/notify} : notify a teachers students about changes in his timeSlots.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PostMapping("/time-slots/notify")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<Void> notifyStudentsAboutTimeSlotChanges() {
        log.debug("REST request to notify students about TimeSlot changes");

        timeSlotService.notifyStudentsAboutTimeSlotChanges(SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new IllegalArgumentException("Current login not found")));

        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName,
            ENTITY_NAME + ".notify.changed", "")).build();
    }

    /**
     * {@code GET /time-slots/:teacherId} : get all timeSlots from Teacher "teacherId".
     *
     * @param id the id of the Teacher of retrieving timeSlots.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<timeSlotDTO>,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/time-slots/teacher/{id}")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public List<TimeSlotDTO> getAllTimeSlotsForTeacherId(@PathVariable Long id) {
        log.debug("REST request to get all TimeSlots for Teacher : {}", id);
        return timeSlotService.getAllTimeSlotsForTeacherId(id);
    }

    /**
     * {@code GET /time-slots/teacher} : get all timeSlots of the currently logged in teacher.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<timeSlotDTO>,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/time-slots/teacher")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<List<TimeSlotDTO>> getAllTimeSlotsForTeacher() {
        log.debug("REST request to get all TimeSlots for current logged in Teacher");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .map(timeSlotService::getAllTimeSlotsForTeacherLogin));
    }

    /**
     * {@code GET /time-slots/student} : get all timeSlots from the teacher of the current logged in student.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the List<timeSlotDTO>,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/time-slots/student")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<List<TimeSlotDTO>> getAllTimeSlotsForStudent() {
        log.debug("REST request to get all TimeSlots for current logged in Student");
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .map(timeSlotService::getAllTimeSlotsForStudentLogin));
    }

    /**
     * {@code GET  /time-slots/:id} : get the "id" timeSlot.
     *
     * @param id the id of the timeSlot to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timeSlot, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/time-slots/{id}")
    public ResponseEntity<TimeSlotDTO> getTimeSlot(@PathVariable Long id) {
        log.debug("REST request to get TimeSlot : {}", id);

        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.STUDENT)) {
            Optional<Boolean> isTimeSlotOfStudent = SecurityUtils.getCurrentUserLogin()
                .map(login -> timeSlotService.isTimeSlotOfStudent(id, login));

            if (isTimeSlotOfStudent.isPresent()) {
                if (!isTimeSlotOfStudent.get())
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            return ResponseUtil.wrapOrNotFound(timeSlotService.getTimeSlot(id, true));
        }

        return ResponseUtil.wrapOrNotFound(timeSlotService.getTimeSlot(id, false));
    }

    /**
     * {@code DELETE  /time-slots/:id} : delete the "id" timeSlot.
     *
     * @param id the id of the timeSlot to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/time-slots/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        log.debug("REST request to delete TimeSlot : {}", id);
        Optional<Boolean> isTimeSlotOfTeacher = SecurityUtils.getCurrentUserLogin()
            .map(login -> timeSlotService.isTimeSlotOfTeacher(id, login));

        if (isTimeSlotOfTeacher.isPresent()) {
            if (!isTimeSlotOfTeacher.get())
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        timeSlotService.deleteTimeSlot(id, SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new IllegalArgumentException("Current login not found")));

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName,
            true, ENTITY_NAME, id.toString())).build();
    }
}
