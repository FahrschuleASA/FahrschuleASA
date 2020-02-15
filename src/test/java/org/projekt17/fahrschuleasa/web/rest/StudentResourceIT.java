package org.projekt17.fahrschuleasa.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.repository.UserRepository;
import org.projekt17.fahrschuleasa.service.dto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Integration tests for the {@link StudentResource} REST controller.
 */
@SpringBootTest(classes = FahrschuleAsaApp.class)
public class StudentResourceIT {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restStudentMockMvc;

    private EntityCreationHelper entityCreationHelper;

    private Student student;

    private Teacher teacher;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        entityCreationHelper = new EntityCreationHelper();
        MockitoAnnotations.initMocks(this);
        this.restStudentMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Transactional
    public void saveAndFlushStudentAndUser(){
        userRepository.saveAndFlush(student.getUser());
        studentRepository.saveAndFlush(student);
    }

    @Transactional
    public void saveAndFlushTeacherAndUser(){
        userRepository.saveAndFlush(teacher.getUser());
        teacherRepository.saveAndFlush(teacher);
    }

    @BeforeEach
    public void initTest() {
        student = entityCreationHelper.createEntityStudent(false);
        teacher = entityCreationHelper.createEntityTeacher(true);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void createStudentByAdmin() throws Exception {
        int databaseSizeBeforeCreate = studentRepository.findAll().size();

        StudentDTO studentDTO = new StudentDTO(student);
        studentDTO.setId(Long.MAX_VALUE);
        // check for BadRequest if studentDTO.getId() != null
        restStudentMockMvc.perform(post("/api/students/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isBadRequest());

        studentDTO = new StudentDTO(student);
        studentDTO.getUser().setId(Long.MAX_VALUE);
        // check for Bad Request if studentDTO.getUser.getId() != null
        restStudentMockMvc.perform(post("/api/students/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isBadRequest());

        studentDTO = new StudentDTO(student);
        studentDTO.setTeacherId(Long.MAX_VALUE);
        // check for Bad Request if the given teacher does not exist
        restStudentMockMvc.perform(post("/api/students/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isBadRequest());
        teacherRepository.findAll().forEach(t -> assertThat(t.getId()).isNotEqualTo(Long.MAX_VALUE));

        // Create the Student
        saveAndFlushTeacherAndUser();
        studentDTO = new StudentDTO(student);
        studentDTO.setTeacherId(teacher.getId());
        restStudentMockMvc.perform(post("/api/students/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.teacherId").value(teacher.getId()))
            .andExpect(jsonPath("$.changedPreferences").value(entityCreationHelper.getDEFAULT_CHANGED_PREFERENCES()))
            .andExpect(jsonPath("$.readyForTheory").value(entityCreationHelper.getDEFAULT_READY_FOR_THEORY()))
            .andExpect(jsonPath("$.category").value(entityCreationHelper.getDEFAULT_CATEGORY().toString()))
            .andExpect(jsonPath("$.address.street").value(student.getAddress().getStreet()));
        assertThat(studentRepository.findAll()).hasSize(databaseSizeBeforeCreate+1);
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).getCategory()).isEqualTo(entityCreationHelper.getDEFAULT_CATEGORY());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).isReadyForTheory()).isEqualTo(entityCreationHelper.getDEFAULT_READY_FOR_THEORY());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).getWantedLessons()).isEqualTo(SchoolConfiguration.getInitialLessons());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).isChangedPreferences()).isEqualTo(entityCreationHelper.getDEFAULT_CHANGED_PREFERENCES());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createStudetnByTeacher() throws Exception {
        int databaseSizeBeforeCreate = studentRepository.findAll().size();
        StudentDTO studentDTO = new StudentDTO(student);
        teacherRepository.saveAndFlush(teacher);
        studentDTO = new StudentDTO(student);
        studentDTO.setTeacherId(teacher.getId());
        restStudentMockMvc.perform(post("/api/students/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.teacherId").value(teacher.getId()))
            .andExpect(jsonPath("$.changedPreferences").value(entityCreationHelper.getDEFAULT_CHANGED_PREFERENCES()))
            .andExpect(jsonPath("$.readyForTheory").value(entityCreationHelper.getDEFAULT_READY_FOR_THEORY()))
            .andExpect(jsonPath("$.category").value(entityCreationHelper.getDEFAULT_CATEGORY().toString()))
            .andExpect(jsonPath("$.address.street").value(student.getAddress().getStreet()));
        assertThat(studentRepository.findAll()).hasSize(databaseSizeBeforeCreate+1);
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).getCategory()).isEqualTo(entityCreationHelper.getDEFAULT_CATEGORY());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).isReadyForTheory()).isEqualTo(entityCreationHelper.getDEFAULT_READY_FOR_THEORY());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).getWantedLessons()).isEqualTo(SchoolConfiguration.getInitialLessons());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeCreate).isChangedPreferences()).isEqualTo(entityCreationHelper.getDEFAULT_CHANGED_PREFERENCES());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void createStudentForbidden() throws Exception {
        restStudentMockMvc.perform(post("/api/students/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StudentDTO(student))))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void updateStudentByAdmin() throws Exception {
        // Initialize the database
        saveAndFlushTeacherAndUser();
        student.setTeacher(teacher);
        saveAndFlushStudentAndUser();
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();

        // check for bad Request if studetnDTO.getId() == null
        StudentDTO studentDTO = new StudentDTO(student);
        studentDTO.setId(null);
        restStudentMockMvc.perform(put("/api/students")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isBadRequest());

        // check for NotFound if student does not exist
        studentDTO = new StudentDTO(student);
        studentDTO.setId(Long.MAX_VALUE);
        restStudentMockMvc.perform(put("/api/students")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isNotFound());
        studentRepository.findAll().forEach(s -> assertThat(s.getId()).isNotEqualTo(Long.MAX_VALUE));

        // check for Bad Request if teacher does not exist
        studentDTO = new StudentDTO(student);
        studentDTO.setTeacherId(Long.MAX_VALUE);
        restStudentMockMvc.perform(put("/api/students")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isBadRequest());
        teacherRepository.findAll().forEach(t -> assertThat(t.getId()).isNotEqualTo(Long.MAX_VALUE));

        // Update the student
        Student updatedStudent = studentRepository.findById(student.getId()).get();
        // Disconnect from session so that the updates on updatedStudent are not directly saved in db
        em.detach(updatedStudent);
        updatedStudent
            .category(entityCreationHelper.getUPDATED_CATEGORY());
        updatedStudent.getUser().setFirstName(entityCreationHelper.getUPDATED_FIRSTNAME());
        updatedStudent.getUser().setLastName(entityCreationHelper.getUPDATED_LASTNAME());
        updatedStudent.getUser().setEmail(entityCreationHelper.getUPDATED_EMAIL());

        restStudentMockMvc.perform(put("/api/students")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StudentDTO(updatedStudent))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.category").value(entityCreationHelper.getUPDATED_CATEGORY().toString()))
            .andExpect(jsonPath("$.user.firstName").value(entityCreationHelper.getUPDATED_FIRSTNAME()))
            .andExpect(jsonPath("$.user.lastName").value(entityCreationHelper.getUPDATED_LASTNAME()))
            .andExpect(jsonPath("$.user.email").value(entityCreationHelper.getDEFAULT_EMAIL()));

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
        Student testStudent = studentList.get(studentList.size() - 1);
        assertThat(testStudent.getCategory()).isEqualTo(entityCreationHelper.getUPDATED_CATEGORY());
        assertThat(testStudent.getUser().getFirstName()).isEqualTo(entityCreationHelper.getUPDATED_FIRSTNAME());
        assertThat(testStudent.getUser().getLastName()).isEqualTo(entityCreationHelper.getUPDATED_LASTNAME());
        assertThat(testStudent.getUser().getEmail()).isEqualTo(entityCreationHelper.getDEFAULT_EMAIL());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void updateStudentByTeacher() throws Exception {
        // Initialize the database
        saveAndFlushTeacherAndUser();
        student.setTeacher(teacher);
        saveAndFlushStudentAndUser();
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();

        // Update the student
        Student updatedStudent = studentRepository.findById(student.getId()).get();
        // Disconnect from session so that the updates on updatedStudent are not directly saved in db
        em.detach(updatedStudent);
        updatedStudent
            .category(entityCreationHelper.getUPDATED_CATEGORY());
        updatedStudent.getUser().setFirstName(entityCreationHelper.getUPDATED_FIRSTNAME());
        updatedStudent.getUser().setLastName(entityCreationHelper.getUPDATED_LASTNAME());
        updatedStudent.getUser().setEmail(entityCreationHelper.getUPDATED_EMAIL());

        restStudentMockMvc.perform(put("/api/students")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StudentDTO(updatedStudent))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.category").value(entityCreationHelper.getUPDATED_CATEGORY().toString()))
            .andExpect(jsonPath("$.user.firstName").value(entityCreationHelper.getUPDATED_FIRSTNAME()))
            .andExpect(jsonPath("$.user.lastName").value(entityCreationHelper.getUPDATED_LASTNAME()))
            .andExpect(jsonPath("$.user.email").value(entityCreationHelper.getDEFAULT_EMAIL()));

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
        Student testStudent = studentList.get(studentList.size() - 1);
        assertThat(testStudent.getCategory()).isEqualTo(entityCreationHelper.getUPDATED_CATEGORY());
        assertThat(testStudent.getUser().getFirstName()).isEqualTo(entityCreationHelper.getUPDATED_FIRSTNAME());
        assertThat(testStudent.getUser().getLastName()).isEqualTo(entityCreationHelper.getUPDATED_LASTNAME());
        assertThat(testStudent.getUser().getEmail()).isEqualTo(entityCreationHelper.getDEFAULT_EMAIL());
    }
    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void updateStudentForbidden() throws Exception {
        restStudentMockMvc.perform(put("/api/students")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StudentDTO(student))))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void updateStudentSettingsByStudent() throws Exception {
        // Initialize the database
        saveAndFlushTeacherAndUser();
        student.setTeacher(teacher);
        saveAndFlushStudentAndUser();
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();

        // Update the student
        Student updatedStudent = studentRepository.findById(student.getId()).get();
        // Disconnect from session so that the updates on updatedStudent are not directly saved in db
        em.detach(updatedStudent);
        updatedStudent.active(entityCreationHelper.getUPDATED_ACTIVE());
        updatedStudent.setReadyForTheory(entityCreationHelper.getUPDATED_READY_FOR_THEORY());
        updatedStudent.setNotifyForFreeLesson(entityCreationHelper.getDEFAULT_NOTIFY_FOR_FREE_LESSON());
        updatedStudent.setWantedLessons(entityCreationHelper.getUPDATED_WANTED_LESSONS());

        restStudentMockMvc.perform(put("/api/students/settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StudentDTO(updatedStudent))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(student.getId()))
            .andExpect(jsonPath("$.readyForTheory").value(entityCreationHelper.getUPDATED_READY_FOR_THEORY()))
            .andExpect(jsonPath("$.notifyForFreeLesson").value(entityCreationHelper.getDEFAULT_NOTIFY_FOR_FREE_LESSON()))
            .andExpect(jsonPath("$.wantedLessons").value(entityCreationHelper.getDEFAULT_ALLOWED_LESSONS()))
            .andExpect(jsonPath("$.active").value(entityCreationHelper.getUPDATED_ACTIVE()));
        assertThat(studentRepository.findAll().size()).isEqualTo(databaseSizeBeforeUpdate);
        assertThat(studentRepository.findAll().get(databaseSizeBeforeUpdate-1).isActive()).isEqualTo(entityCreationHelper.getUPDATED_ACTIVE());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeUpdate-1).isReadyForTheory())
            .isEqualTo(entityCreationHelper.getUPDATED_READY_FOR_THEORY());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeUpdate-1).getNotifyForFreeLesson())
            .isEqualTo(entityCreationHelper.getDEFAULT_NOTIFY_FOR_FREE_LESSON());
        assertThat(studentRepository.findAll().get(databaseSizeBeforeUpdate-1).getWantedLessons())
            .isEqualTo(entityCreationHelper.getDEFAULT_ALLOWED_LESSONS());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void updateStudentSettingsForbidden() throws Exception {
        restStudentMockMvc.perform(put("/api/students/settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StudentDTO(student))))
            .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllStudentsByAdmin() throws Exception {
        // Initialize the database
        saveAndFlushTeacherAndUser();
        student.setTeacher(teacher);
        saveAndFlushStudentAndUser();
        int dbSize = studentRepository.findAll().size();

        // Get all the studentList
        restStudentMockMvc.perform(get("/api/students?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(dbSize)))
            .andExpect(jsonPath("$.[0].active").value(studentRepository.findAll().get(0).isActive()));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllStudentsByTeacher() throws Exception {
        // Initialize the database
        saveAndFlushTeacherAndUser();
        student.setTeacher(teacher);
        saveAndFlushStudentAndUser();
        int dbSize = studentRepository.findAll().size();

        // Get all the studentList
        restStudentMockMvc.perform(get("/api/students?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$",hasSize(dbSize)))
            .andExpect(jsonPath("$.[0].active").value(studentRepository.findAll().get(0).isActive()));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllStudentsForbidden() throws Exception {
        restStudentMockMvc.perform(get("/api/students?sort=id,desc"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllStudentsSmallByAdmin() throws Exception {
        // Initialize the database
        saveAndFlushTeacherAndUser();
        student.setTeacher(teacher);
        saveAndFlushStudentAndUser();
        int dbSize = studentRepository.findAll().size();

        student.getUser().setActivated(false);
        restStudentMockMvc.perform(get("/api/students/small")
            .param("active_only", Boolean.TRUE.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$",hasSize(dbSize-1)))
            .andExpect(jsonPath("$.active").doesNotExist())
            .andExpect(jsonPath("$.readyForTheory").doesNotExist());

        restStudentMockMvc.perform(get("/api/students/small")
        .param("active_only", Boolean.FALSE.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$",hasSize(dbSize)))
            .andExpect(jsonPath("$.active").doesNotExist())
            .andExpect(jsonPath("$.readyForTheory").doesNotExist());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllStudetnsSmallForbidden() throws Exception {
        restStudentMockMvc.perform(get("/api/students/small"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(username = "usertest", roles = "TEACHER")
    public void getAllStudentsForTeacherByTeacher() throws Exception {
        // Initialize the database
        saveAndFlushTeacherAndUser();
        saveAndFlushStudentAndUser();
        int dbSize = studentRepository.findAll().size();

        // check that nothing is returned if the teacher has not students
        restStudentMockMvc.perform(get("/api/students/teacher")
            .param("active_only", Boolean.FALSE.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$",hasSize(0)));

        // check that one item is returned if the teacher has one student
        student.setTeacher(teacher);
        student.getUser().setActivated(true);
        restStudentMockMvc.perform(get("/api/students/teacher")
            .param("active_only", Boolean.TRUE.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$",hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(student.getId()));
        assertThat(studentRepository.findAll().get(dbSize-1).getTeacher()).isEqualTo(teacher);
        assertThat(studentRepository.findAll().get(dbSize-1).isActive()).isEqualTo(true);

        // check that no student is returned if the only student of the teacher is inactive
        student.getUser().setActivated(false);
        restStudentMockMvc.perform(get("/api/students/teacher")
            .param("active_only", Boolean.TRUE.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllStudentsForTeacherForbidden() throws Exception {
        restStudentMockMvc.perform(get("/api/students/teacher")
            .param("active_only", Boolean.FALSE.toString()))
            .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getStudentByAdmin() throws Exception {
        // Initialize the database
        saveAndFlushStudentAndUser();
        int dbSize = studentRepository.findAll().size();

        // check for not found if student with given id does not exist
        restStudentMockMvc.perform(get("/api/students/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
        studentRepository.findAll().forEach(s -> assertThat(s.getId()).isNotEqualTo(Long.MAX_VALUE));

        // Get the student
        restStudentMockMvc.perform(get("/api/students/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(student.getId().intValue()))
            .andExpect(jsonPath("$.category").value(entityCreationHelper.getDEFAULT_CATEGORY().toString()))
            .andExpect(jsonPath("$.readyForTheory").value(entityCreationHelper.getDEFAULT_READY_FOR_THEORY()))
            .andExpect(jsonPath("$.wantedLessons").value(entityCreationHelper.getDEFAULT_WANTED_LESSONS()))
            .andExpect(jsonPath("$.changedPreferences").value(entityCreationHelper.getDEFAULT_CHANGED_PREFERENCES()));
        assertThat(studentRepository.findAll().get(dbSize-1).getId()).isEqualTo(student.getId());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getStudentByTeacher() throws Exception {
        // Initialize the database
        saveAndFlushStudentAndUser();
        int dbSize = studentRepository.findAll().size();

        // Get the student
        restStudentMockMvc.perform(get("/api/students/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(student.getId().intValue()))
            .andExpect(jsonPath("$.category").value(entityCreationHelper.getDEFAULT_CATEGORY().toString()))
            .andExpect(jsonPath("$.readyForTheory").value(entityCreationHelper.getDEFAULT_READY_FOR_THEORY()))
            .andExpect(jsonPath("$.wantedLessons").value(entityCreationHelper.getDEFAULT_WANTED_LESSONS()))
            .andExpect(jsonPath("$.changedPreferences").value(entityCreationHelper.getDEFAULT_CHANGED_PREFERENCES()));
        assertThat(studentRepository.findAll().get(dbSize-1).getId()).isEqualTo(student.getId());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getStudentForbidden() throws Exception {
        restStudentMockMvc.perform(get("/api/students/{id}", Long.MAX_VALUE))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getStudentForStudentByStudent() throws Exception {
        // Initialize the database
        saveAndFlushStudentAndUser();

        restStudentMockMvc.perform(get("/api/student"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(student.getId().intValue()))
            .andExpect(jsonPath("$.category").value(entityCreationHelper.getDEFAULT_CATEGORY().toString()))
            .andExpect(jsonPath("$.readyForTheory").value(entityCreationHelper.getDEFAULT_READY_FOR_THEORY()))
            .andExpect(jsonPath("$.wantedLessons").value(entityCreationHelper.getDEFAULT_WANTED_LESSONS()))
            .andExpect(jsonPath("$.changedPreferences").value(entityCreationHelper.getDEFAULT_CHANGED_PREFERENCES()));;
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getStudentForStudentForbidden() throws Exception {
        restStudentMockMvc.perform(get("/api/student"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void deleteStudentByAdmin() throws Exception {
        // Initialize the database
        saveAndFlushTeacherAndUser();
        student.setTeacher(teacher);
        saveAndFlushStudentAndUser();
        int dbSize = studentRepository.findAll().size();

        // check for internal Server Error if student with id does not exist
        restStudentMockMvc.perform(delete("/api/students/{id}", Long.MAX_VALUE))
            .andExpect(status().isInternalServerError());

        // Delete the student
        restStudentMockMvc.perform(delete("/api/students/{id}", student.getId()))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(dbSize);
        assertThat(studentList.get(dbSize-1).getId()).isEqualTo(student.getId());
        assertThat(studentList.get(dbSize-1).getUser().getActivated()).isEqualTo(false);
        assertThat(studentList.get(dbSize-1).getDeactivatedUntil()).isNull();
        assertThat(studentList.get(dbSize-1).isActive()).isEqualTo(false);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Student.class);
        Student student1 = new Student();
        student1.setId(1L);
        Student student2 = new Student();
        student2.setId(student1.getId());
        assertThat(student1).isEqualTo(student2);
        student2.setId(2L);
        assertThat(student1).isNotEqualTo(student2);
        student1.setId(null);
        assertThat(student1).isNotEqualTo(student2);
    }
}
