package org.projekt17.fahrschuleasa.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.TeachingDiagram;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.service.dto.EducationDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link EducationDataResource} REST controller.
 */
@SpringBootTest(classes = FahrschuleAsaApp.class)
public class EducationDataResourceIT {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    private MockMvc restMockMvc;

    private EntityCreationHelper entityCreationHelper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        entityCreationHelper = new EntityCreationHelper();
        MockitoAnnotations.initMocks(this);
        restMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private void checkEducationData(Student student, Teacher teacher, ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.studentId").value(student.getId()))
            .andExpect(jsonPath("$.basic").value(entityCreationHelper.getDEFAULT_BASIC()))
            .andExpect(jsonPath("$.advanced").value(entityCreationHelper.getDEFAULT_ADVANCED()))
            .andExpect(jsonPath("$.performance").value(entityCreationHelper.getDEFAULT_PERFORMANCE()))
            .andExpect(jsonPath("$.independence").value(entityCreationHelper.getDEFAULT_INDEPENDENCE()))
            .andExpect(jsonPath("$.overland").value(entityCreationHelper.getDEFAULT_OVERLAND()))
            .andExpect(jsonPath("$.autobahn").value(entityCreationHelper.getDEFAULT_AUTOBAHN()))
            .andExpect(jsonPath("$.night").value(entityCreationHelper.getDEFAULT_NIGHT()))
            .andExpect(jsonPath("$.basicCount").value(1))
            .andExpect(jsonPath("$.overlandCount").value(0))
            .andExpect(jsonPath("$.autobahnCount").value(0))
            .andExpect(jsonPath("$.nightCount").value(0))
            .andExpect(jsonPath("$.drivingLessonsCount").value(1))
            .andExpect(jsonPath("$.missedDrivingLessonsCount").value(1))
            .andExpect(jsonPath("$.lateMissedDrivingLessonsCount").value(1))
            .andExpect(jsonPath("$.theoryLessonsCount").value(1))
            .andExpect(jsonPath("$.drivingCategory").value(entityCreationHelper.getDEFAULT_CATEGORY().toString()))
            .andExpect(jsonPath("$.readyForTheory").value(entityCreationHelper.getDEFAULT_READY_FOR_THEORY()))
            .andExpect(jsonPath("$.missionAccomplished").value(entityCreationHelper.getDEFAULT_MISSION_ACCOMPLISHED()))
            .andExpect(jsonPath("$.wantedLessons").value(entityCreationHelper.getDEFAULT_WANTED_LESSONS()))
            .andExpect(jsonPath("$.allowedLessons").value(entityCreationHelper.getDEFAULT_ALLOWED_LESSONS()))
            .andExpect(jsonPath("$.teacherId").value(teacher.getId()));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testGetEducationDataForStudent() throws Exception {
        Student student = entityCreationHelper.createEntityStudentWithLessons(false);
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacher.getUser().setLogin("other");

        teacherRepository.save(teacher);
        student.setTeacher(teacher);
        studentRepository.save(student);

        ResultActions resultActions = restMockMvc.perform(get("/api/education-data").accept(MediaType.APPLICATION_JSON));
        checkEducationData(student, teacher, resultActions);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetEducationDataForStudentForbiddenTeacher() throws Exception {
        restMockMvc.perform(get("/api/education-data")).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void testGetEducationDataForStudentForbiddenAdmin() throws Exception {
        restMockMvc.perform(get("/api/education-data")).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testGetEducationDataByStudentId() throws Exception {
        Student student = entityCreationHelper.createEntityStudentWithLessons(false);
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacher.getUser().setLogin("other");

        teacherRepository.save(teacher);
        student.setTeacher(teacher);
        studentRepository.save(student);

        ResultActions resultActions = restMockMvc.perform(get("/api/education-data/" + student.getId()).accept(MediaType.APPLICATION_JSON));
        checkEducationData(student, teacher, resultActions);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testGetEducationDataByStudentIdForbidden() throws Exception {
        restMockMvc.perform(get("/api/education-data/1")).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void testUpdateEducationData() throws Exception {
        Student student = entityCreationHelper.createEntityStudentWithLessons(false);
        student.setDeactivatedUntil(LocalDate.of(2000, 2, 2));
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacher.getUser().setLogin("other");

        Teacher altTeacher = entityCreationHelper.createEntityTeacher(true);

        teacherRepository.save(altTeacher);
        teacherRepository.save(teacher);
        student.setTeacher(teacher);
        studentRepository.save(student);

        EducationDataDTO educationDataDTO = new EducationDataDTO();
        educationDataDTO.setStudentId(student.getId());
        educationDataDTO.setBasic(100);
        educationDataDTO.setAdvanced(100);
        educationDataDTO.setPerformance(100);
        educationDataDTO.setIndependence(100);
        educationDataDTO.setOverland(100);
        educationDataDTO.setAutobahn(100);
        educationDataDTO.setNight(100);
        educationDataDTO.setDrivingCategory(DrivingCategory.A1);
        educationDataDTO.setMissionAccomplished(true);
        educationDataDTO.setAllowedLessons(3);
        educationDataDTO.setTeacherId(altTeacher.getId());
        educationDataDTO.setReadyForTheory(true);

        //perform PUT request and check returned DTO
        restMockMvc.perform(put("/api/education-data")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(educationDataDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.studentId").value(student.getId()))
            .andExpect(jsonPath("$.basic").value(100))
            .andExpect(jsonPath("$.advanced").value(100))
            .andExpect(jsonPath("$.performance").value(100))
            .andExpect(jsonPath("$.independence").value(100))
            .andExpect(jsonPath("$.overland").value(100))
            .andExpect(jsonPath("$.autobahn").value(100))
            .andExpect(jsonPath("$.night").value(100))
            .andExpect(jsonPath("$.basicCount").value(1))
            .andExpect(jsonPath("$.overlandCount").value(0))
            .andExpect(jsonPath("$.autobahnCount").value(0))
            .andExpect(jsonPath("$.nightCount").value(0))
            .andExpect(jsonPath("$.drivingLessonsCount").value(1))
            .andExpect(jsonPath("$.missedDrivingLessonsCount").value(1))
            .andExpect(jsonPath("$.lateMissedDrivingLessonsCount").value(1))
            .andExpect(jsonPath("$.theoryLessonsCount").value(1))
            .andExpect(jsonPath("$.drivingCategory").value(DrivingCategory.A1.toString()))
            .andExpect(jsonPath("$.readyForTheory").value(entityCreationHelper.getDEFAULT_READY_FOR_THEORY()))
            .andExpect(jsonPath("$.missionAccomplished").value(true))
            .andExpect(jsonPath("$.wantedLessons").value(3))
            .andExpect(jsonPath("$.allowedLessons").value(3))
            .andExpect(jsonPath("$.teacherId").value(altTeacher.getId()));

        //check current state of database
        Optional<Student> optionalStudent = studentRepository.findById(student.getId());
        assertThat(optionalStudent.isPresent()).isTrue();
        student = optionalStudent.get();

        TeachingDiagram teachingDiagram = student.getTeachingDiagram();
        assertThat(teachingDiagram.getAdvanced()).isEqualTo(100);
        assertThat(teachingDiagram.getAutobahn()).isEqualTo(100);
        assertThat(teachingDiagram.getBasic()).isEqualTo(100);
        assertThat(teachingDiagram.getIndependence()).isEqualTo(100);
        assertThat(teachingDiagram.getNight()).isEqualTo(100);
        assertThat(teachingDiagram.getOverland()).isEqualTo(100);
        assertThat(teachingDiagram.getPerformance()).isEqualTo(100);

        assertThat(student.getCategory()).isEqualTo(DrivingCategory.A1);
        assertThat(student.isChangedPreferences()).isTrue();
        assertThat(student.getMissionAccomplished()).isTrue();
        assertThat(student.isActive()).isFalse();
        assertThat(student.getDeactivatedUntil()).isNull();
        assertThat(student.getUser().getActivated()).isFalse();
        assertThat(student.getAllowedLessons()).isEqualTo(3);
        assertThat(student.getWantedLessons()).isEqualTo(3);
        assertThat(student.getTeacher().getId()).isEqualTo(altTeacher.getId());
        assertThat(student.getDrivingLessons().size()).isEqualTo(1);
        assertThat(student.getMissedLessons().size()).isEqualTo(1);
        assertThat(student.getLateMissedLessons().size()).isEqualTo(1);
        assertThat(student.getTheoryLessons().size()).isEqualTo(1);
        assertThat(student.isReadyForTheory()).isFalse();

        educationDataDTO.setAllowedLessons(4);

        restMockMvc.perform(put("/api/education-data")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(educationDataDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.wantedLessons").value(3))
            .andExpect(jsonPath("$.allowedLessons").value(4));

        optionalStudent = studentRepository.findById(student.getId());
        assertThat(optionalStudent.isPresent()).isTrue();
        student = optionalStudent.get();

        assertThat(student.getAllowedLessons()).isEqualTo(4);
        assertThat(student.getWantedLessons()).isEqualTo(3);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void testUpdateEducationDataForbidden() throws Exception {
        restMockMvc.perform(put("/api/education-data").contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new EducationDataDTO()))).andExpect(status().isForbidden());
    }

    @Test
    @EntityCreationHelper.WithMockTeacher
    public void testUpdateEducationDataMissingStudentId() throws Exception {
        EducationDataDTO educationDataDTO = new EducationDataDTO();
        educationDataDTO.setStudentId(1L);

        restMockMvc.perform(put("/api/education-data").contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(educationDataDTO))).andExpect(status().isBadRequest());
    }

    @Test
    @EntityCreationHelper.WithMockTeacher
    public void testUpdateEducationDataMissingTeacherId() throws Exception {
        EducationDataDTO educationDataDTO = new EducationDataDTO();
        educationDataDTO.setTeacherId(1L);

        restMockMvc.perform(put("/api/education-data").contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(educationDataDTO))).andExpect(status().isBadRequest());
    }
}
