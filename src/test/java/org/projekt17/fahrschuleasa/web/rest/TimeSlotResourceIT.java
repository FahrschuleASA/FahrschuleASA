package org.projekt17.fahrschuleasa.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.repository.TimeSlotRepository;
import org.projekt17.fahrschuleasa.service.MailService;
import org.projekt17.fahrschuleasa.service.dto.TimeSlotDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Integration tests for the {@link TimeSlotResource} REST controller.
 */
@SpringBootTest(classes = FahrschuleAsaApp.class)
public class TimeSlotResourceIT {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    private EntityCreationHelper entityCreationHelper;

    @Mock
    private MailService mockMailService;

    private MockMvc restTimeSlotMockMvc;

    private TimeSlot timeSlot;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendActivationEmail(any());

        this.entityCreationHelper = new EntityCreationHelper();
        MockitoAnnotations.initMocks(this);
        this.restTimeSlotMockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @BeforeEach
    public void initTest() {
        timeSlot = this.entityCreationHelper.createEntityTimeSlot(false);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createTimeSlot() throws Exception {
        int databaseSizeBeforeCreate = timeSlotRepository.findAll().size();

        // it is necessary to have a teacherId due to a time slot
        // regarding to a specific teacher
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        TimeSlotDTO timeSlotDTO = new TimeSlotDTO(timeSlot);

        // Create the TimeSlot
        restTimeSlotMockMvc.perform(post("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(timeSlotDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.begin").value(entityCreationHelper.getDEFAULT_BEGIN().toString()))
            .andExpect(jsonPath("$.end").value(entityCreationHelper.getDEFAULT_END().toString()))
            .andExpect(jsonPath("$.day").value(entityCreationHelper.getDEFAULT_DAY().toString()));

        // Validate the TimeSlot in the database
        List<TimeSlot> timeSlotList = timeSlotRepository.findAll();
        assertThat(timeSlotList).hasSize(databaseSizeBeforeCreate + 1);
        TimeSlot testTimeSlot = timeSlotList.get(timeSlotList.size() - 1);
        assertThat(testTimeSlot.getBegin()).isEqualTo(entityCreationHelper.getDEFAULT_BEGIN());
        assertThat(testTimeSlot.getEnd()).isEqualTo(entityCreationHelper.getDEFAULT_END());
        assertThat(testTimeSlot.getDay()).isEqualTo(entityCreationHelper.getDEFAULT_DAY());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher()
    public void createTimeSlotWrong() throws Exception {
        int databaseSizeBeforeCreate = timeSlotRepository.findAll().size();
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        TimeSlotDTO timeSlotDTO = new TimeSlotDTO(timeSlot);

        // Create the TimeSlot with an existing ID
        timeSlot.setId(1L);

        // check for Bad request if time Slot is not in between 45 - 180 minutes
        timeSlotDTO.setBegin(13);
        timeSlotDTO.setEnd(18);
        restTimeSlotMockMvc.perform(post("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(timeSlotDTO)))
            .andExpect(status().isBadRequest());

        timeSlotDTO.setBegin(1300);
        timeSlotDTO.setEnd(1800);
        restTimeSlotMockMvc.perform(post("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(timeSlotDTO)))
            .andExpect(status().isBadRequest());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeSlotMockMvc.perform(post("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO(timeSlot))))
            .andExpect(status().isBadRequest());

        // Validate the TimeSlot in the database
        List<TimeSlot> timeSlotList = timeSlotRepository.findAll();
        assertThat(timeSlotList).hasSize(databaseSizeBeforeCreate);

        // check that no timeSlot is created without begin or end
        // set id again to null because the request shouldn't fail because an already existing id
        timeSlot.setId(null);
        timeSlot.setBegin(null);
        restTimeSlotMockMvc.perform(post("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO(timeSlot))))
            .andExpect(status().isBadRequest());

        timeSlot.setBegin(entityCreationHelper.getDEFAULT_BEGIN());
        timeSlot.setEnd(null);
        restTimeSlotMockMvc.perform(post("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO(timeSlot))))
            .andExpect(status().isBadRequest());

        // check that no timeSlot is created without a day
        timeSlot.setEnd(entityCreationHelper.getDEFAULT_END());
        timeSlot.setDay(null);
        restTimeSlotMockMvc.perform(post("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO(timeSlot))))
            .andExpect(status().isBadRequest());

        // check that not time slot is created without at least one preferred category
        timeSlot.setDay(entityCreationHelper.getDEFAULT_DAY());
        timeSlot.setPreferredCategories(new HashSet<>());
        restTimeSlotMockMvc.perform(post("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO(timeSlot))))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllTimeSlotsForTeacherIdTest() throws Exception {
        // initialize database
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        timeSlotRepository.saveAndFlush(timeSlot);

        restTimeSlotMockMvc.perform(get("/api/time-slots/teacher/{id}", timeSlot.getTeacher().getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.[0].teacherId").value(timeSlot.getTeacher().getId()))
            .andExpect(jsonPath("$.[0].begin").value(timeSlot.getBegin()))
            .andExpect(jsonPath("$.[0].end").value(timeSlot.getEnd()))
            .andExpect(jsonPath("$.[0].day").value(timeSlot.getDay().toString()));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllTimeSlotsForTeacher() throws Exception {
        // initialize db
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        timeSlotRepository.saveAndFlush(timeSlot);

        restTimeSlotMockMvc.perform(get("/api/time-slots/teacher"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.[0].teacherId").value(timeSlot.getTeacher().getId()))
            .andExpect(jsonPath("$.[0].begin").value(timeSlot.getBegin()))
            .andExpect(jsonPath("$.[0].end").value(timeSlot.getEnd()))
            .andExpect(jsonPath("$.[0].day").value(timeSlot.getDay().toString()));

    }

    @Test
    @Transactional
    @WithMockUser(username = "usertest", roles = {"STUDENT"})
    public void getAllTimeSlotsForStudent() throws Exception {
        // initialize db
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(timeSlot.getTeacher());
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        studentRepository.saveAndFlush(student);
        timeSlotRepository.saveAndFlush(timeSlot);

        restTimeSlotMockMvc.perform(get("/api/time-slots/student"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.[0].teacherId").value(timeSlot.getTeacher().getId()))
            .andExpect(jsonPath("$.[0].begin").value(timeSlot.getBegin()))
            .andExpect(jsonPath("$.[0].end").value(timeSlot.getEnd()))
            .andExpect(jsonPath("$.[0].day").value(timeSlot.getDay().toString()));

        student.setCategory(DrivingCategory.MOFA);
        restTimeSlotMockMvc.perform(get("/api/time-slots/student"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.[0].teacherId").value(timeSlot.getTeacher().getId()))
            .andExpect(jsonPath("$.[0].begin").value(timeSlot.getBegin()))
            .andExpect(jsonPath("$.[0].end").value(timeSlot.getEnd()))
            .andExpect(jsonPath("$.[0].day").value(timeSlot.getDay().toString()));
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"TEACHER", "USER"})
    public void getAllTimeSlotsForStudentWrongAuthority() throws Exception {
        // initialize db
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(timeSlot.getTeacher());
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        studentRepository.saveAndFlush(student);
        timeSlotRepository.saveAndFlush(timeSlot);

        restTimeSlotMockMvc.perform(get("/api/time-slots/student"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void getTimeSlot() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        timeSlotRepository.saveAndFlush(timeSlot);

        // Get the timeSlot
        restTimeSlotMockMvc.perform(get("/api/time-slots/{id}", timeSlot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.begin").value(entityCreationHelper.getDEFAULT_BEGIN()))
            .andExpect(jsonPath("$.end").value(entityCreationHelper.getDEFAULT_END()))
            .andExpect(jsonPath("$.day").value(entityCreationHelper.getDEFAULT_DAY().toString()));

        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(timeSlot.getTeacher());
        restTimeSlotMockMvc.perform(get("/api/time-slots/{id}", timeSlot.getId())
            .with(user("johndoe").roles("STUDENT")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.begin").value(entityCreationHelper.getDEFAULT_BEGIN()))
            .andExpect(jsonPath("$.end").value(entityCreationHelper.getDEFAULT_END()))
            .andExpect(jsonPath("$.day").value(entityCreationHelper.getDEFAULT_DAY().toString()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "johndoe", roles = "STUDENT")
    public void getTimeSlotFail() throws Exception {
        // initialize db
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(timeSlot.getTeacher());
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        studentRepository.saveAndFlush(student);
        timeSlotRepository.saveAndFlush(timeSlot);

        // Get the timeSlot
        restTimeSlotMockMvc.perform(get("/api/time-slots/{id}", Long.MAX_VALUE))
            .andExpect(status().isForbidden());

        student.setCategory(DrivingCategory.C1E);
        studentRepository.saveAndFlush(student);
        restTimeSlotMockMvc.perform(get("/api/time-slots/{id}", timeSlot.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void updateTimeSlot() throws Exception {

        int databaseSizeBeforeUpdate = timeSlotRepository.findAll().size();

        HashSet<DrivingCategory> newCategories = new HashSet<>();
        newCategories.add(DrivingCategory.BE);
        newCategories.add(DrivingCategory.C1E);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeSlotMockMvc.perform(put("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO(timeSlot))))
            .andExpect(status().isBadRequest());

        // Validate the TimeSlot in the database
        List<TimeSlot> timeSlotList = timeSlotRepository.findAll();
        assertThat(timeSlotList).hasSize(databaseSizeBeforeUpdate);

        // Initialize the database
        Teacher teacher = timeSlot.getTeacher();
        timeSlot.setTeacher(null);
        timeSlotRepository.saveAndFlush(timeSlot);
        databaseSizeBeforeUpdate = timeSlotRepository.findAll().size();

        // bad request if timeSlotDTO has no id
        restTimeSlotMockMvc.perform(put("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO())))
            .andExpect(status().isBadRequest());

        // forbidden if it is not the timeSLot of the executing teacher
        restTimeSlotMockMvc.perform(put("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO(timeSlot))))
            .andExpect(status().isForbidden());


        // now update a valid timeSlot
        timeSlot.setTeacher(teacher);
        teacherRepository.saveAndFlush(teacher);
        // Update the timeSlot
        TimeSlot updatedTimeSlot = timeSlotRepository.findById(timeSlot.getId()).get();
        // Disconnect from session so that the updates on updatedTimeSlot are not directly saved in db
        em.detach(updatedTimeSlot);
        updatedTimeSlot
            .begin(entityCreationHelper.getUPDATED_BEGIN())
            .end(entityCreationHelper.getUPDATED_END())
            .day(entityCreationHelper.getUPDATED_DAY())
            .preferredCategories(newCategories)
            .optionalCategories(newCategories);

        restTimeSlotMockMvc.perform(put("/api/time-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TimeSlotDTO(updatedTimeSlot))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.begin").value(entityCreationHelper.getDEFAULT_BEGIN()))
            .andExpect(jsonPath("$.end").value(entityCreationHelper.getDEFAULT_END()))
            .andExpect(jsonPath("$.day").value(entityCreationHelper.getDEFAULT_DAY().toString()))
            .andExpect(jsonPath("$.preferredCategories", hasSize(2)))
            .andExpect(jsonPath("$.optionalCategories", hasSize(2)))
            .andExpect(jsonPath("$.preferredCategories.[*]").value(hasItem(DrivingCategory.C1E.toString())))
            .andExpect(jsonPath("$.optionalCategories.[*]").value(hasItem(DrivingCategory.C1E.toString())));

        // Validate the TimeSlot in the database
        timeSlotList = timeSlotRepository.findAll();
        assertThat(timeSlotList).hasSize(databaseSizeBeforeUpdate);
        TimeSlot testTimeSlot = timeSlotList.get(timeSlotList.size() - 1);
        assertThat(testTimeSlot.getBegin()).isEqualTo(entityCreationHelper.getDEFAULT_BEGIN());
        assertThat(testTimeSlot.getEnd()).isEqualTo(entityCreationHelper.getDEFAULT_END());
        assertThat(testTimeSlot.getDay()).isEqualTo(entityCreationHelper.getDEFAULT_DAY());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void deleteTimeSlot() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        timeSlotRepository.saveAndFlush(timeSlot);

        int databaseSizeBeforeDelete = timeSlotRepository.findAll().size();

        // Delete the timeSlot
        restTimeSlotMockMvc.perform(delete("/api/time-slots/{id}", timeSlot.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimeSlot> timeSlotList = timeSlotRepository.findAll();
        assertThat(timeSlotList).hasSize(databaseSizeBeforeDelete-1);
        timeSlotList.forEach(t -> assertThat(t.getId()).isNotEqualTo(timeSlot.getId()));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeSlot.class);
        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setId(1L);
        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setId(timeSlot1.getId());
        assertThat(timeSlot1).isEqualTo(timeSlot2);
        timeSlot2.setId(2L);
        assertThat(timeSlot1).isNotEqualTo(timeSlot2);
        timeSlot1.setId(null);
        assertThat(timeSlot1).isNotEqualTo(timeSlot2);
    }

    @Test
    @EntityCreationHelper.WithMockTeacher
    @Transactional
    public void addBlockedDatesTest() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        timeSlotRepository.saveAndFlush(timeSlot);

        int dbSize = timeSlotRepository.findAll().size();

        LocalDate blocked = LocalDate.now().plusDays(2L);

        restTimeSlotMockMvc.perform(post("/api/time-slots/blocked-dates")
            .param("time_slot_id", timeSlot.getId().toString())
            .param("blocked_date", ""))
            .andExpect(status().isBadRequest());

        restTimeSlotMockMvc.perform(post("/api/time-slots/blocked-dates")
            .param("time_slot_id", "")
            .param("blocked_date", blocked.toString()))
            .andExpect(status().isBadRequest());

        restTimeSlotMockMvc.perform(post("/api/time-slots/blocked-dates")
            .param("time_slot_id", timeSlot.getId().toString())
            .param("blocked_date", blocked.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.blockedDates", hasSize(1)))
            .andExpect(jsonPath("$.blockedDates.[0]").value(blocked.toString()));

        assertThat(timeSlotRepository.findAll().get(dbSize -1).getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlotRepository.findAll().get(dbSize -1).getBlockedDates()).contains(blocked);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void removeBlockedDateTest() throws Exception {
        // Initialize the database
        LocalDate blocked = LocalDate.now().plusDays(2L);
        timeSlot.getBlockedDates().add(blocked);
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        timeSlotRepository.saveAndFlush(timeSlot);

        int dbSize = timeSlotRepository.findAll().size();

        restTimeSlotMockMvc.perform(delete("/api/time-slots/blocked-dates")
            .param("time_slot_id", timeSlot.getId().toString())
            .param("blocked_date", ""))
            .andExpect(status().isBadRequest());

        restTimeSlotMockMvc.perform(delete("/api/time-slots/blocked-dates")
            .param("time_slot_id", "")
            .param("blocked_date", blocked.toString()))
            .andExpect(status().isBadRequest());

        restTimeSlotMockMvc.perform(delete("/api/time-slots/blocked-dates")
            .param("time_slot_id", timeSlot.getId().toString())
            .param("blocked_date", blocked.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(timeSlot.getId()))
            .andExpect(jsonPath("$.blockedDates", hasSize(0)));

        assertThat(timeSlotRepository.findAll().get(dbSize -1).getBlockedDates().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void notifyStudentsAboutTimeSlotChanges() throws Exception {
        // initialize db
        teacherRepository.saveAndFlush(timeSlot.getTeacher());
        timeSlotRepository.saveAndFlush(timeSlot);

        restTimeSlotMockMvc.perform(post("/api/time-slots/notify"))
            .andExpect(status().isNoContent());
    }
}
