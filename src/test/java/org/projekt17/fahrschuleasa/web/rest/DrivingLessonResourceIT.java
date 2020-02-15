package org.projekt17.fahrschuleasa.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.DrivingLesson;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;
import org.projekt17.fahrschuleasa.repository.DrivingLessonRepository;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.repository.TimeSlotRepository;
import org.projekt17.fahrschuleasa.service.dto.DrivingLessonDTO;
import org.projekt17.fahrschuleasa.service.dto.SmallStudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Integration tests for the {@link DrivingLessonResource} REST controller.
 */
@SpringBootTest(classes = FahrschuleAsaApp.class)
public class DrivingLessonResourceIT {

    @Autowired
    private DrivingLessonRepository drivingLessonRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    private MockMvc restDrivingLessonMockMvc;

    private EntityCreationHelper entityCreationHelper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        entityCreationHelper = new EntityCreationHelper();
        MockitoAnnotations.initMocks(this);
        this.restDrivingLessonMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherWithStudentId() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(teacher);
        studentRepository.save(student);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(entityCreationHelper.getDEFAULT_DRIVING_LESSON_BEGIN());
        drivingLessonDTO.setEnd(entityCreationHelper.getDEFAULT_DRIVING_LESSON_END());
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        SmallStudentDTO smallStudentDTO = new SmallStudentDTO();
        smallStudentDTO.setStudentId(student.getId());
        drivingLessonDTO.setDriver(smallStudentDTO);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.driver.studentId").value(student.getId()))
            .andExpect(jsonPath("$.driver.firstname").value(student.getUser().getFirstName()))
            .andExpect(jsonPath("$.driver.lastname").value(student.getUser().getLastName()))
            .andExpect(jsonPath("$.drivingCategory").value(student.getCategory().toString()))
            .andExpect(jsonPath("$.missingStudents", hasSize(0)))
            .andExpect(jsonPath("$.lateMissingStudents", hasSize(0)))
            .andExpect(jsonPath("$.pickup.town").value(entityCreationHelper.getDEFAULT_TOWN()))
            .andExpect(jsonPath("$.pickup.street").value(entityCreationHelper.getDEFAULT_STREET()))
            .andExpect(jsonPath("$.pickup.postal").value(entityCreationHelper.getDEFAULT_POSTAL()))
            .andExpect(jsonPath("$.pickup.houseNumber").value(entityCreationHelper.getDEFAULT_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.pickup.country").value(entityCreationHelper.getDEFAULT_COUNTRY()))
            .andExpect(jsonPath("$.pickup.additional").value(entityCreationHelper.getDEFAULT_ADDITIONAL()))
            .andExpect(jsonPath("$.destination.town").value(entityCreationHelper.getALTER_TOWN()))
            .andExpect(jsonPath("$.destination.street").value(entityCreationHelper.getALTER_STREET()))
            .andExpect(jsonPath("$.destination.postal").value(entityCreationHelper.getALTER_POSTAL()))
            .andExpect(jsonPath("$.destination.houseNumber").value(entityCreationHelper.getALTER_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.destination.country").value(entityCreationHelper.getALTER_COUNTRY()))
            .andExpect(jsonPath("$.destination.additional").value(entityCreationHelper.getALTER_ADDITIONAL()))
            .andExpect(jsonPath("$.lessonType").value(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString()))
            .andExpect(jsonPath("$.bookable").value(true))
            .andExpect(jsonPath("$.teacherId").value(teacher.getId()));

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        DrivingLesson testDrivingLesson = drivingLessonList.get(drivingLessonList.size() - 1);
        assertThat(testDrivingLesson.getManualLesson()).isTrue();
        assertThat(testDrivingLesson.getBookable()).isTrue();
        assertThat(testDrivingLesson.getLessonType()).isEqualTo(entityCreationHelper.getDEFAULT_LESSON_TYPE());
        assertThat(testDrivingLesson.getDriver().getId()).isEqualTo(student.getId());
        assertThat(testDrivingLesson.getMissingStudents()).isEmpty();
        assertThat(testDrivingLesson.getLateMissingStudents()).isEmpty();
        assertThat(testDrivingLesson.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(testDrivingLesson.getPickup().checkSimilar(entityCreationHelper.createEntityLocation())).isTrue();
        assertThat(testDrivingLesson.getDestination().checkSimilar(entityCreationHelper.createAlterEntityLocation())).isTrue();
        assertThat(testDrivingLesson.getOptionalStudents()).isEmpty();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherWithoutStudentId() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(entityCreationHelper.getDEFAULT_DRIVING_LESSON_BEGIN());
        drivingLessonDTO.setEnd(entityCreationHelper.getDEFAULT_DRIVING_LESSON_END());
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.driver").doesNotExist());

        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        DrivingLesson testDrivingLesson = drivingLessonList.get(drivingLessonList.size() - 1);
        assertThat(testDrivingLesson.getManualLesson()).isTrue();
        assertThat(testDrivingLesson.getBookable()).isTrue();
        assertThat(testDrivingLesson.getLessonType()).isEqualTo(entityCreationHelper.getDEFAULT_LESSON_TYPE());
        assertThat(testDrivingLesson.getDriver()).isNull();
        assertThat(testDrivingLesson.getMissingStudents()).isEmpty();
        assertThat(testDrivingLesson.getLateMissingStudents()).isEmpty();
        assertThat(testDrivingLesson.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(testDrivingLesson.getPickup().checkSimilar(entityCreationHelper.createEntityLocation())).isTrue();
        assertThat(testDrivingLesson.getDestination().checkSimilar(entityCreationHelper.createAlterEntityLocation())).isTrue();
        assertThat(testDrivingLesson.getOptionalStudents()).isEmpty();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherBookableNull() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(entityCreationHelper.getDEFAULT_DRIVING_LESSON_BEGIN());
        drivingLessonDTO.setEnd(entityCreationHelper.getDEFAULT_DRIVING_LESSON_END());
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(null);
        drivingLessonDTO.setDriver(null);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.bookable").value(true));

        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        DrivingLesson testDrivingLesson = drivingLessonList.get(drivingLessonList.size() - 1);
        assertThat(testDrivingLesson.getManualLesson()).isTrue();
        assertThat(testDrivingLesson.getBookable()).isTrue();
        assertThat(testDrivingLesson.getLessonType()).isEqualTo(entityCreationHelper.getDEFAULT_LESSON_TYPE());
        assertThat(testDrivingLesson.getDriver()).isNull();
        assertThat(testDrivingLesson.getMissingStudents()).isEmpty();
        assertThat(testDrivingLesson.getLateMissingStudents()).isEmpty();
        assertThat(testDrivingLesson.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(testDrivingLesson.getPickup().checkSimilar(entityCreationHelper.createEntityLocation())).isTrue();
        assertThat(testDrivingLesson.getDestination().checkSimilar(entityCreationHelper.createAlterEntityLocation())).isTrue();
        assertThat(testDrivingLesson.getOptionalStudents()).isEmpty();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherBlockTimeSlotWithLessonOneDayAndSlotOneDay() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        TimeSlot timeSlot = entityCreationHelper.createEntityTimeSlot(false);
        timeSlot.setTeacher(teacher);
        timeSlotRepository.saveAndFlush(timeSlot);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(entityCreationHelper.getDEFAULT_DRIVING_LESSON_BEGIN());
        drivingLessonDTO.setEnd(entityCreationHelper.getDEFAULT_DRIVING_LESSON_END());
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        timeSlot = timeSlotRepository.findById(timeSlot.getId()).orElse(null);
        assertThat(timeSlot.getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlot.getBlockedDates()).contains(entityCreationHelper.getDEFAULT_DRIVING_LESSON_BEGIN().toLocalDate());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherBlockTimeSlotWithLessonOneDayAndSlotTwoDaysBeginDaySame() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        TimeSlot timeSlot = entityCreationHelper.createEntityTimeSlot(false);
        timeSlot.setTeacher(teacher);
        timeSlot.setBegin(1380);
        timeSlot.setEnd(60);
        timeSlotRepository.saveAndFlush(timeSlot);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 6, 22, 0));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 6, 23, 30));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        timeSlot = timeSlotRepository.findById(timeSlot.getId()).orElse(null);
        assertThat(timeSlot.getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlot.getBlockedDates()).contains(LocalDate.of(2000, 3, 6));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherBlockTimeSlotWithLessonOneDayAndSlotTwoDaysAndBeginDayBefore() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        TimeSlot timeSlot = entityCreationHelper.createEntityTimeSlot(false);
        timeSlot.setTeacher(teacher);
        timeSlot.setBegin(1380);
        timeSlot.setEnd(60);
        timeSlotRepository.saveAndFlush(timeSlot);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 7, 0, 30));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 7, 2, 0));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        timeSlot = timeSlotRepository.findById(timeSlot.getId()).orElse(null);
        assertThat(timeSlot.getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlot.getBlockedDates()).contains(LocalDate.of(2000, 3, 6));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherBlockTimeSlotWithLessonTwoDaysAndSlotOneDayAndBeginDaySame() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        TimeSlot timeSlot = entityCreationHelper.createEntityTimeSlot(false);
        timeSlot.setTeacher(teacher);
        timeSlot.setBegin(1290);
        timeSlot.setEnd(1380);
        timeSlotRepository.saveAndFlush(timeSlot);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 6, 22, 30));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 7, 0, 30));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        timeSlot = timeSlotRepository.findById(timeSlot.getId()).orElse(null);
        assertThat(timeSlot.getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlot.getBlockedDates()).contains(LocalDate.of(2000, 3, 6));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherBlockTimeSlotWithLessonTwoDaysAndSlotOneDayAndBeginDayAfter() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        TimeSlot timeSlot = entityCreationHelper.createEntityTimeSlot(false);
        timeSlot.setTeacher(teacher);
        timeSlot.setBegin(30);
        timeSlot.setEnd(150);
        timeSlot.setDay(DayOfWeek.TU);
        timeSlotRepository.saveAndFlush(timeSlot);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 6, 23, 30));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 7, 1, 0));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        timeSlot = timeSlotRepository.findById(timeSlot.getId()).orElse(null);
        assertThat(timeSlot.getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlot.getBlockedDates()).contains(LocalDate.of(2000, 3, 7));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonTeacherBlockTimeSlotWithLessonTwoDaysAndSlotTwoDays() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        TimeSlot timeSlot = entityCreationHelper.createEntityTimeSlot(false);
        timeSlot.setTeacher(teacher);
        timeSlot.setBegin(1380);
        timeSlot.setEnd(60);
        timeSlotRepository.saveAndFlush(timeSlot);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 6, 23, 30));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 7, 1, 30));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        timeSlot = timeSlotRepository.findById(timeSlot.getId()).orElse(null);
        assertThat(timeSlot.getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlot.getBlockedDates()).contains(LocalDate.of(2000, 3, 6));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void createDrivingLessonAdminWithStudentId() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(teacher);
        studentRepository.save(student);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(entityCreationHelper.getDEFAULT_DRIVING_LESSON_BEGIN());
        drivingLessonDTO.setEnd(entityCreationHelper.getDEFAULT_DRIVING_LESSON_END());
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        SmallStudentDTO smallStudentDTO = new SmallStudentDTO();
        smallStudentDTO.setStudentId(student.getId());
        drivingLessonDTO.setDriver(smallStudentDTO);
        drivingLessonDTO.setTeacherId(teacher.getId());

        // Create the DrivingLesson and check returned DTO
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.driver.studentId").value(student.getId()))
            .andExpect(jsonPath("$.driver.firstname").value(student.getUser().getFirstName()))
            .andExpect(jsonPath("$.driver.lastname").value(student.getUser().getLastName()))
            .andExpect(jsonPath("$.drivingCategory").value(student.getCategory().toString()))
            .andExpect(jsonPath("$.missingStudents", hasSize(0)))
            .andExpect(jsonPath("$.lateMissingStudents", hasSize(0)))
            .andExpect(jsonPath("$.pickup.town").value(entityCreationHelper.getDEFAULT_TOWN()))
            .andExpect(jsonPath("$.pickup.street").value(entityCreationHelper.getDEFAULT_STREET()))
            .andExpect(jsonPath("$.pickup.postal").value(entityCreationHelper.getDEFAULT_POSTAL()))
            .andExpect(jsonPath("$.pickup.houseNumber").value(entityCreationHelper.getDEFAULT_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.pickup.country").value(entityCreationHelper.getDEFAULT_COUNTRY()))
            .andExpect(jsonPath("$.pickup.additional").value(entityCreationHelper.getDEFAULT_ADDITIONAL()))
            .andExpect(jsonPath("$.destination.town").value(entityCreationHelper.getALTER_TOWN()))
            .andExpect(jsonPath("$.destination.street").value(entityCreationHelper.getALTER_STREET()))
            .andExpect(jsonPath("$.destination.postal").value(entityCreationHelper.getALTER_POSTAL()))
            .andExpect(jsonPath("$.destination.houseNumber").value(entityCreationHelper.getALTER_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.destination.country").value(entityCreationHelper.getALTER_COUNTRY()))
            .andExpect(jsonPath("$.destination.additional").value(entityCreationHelper.getALTER_ADDITIONAL()))
            .andExpect(jsonPath("$.lessonType").value(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString()))
            .andExpect(jsonPath("$.bookable").value(true))
            .andExpect(jsonPath("$.teacherId").value(teacher.getId()));

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate + 1);
        DrivingLesson testDrivingLesson = drivingLessonList.get(drivingLessonList.size() - 1);
        assertThat(testDrivingLesson.getManualLesson()).isTrue();
        assertThat(testDrivingLesson.getBookable()).isTrue();
        assertThat(testDrivingLesson.getLessonType()).isEqualTo(entityCreationHelper.getDEFAULT_LESSON_TYPE());
        assertThat(testDrivingLesson.getDriver().getId()).isEqualTo(student.getId());
        assertThat(testDrivingLesson.getMissingStudents()).isEmpty();
        assertThat(testDrivingLesson.getLateMissingStudents()).isEmpty();
        assertThat(testDrivingLesson.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(testDrivingLesson.getPickup().checkSimilar(entityCreationHelper.createEntityLocation())).isTrue();
        assertThat(testDrivingLesson.getDestination().checkSimilar(entityCreationHelper.createAlterEntityLocation())).isTrue();
        assertThat(testDrivingLesson.getOptionalStudents()).isEmpty();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void createDrivingLessonAdminMissingTeacherId() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        // Create the DrivingLessonDTO with an existing ID
        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 6, 23, 30));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 7, 1, 30));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        // Create the DrivingLessonDTO with an existing ID
        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonWithMissingBeginEnd() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        // Create the DrivingLessonDTO with an existing ID
        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonWithBadBeginEnd() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);

        // Create the DrivingLessonDTO with an existing ID
        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 6, 5, 0));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 6, 7, 0));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonWithTooLongDuration() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);

        // Create the DrivingLessonDTO with an existing ID
        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 6, 10, 0));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 6, 13, 45));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonWithTooShortDuration() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);

        // Create the DrivingLessonDTO with an existing ID
        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setBegin(LocalDateTime.of(2000, 3, 6, 13, 30));
        drivingLessonDTO.setEnd(LocalDateTime.of(2000, 3, 6, 13, 45));
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        drivingLessonDTO.setDriver(null);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createDrivingLessonWithStudentTeacherMismatch() throws Exception {
        int databaseSizeBeforeCreate = drivingLessonRepository.findAll().size();

        Teacher teacher1 = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher1);
        Teacher teacher2 = entityCreationHelper.createEntityTeacher(true);
        teacher2.getUser().setLogin("other");
        teacherRepository.save(teacher2);
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(teacher2);
        studentRepository.save(student);

        // Create the DrivingLessonDTO with an existing ID
        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO();
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(true);
        SmallStudentDTO smallStudentDTO = new SmallStudentDTO();
        smallStudentDTO.setStudentId(student.getId());
        drivingLessonDTO.setDriver(smallStudentDTO);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testCreateDrivingLessonForbidden() throws Exception {
        restDrivingLessonMockMvc.perform(post("/api/driving-lessons").contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new DrivingLessonDTO()))).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testBookDrivingLessonTeacher() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(teacher);
        studentRepository.save(student);
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLesson.setBegin(LocalDateTime.now().plusDays(1));
        drivingLesson.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        drivingLesson.setTeacher(teacher);
        drivingLessonRepository.save(drivingLesson);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO(drivingLesson);
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        SmallStudentDTO smallStudentDTO = new SmallStudentDTO();
        smallStudentDTO.setStudentId(student.getId());
        drivingLessonDTO.setDriver(smallStudentDTO);
        drivingLessonDTO.setTeacherId(teacher.getId());

        restDrivingLessonMockMvc.perform(put("/api/driving-lesson/book")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.driver.studentId").value(student.getId()))
            .andExpect(jsonPath("$.driver.firstname").value(student.getUser().getFirstName()))
            .andExpect(jsonPath("$.driver.lastname").value(student.getUser().getLastName()))
            .andExpect(jsonPath("$.drivingCategory").value(student.getCategory().toString()))
            .andExpect(jsonPath("$.missingStudents", hasSize(0)))
            .andExpect(jsonPath("$.lateMissingStudents", hasSize(0)))
            .andExpect(jsonPath("$.pickup.town").value(entityCreationHelper.getDEFAULT_TOWN()))
            .andExpect(jsonPath("$.pickup.street").value(entityCreationHelper.getDEFAULT_STREET()))
            .andExpect(jsonPath("$.pickup.postal").value(entityCreationHelper.getDEFAULT_POSTAL()))
            .andExpect(jsonPath("$.pickup.houseNumber").value(entityCreationHelper.getDEFAULT_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.pickup.country").value(entityCreationHelper.getDEFAULT_COUNTRY()))
            .andExpect(jsonPath("$.pickup.additional").value(entityCreationHelper.getDEFAULT_ADDITIONAL()))
            .andExpect(jsonPath("$.destination.town").value(entityCreationHelper.getALTER_TOWN()))
            .andExpect(jsonPath("$.destination.street").value(entityCreationHelper.getALTER_STREET()))
            .andExpect(jsonPath("$.destination.postal").value(entityCreationHelper.getALTER_POSTAL()))
            .andExpect(jsonPath("$.destination.houseNumber").value(entityCreationHelper.getALTER_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.destination.country").value(entityCreationHelper.getALTER_COUNTRY()))
            .andExpect(jsonPath("$.destination.additional").value(entityCreationHelper.getALTER_ADDITIONAL()))
            .andExpect(jsonPath("$.lessonType").value(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString()))
            .andExpect(jsonPath("$.bookable").value(true))
            .andExpect(jsonPath("$.teacherId").value(teacher.getId()));

        drivingLesson = drivingLessonRepository.findById(drivingLesson.getId()).orElse(null);
        assertThat(drivingLesson).isNotNull();
        assertThat(drivingLesson.getPickup().checkSimilar(entityCreationHelper.createEntityLocation())).isTrue();
        assertThat(drivingLesson.getDestination().checkSimilar(entityCreationHelper.createAlterEntityLocation())).isTrue();
        assertThat(drivingLesson.getDriver().getId()).isEqualTo(student.getId());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testBookDrivingLessonStudent() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(false);
        student.setTeacher(teacher);
        studentRepository.save(student);
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLesson.setBegin(LocalDateTime.now().plusDays(1));
        drivingLesson.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        drivingLesson.setTeacher(teacher);
        drivingLessonRepository.save(drivingLesson);

        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO(drivingLesson);
        drivingLessonDTO.setPickup(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setDestination(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setTeacherId(teacher.getId());

        restDrivingLessonMockMvc.perform(put("/api/driving-lesson/book")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.driver.studentId").value(student.getId()))
            .andExpect(jsonPath("$.driver.firstname").value(student.getUser().getFirstName()))
            .andExpect(jsonPath("$.driver.lastname").value(student.getUser().getLastName()))
            .andExpect(jsonPath("$.drivingCategory").value(student.getCategory().toString()))
            .andExpect(jsonPath("$.missingStudents", hasSize(0)))
            .andExpect(jsonPath("$.lateMissingStudents", hasSize(0)))
            .andExpect(jsonPath("$.pickup.town").value(entityCreationHelper.getDEFAULT_TOWN()))
            .andExpect(jsonPath("$.pickup.street").value(entityCreationHelper.getDEFAULT_STREET()))
            .andExpect(jsonPath("$.pickup.postal").value(entityCreationHelper.getDEFAULT_POSTAL()))
            .andExpect(jsonPath("$.pickup.houseNumber").value(entityCreationHelper.getDEFAULT_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.pickup.country").value(entityCreationHelper.getDEFAULT_COUNTRY()))
            .andExpect(jsonPath("$.pickup.additional").value(entityCreationHelper.getDEFAULT_ADDITIONAL()))
            .andExpect(jsonPath("$.destination.town").value(entityCreationHelper.getALTER_TOWN()))
            .andExpect(jsonPath("$.destination.street").value(entityCreationHelper.getALTER_STREET()))
            .andExpect(jsonPath("$.destination.postal").value(entityCreationHelper.getALTER_POSTAL()))
            .andExpect(jsonPath("$.destination.houseNumber").value(entityCreationHelper.getALTER_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.destination.country").value(entityCreationHelper.getALTER_COUNTRY()))
            .andExpect(jsonPath("$.destination.additional").value(entityCreationHelper.getALTER_ADDITIONAL()))
            .andExpect(jsonPath("$.lessonType").value(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString()))
            .andExpect(jsonPath("$.bookable").value(true))
            .andExpect(jsonPath("$.teacherId").value(teacher.getId()));

        drivingLesson = drivingLessonRepository.findById(drivingLesson.getId()).orElse(null);
        assertThat(drivingLesson).isNotNull();
        assertThat(drivingLesson.getPickup().checkSimilar(entityCreationHelper.createEntityLocation())).isTrue();
        assertThat(drivingLesson.getDestination().checkSimilar(entityCreationHelper.createAlterEntityLocation())).isTrue();
        assertThat(drivingLesson.getDriver().getId()).isEqualTo(student.getId());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testCancelDrivingLessonTeacher() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(teacher);
        studentRepository.save(student);
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLesson.setBegin(LocalDateTime.now().plusDays(1));
        drivingLesson.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        drivingLesson.setTeacher(teacher);
        drivingLesson.setDriver(student);
        drivingLessonRepository.save(drivingLesson);

        SchoolConfiguration.setDeadlineMissedLesson(0);

        Long id = drivingLesson.getId();
        restDrivingLessonMockMvc.perform(put("/api/driving-lesson/cancel?driving_lesson_id={id}", id));

        drivingLesson = drivingLessonRepository.findById(drivingLesson.getId()).orElse(null);
        assertThat(drivingLesson).isNotNull();
        assertThat(drivingLesson.getPickup()).isNull();
        assertThat(drivingLesson.getDestination()).isNull();
        assertThat(drivingLesson.getDriver()).isNull();
        assertThat(drivingLesson.getMissingStudents().size()).isEqualTo(1);
        assertThat(drivingLesson.getLateMissingStudents().size()).isEqualTo(0);
        assertThat(drivingLesson.getMissingStudents()).contains(student);
        assertThat(drivingLesson.getBookable()).isTrue();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testCancelDrivingLessonTeacherBookableFalse() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(teacher);
        studentRepository.save(student);
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLesson.setBegin(LocalDateTime.now().plusDays(1));
        drivingLesson.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        drivingLesson.setTeacher(teacher);
        drivingLesson.setDriver(student);
        drivingLessonRepository.save(drivingLesson);

        SchoolConfiguration.setDeadlineMissedLesson(0);

        Long id = drivingLesson.getId();
        restDrivingLessonMockMvc.perform(put("/api/driving-lesson/cancel?driving_lesson_id={id}&bookable=false", id));

        drivingLesson = drivingLessonRepository.findById(drivingLesson.getId()).orElse(null);
        assertThat(drivingLesson).isNotNull();
        assertThat(drivingLesson.getPickup()).isNull();
        assertThat(drivingLesson.getDestination()).isNull();
        assertThat(drivingLesson.getDriver()).isNull();
        assertThat(drivingLesson.getMissingStudents().size()).isEqualTo(1);
        assertThat(drivingLesson.getLateMissingStudents().size()).isEqualTo(0);
        assertThat(drivingLesson.getMissingStudents()).contains(student);
        assertThat(drivingLesson.getBookable()).isFalse();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testCancelDrivingLessonTeacherLateCanceled() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(true);
        student.setTeacher(teacher);
        studentRepository.save(student);
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLesson.setBegin(LocalDateTime.now().plusDays(1));
        drivingLesson.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        drivingLesson.setTeacher(teacher);
        drivingLesson.setDriver(student);
        drivingLessonRepository.save(drivingLesson);

        SchoolConfiguration.setDeadlineMissedLesson(48);

        Long id = drivingLesson.getId();
        restDrivingLessonMockMvc.perform(put("/api/driving-lesson/cancel?driving_lesson_id={id}", id));

        drivingLesson = drivingLessonRepository.findById(drivingLesson.getId()).orElse(null);
        assertThat(drivingLesson).isNotNull();
        assertThat(drivingLesson.getPickup()).isNull();
        assertThat(drivingLesson.getDestination()).isNull();
        assertThat(drivingLesson.getDriver()).isNull();
        assertThat(drivingLesson.getMissingStudents().size()).isEqualTo(1);
        assertThat(drivingLesson.getLateMissingStudents().size()).isEqualTo(0);
        assertThat(drivingLesson.getMissingStudents()).contains(student);
        assertThat(drivingLesson.getBookable()).isTrue();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testCancelDrivingLessonStudent() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(false);
        student.setTeacher(teacher);
        studentRepository.save(student);
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLesson.setBegin(LocalDateTime.now().plusDays(1));
        drivingLesson.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        drivingLesson.setTeacher(teacher);
        drivingLesson.setDriver(student);
        drivingLessonRepository.save(drivingLesson);

        SchoolConfiguration.setDeadlineMissedLesson(0);

        Long id = drivingLesson.getId();
        restDrivingLessonMockMvc.perform(put("/api/driving-lesson/cancel?driving_lesson_id={id}", id));

        drivingLesson = drivingLessonRepository.findById(drivingLesson.getId()).orElse(null);
        assertThat(drivingLesson).isNotNull();
        assertThat(drivingLesson.getPickup()).isNull();
        assertThat(drivingLesson.getDestination()).isNull();
        assertThat(drivingLesson.getDriver()).isNull();
        assertThat(drivingLesson.getMissingStudents().size()).isEqualTo(1);
        assertThat(drivingLesson.getLateMissingStudents().size()).isEqualTo(0);
        assertThat(drivingLesson.getMissingStudents()).contains(student);
        assertThat(drivingLesson.getBookable()).isTrue();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testCancelDrivingLessonStudentBookableFalse() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(false);
        student.setTeacher(teacher);
        studentRepository.save(student);
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLesson.setBegin(LocalDateTime.now().plusDays(1));
        drivingLesson.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        drivingLesson.setTeacher(teacher);
        drivingLesson.setDriver(student);
        drivingLessonRepository.save(drivingLesson);

        SchoolConfiguration.setDeadlineMissedLesson(0);

        Long id = drivingLesson.getId();
        restDrivingLessonMockMvc.perform(put("/api/driving-lesson/cancel?driving_lesson_id={id}&bookable=false", id));

        drivingLesson = drivingLessonRepository.findById(drivingLesson.getId()).orElse(null);
        assertThat(drivingLesson).isNotNull();
        assertThat(drivingLesson.getPickup()).isNull();
        assertThat(drivingLesson.getDestination()).isNull();
        assertThat(drivingLesson.getDriver()).isNull();
        assertThat(drivingLesson.getMissingStudents().size()).isEqualTo(1);
        assertThat(drivingLesson.getLateMissingStudents().size()).isEqualTo(0);
        assertThat(drivingLesson.getMissingStudents()).contains(student);
        assertThat(drivingLesson.getBookable()).isTrue();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testCancelDrivingLessonStudentLateCanceled() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        teacherRepository.save(teacher);
        Student student = entityCreationHelper.createEntityStudent(false);
        student.setTeacher(teacher);
        studentRepository.save(student);
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLesson.setBegin(LocalDateTime.now().plusDays(1));
        drivingLesson.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        drivingLesson.setTeacher(teacher);
        drivingLesson.setDriver(student);
        drivingLessonRepository.save(drivingLesson);

        SchoolConfiguration.setDeadlineMissedLesson(48);

        Long id = drivingLesson.getId();
        restDrivingLessonMockMvc.perform(put("/api/driving-lesson/cancel?driving_lesson_id={id}&bookable=false", id));

        drivingLesson = drivingLessonRepository.findById(drivingLesson.getId()).orElse(null);
        assertThat(drivingLesson).isNotNull();
        assertThat(drivingLesson.getPickup()).isNull();
        assertThat(drivingLesson.getDestination()).isNull();
        assertThat(drivingLesson.getDriver()).isNull();
        assertThat(drivingLesson.getMissingStudents().size()).isEqualTo(0);
        assertThat(drivingLesson.getLateMissingStudents().size()).isEqualTo(1);
        assertThat(drivingLesson.getLateMissingStudents()).contains(student);
        assertThat(drivingLesson.getBookable()).isTrue();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void updateDrivingLesson() throws Exception {
        // Initialize the database
        DrivingLesson drivingLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLessonRepository.saveAndFlush(drivingLesson);

        int databaseSizeBeforeUpdate = drivingLessonRepository.findAll().size();

        // Update the drivingLesson
        DrivingLessonDTO drivingLessonDTO = new DrivingLessonDTO(drivingLesson);
        drivingLessonDTO.setDestination(entityCreationHelper.createEntityLocation());
        drivingLessonDTO.setPickup(entityCreationHelper.createAlterEntityLocation());
        drivingLessonDTO.setBookable(false);
        drivingLessonDTO.setLessonType(DrivingLessonType.AUTOBAHN);

        restDrivingLessonMockMvc.perform(put("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drivingLessonDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lessonType").value(drivingLessonDTO.getLessonType().toString()))
            .andExpect(jsonPath("$.bookable").value(drivingLessonDTO.getBookable()));

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeUpdate);
        DrivingLesson testDrivingLesson = drivingLessonList.get(drivingLessonList.size() - 1);
        assertThat(testDrivingLesson.getLessonType()).isEqualTo(drivingLessonDTO.getLessonType());
        assertThat(testDrivingLesson.getPickup().checkSimilar(drivingLessonDTO.getPickup())).isTrue();
        assertThat(testDrivingLesson.getDestination().checkSimilar(drivingLessonDTO.getDestination())).isTrue();
        assertThat(testDrivingLesson.getBookable()).isFalse();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testUpdateDrivingLessonForbidden() throws Exception {
        restDrivingLessonMockMvc.perform(put("/api/driving-lessons").contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new DrivingLessonDTO()))).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetAllAssignedDrivingLessons() throws Exception {
        Student student = entityCreationHelper.createEntityStudent(false);
        studentRepository.save(student);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        assignedLesson.setDriver(student);
        DrivingLesson unassignedLesson = entityCreationHelper.createEntityDrivingLesson(true);
        drivingLessonRepository.save(assignedLesson);
        drivingLessonRepository.save(unassignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lessons"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetDrivingLesson() throws Exception {
        DrivingLesson unassignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        drivingLessonRepository.save(unassignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lessons/" + unassignedLesson.getId()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(unassignedLesson.getId().intValue()))
            .andExpect(jsonPath("$.lessonType").value(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString()));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetAllAssignedDrivingLessonsByTeacherId() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        Student student = entityCreationHelper.createEntityStudent(true);
        studentRepository.save(student);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        assignedLesson.setDriver(student);
        teacher.addDrivingLesson(assignedLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(assignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/teacher/" + assignedLesson.getTeacher().getId()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetAllDrivingLessonsByStudentId() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        Student student = entityCreationHelper.createEntityStudent(true);
        studentRepository.save(student);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        teacher.addDrivingLesson(assignedLesson);
        student.addDrivingLessons(assignedLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(assignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/student/" + assignedLesson.getDriver().getId()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testGetAllDrivingLessonsForStudent() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        Student student = entityCreationHelper.createEntityStudent(false);
        studentRepository.save(student);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        student.addDrivingLessons(assignedLesson);
        teacher.addDrivingLesson(assignedLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(assignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/student"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetAllAssignedDrivingLessonsForTeacher() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        Student student = entityCreationHelper.createEntityStudent(true);
        studentRepository.save(student);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        student.addDrivingLessons(assignedLesson);
        teacher.addDrivingLesson(assignedLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(assignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/teacher"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetAllUnassignedDrivingLessonsByTeacherId() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        assignedLesson.setBegin(LocalDateTime.now().plusDays(2));
        assignedLesson.setEnd(LocalDateTime.now().plusDays(2).plusHours(2));
        teacher.addDrivingLesson(assignedLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(assignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/unassigned/" + teacher.getId()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetAllUnassignedDrivingLessonsTeacher() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        assignedLesson.setBegin(LocalDateTime.now().plusDays(2));
        assignedLesson.setEnd(LocalDateTime.now().plusDays(2).plusHours(2));
        teacher.addDrivingLesson(assignedLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(assignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/unassigned"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void testGetAllUnassignedDrivingLessonsAdmin() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        assignedLesson.setBegin(LocalDateTime.now().plusDays(2));
        assignedLesson.setEnd(LocalDateTime.now().plusDays(2).plusHours(2));
        teacher.addDrivingLesson(assignedLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(assignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/unassigned"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testGetAllUnassignedDrivingLessonsStudent() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        Student student = entityCreationHelper.createEntityStudent(false);
        teacher.addStudent(student);
        studentRepository.save(student);
        DrivingLesson assignedLesson = entityCreationHelper.createEntityDrivingLesson(false);
        assignedLesson.setBegin(LocalDateTime.now().plusDays(2));
        assignedLesson.setEnd(LocalDateTime.now().plusDays(2).plusHours(2));
        teacher.addDrivingLesson(assignedLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(assignedLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/unassigned"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignedLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testGetAllCanceledDrivingLessonsByLogin() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        Student student = entityCreationHelper.createEntityStudent(false);
        teacher.addStudent(student);
        studentRepository.save(student);
        DrivingLesson canceledLesson = entityCreationHelper.createEntityDrivingLesson(false);
        canceledLesson.addMissingStudent(student);
        teacher.addDrivingLesson(canceledLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(canceledLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/canceled?late=false"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(canceledLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testGetAllLateCanceledDrivingLessonsByLogin() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        Student student = entityCreationHelper.createEntityStudent(false);
        teacher.addStudent(student);
        studentRepository.save(student);
        DrivingLesson canceledLesson = entityCreationHelper.createEntityDrivingLesson(false);
        canceledLesson.addLateMissingStudent(student);
        teacher.addDrivingLesson(canceledLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(canceledLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/canceled?late=true"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(canceledLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetAllCanceledDrivingLessonsById() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        Student student = entityCreationHelper.createEntityStudent(false);
        teacher.addStudent(student);
        studentRepository.save(student);
        DrivingLesson canceledLesson = entityCreationHelper.createEntityDrivingLesson(false);
        canceledLesson.addMissingStudent(student);
        teacher.addDrivingLesson(canceledLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(canceledLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/canceled/" + student.getId() + "?late=false"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(canceledLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetAllLateCanceledDrivingLessonsById() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(true);
        Student student = entityCreationHelper.createEntityStudent(false);
        teacher.addStudent(student);
        studentRepository.save(student);
        DrivingLesson canceledLesson = entityCreationHelper.createEntityDrivingLesson(false);
        canceledLesson.addLateMissingStudent(student);
        teacher.addDrivingLesson(canceledLesson);
        teacherRepository.save(teacher);
        drivingLessonRepository.save(canceledLesson);

        restDrivingLessonMockMvc.perform(get("/api/driving-lesson/canceled/" + student.getId() + "?late=true"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(canceledLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(entityCreationHelper.getDEFAULT_LESSON_TYPE().toString())));
    }

    @Test
    @Transactional
    public void getNonExistingDrivingLesson() throws Exception {
        // Get the drivingLesson
        restDrivingLessonMockMvc.perform(get("/api/driving-lessons/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void updateNonExistingDrivingLesson() throws Exception {
        int databaseSizeBeforeUpdate = drivingLessonRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDrivingLessonMockMvc.perform(put("/api/driving-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new DrivingLessonDTO())))
            .andExpect(status().isBadRequest());

        // Validate the DrivingLesson in the database
        List<DrivingLesson> drivingLessonList = drivingLessonRepository.findAll();
        assertThat(drivingLessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DrivingLesson.class);
        DrivingLesson drivingLesson1 = new DrivingLesson();
        drivingLesson1.setId(1L);
        DrivingLesson drivingLesson2 = new DrivingLesson();
        drivingLesson2.setId(drivingLesson1.getId());
        assertThat(drivingLesson1).isEqualTo(drivingLesson2);
        drivingLesson2.setId(2L);
        assertThat(drivingLesson1).isNotEqualTo(drivingLesson2);
        drivingLesson1.setId(null);
        assertThat(drivingLesson1).isNotEqualTo(drivingLesson2);
    }
}
