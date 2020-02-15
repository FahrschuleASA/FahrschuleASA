package org.projekt17.fahrschuleasa.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.repository.LocationRepository;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.repository.UserRepository;
import org.projekt17.fahrschuleasa.service.TeacherService;
import org.projekt17.fahrschuleasa.service.UserService;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.service.dto.TeacherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link TeacherResource} REST controller.
 */
@SpringBootTest(classes = FahrschuleAsaApp.class)
public class TeacherResourceIT {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager em;

    private MockMvc restTeacherMockMvc;

    private Teacher teacher;

    private EntityCreationHelper entityCreationHelper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        entityCreationHelper = new EntityCreationHelper();
        MockitoAnnotations.initMocks(this);
        this.restTeacherMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Transactional
    public void saveAndFlushLocUsTeach(){
        locationRepository.saveAndFlush(teacher.getAddress());
        userRepository.saveAndFlush(teacher.getUser());
        teacherRepository.saveAndFlush(teacher);
    }

    @BeforeEach
    public void initTest() {
        teacher = entityCreationHelper.createEntityTeacher(false);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void createTeacher() throws Exception {
        int databaseSizeBeforeCreate = teacherRepository.findAll().size();

        // check for bad request if teacherDTO has an id
        TeacherDTO test = new TeacherDTO(teacher);
        test.setId(1L);
        restTeacherMockMvc.perform(post("/api/teachers/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(test)))
            .andExpect(status().isBadRequest());
        assertThat(teacherRepository.findAll().size()).isEqualTo(databaseSizeBeforeCreate);
        test.setId(null);

        // check for bad request if teacherDTO.user has an id
        test.getUser().setId(1L);
        restTeacherMockMvc.perform(post("/api/teachers/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(test)))
            .andExpect(status().isBadRequest());
        assertThat(teacherRepository.findAll().size()).isEqualTo(databaseSizeBeforeCreate);
        test.getUser().setId(null);

        // Create a Teacher
        restTeacherMockMvc.perform(post("/api/teachers/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TeacherDTO(teacher))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.changedTimeSlots").value(teacher.isChangedTimeSlots()))
            .andExpect(jsonPath("$.schoolOwner").value(teacher.isSchoolOwner()));

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeCreate + 1);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.isChangedTimeSlots()).isEqualTo(entityCreationHelper.getDEFAULT_CHANGED_TIME_SLOTS());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createTeacherForbidden() throws Exception {
        restTeacherMockMvc.perform(post("/api/teachers/create")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TeacherDTO(teacher))))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void updateTeacherByAdmin() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher
        Teacher updatedTeacher = teacherRepository.findById(teacher.getId()).get();
        // Disconnect from session so that the updates on updatedTeacher are not directly saved in db
        em.detach(updatedTeacher);
        updatedTeacher.setBirthdate(entityCreationHelper.getUPDATED_DATE_OF_BIRTH());
        updatedTeacher.getUser().setFirstName(entityCreationHelper.getUPDATED_FIRSTNAME());
        updatedTeacher.getUser().setLastName(entityCreationHelper.getUPDATED_LASTNAME());
        updatedTeacher.getUser().setEmail(entityCreationHelper.getUPDATED_EMAIL());
        MyAccountDTO teacherDTO = new MyAccountDTO(updatedTeacher);
        teacherDTO.setId(null);

        // check for Bad Request if myAccountId == null
        restTeacherMockMvc.perform(put("/api/teachers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isBadRequest());
        assertThat(teacherRepository.findAll().size()).isEqualTo(databaseSizeBeforeUpdate);
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getFirstName())
            .isEqualTo(entityCreationHelper.getDEFAULT_FIRSTNAME());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getLastName())
            .isEqualTo(entityCreationHelper.getDEFAULT_LASTNAME());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getEmail())
            .isEqualTo(entityCreationHelper.getDEFAULT_EMAIL());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getBirthdate())
            .isEqualTo(entityCreationHelper.getDEFAULT_DATE_OF_BIRTH());

        teacherDTO.setId(Long.MAX_VALUE);
        // check no update of teacher when wrong myAccountId
        restTeacherMockMvc.perform(put("/api/teachers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isNotFound());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getFirstName())
            .isEqualTo(entityCreationHelper.getDEFAULT_FIRSTNAME());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getLastName())
            .isEqualTo(entityCreationHelper.getDEFAULT_LASTNAME());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getEmail())
            .isEqualTo(entityCreationHelper.getDEFAULT_EMAIL());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getBirthdate())
            .isEqualTo(entityCreationHelper.getDEFAULT_DATE_OF_BIRTH());

        teacherDTO.setId(teacher.getId());
        // check correct update
        restTeacherMockMvc.perform(put("/api/teachers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.firstName").value(entityCreationHelper.getUPDATED_FIRSTNAME()))
            .andExpect(jsonPath("$.user.lastName").value(entityCreationHelper.getUPDATED_LASTNAME()))
            .andExpect(jsonPath("$.user.email").value(entityCreationHelper.getDEFAULT_EMAIL()));
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getFirstName())
            .isEqualTo(entityCreationHelper.getUPDATED_FIRSTNAME());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getLastName())
            .isEqualTo(entityCreationHelper.getUPDATED_LASTNAME());
        // check here that the e mail is not directly overwritten
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getEmail())
            .isEqualTo(entityCreationHelper.getDEFAULT_EMAIL());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getBirthdate())
            .isEqualTo(entityCreationHelper.getUPDATED_DATE_OF_BIRTH());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void updateTeacherByTeacher() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher
        Teacher updatedTeacher = teacherRepository.findById(teacher.getId()).get();
        // Disconnect from session so that the updates on updatedTeacher are not directly saved in db
        em.detach(updatedTeacher);
        updatedTeacher.setBirthdate(entityCreationHelper.getUPDATED_DATE_OF_BIRTH());
        updatedTeacher.getUser().setFirstName(entityCreationHelper.getUPDATED_FIRSTNAME());
        updatedTeacher.getUser().setLastName(entityCreationHelper.getUPDATED_LASTNAME());
        updatedTeacher.getUser().setEmail(entityCreationHelper.getUPDATED_EMAIL());
        MyAccountDTO teacherDTO = new MyAccountDTO(updatedTeacher);

        // check correct update
        restTeacherMockMvc.perform(put("/api/teachers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.firstName").value(entityCreationHelper.getUPDATED_FIRSTNAME()))
            .andExpect(jsonPath("$.user.lastName").value(entityCreationHelper.getUPDATED_LASTNAME()))
            .andExpect(jsonPath("$.user.email").value(entityCreationHelper.getDEFAULT_EMAIL()));
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getFirstName())
            .isEqualTo(entityCreationHelper.getUPDATED_FIRSTNAME());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getLastName())
            .isEqualTo(entityCreationHelper.getUPDATED_LASTNAME());
        // check here that the e mail is not directly overwritten
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getUser().getEmail())
            .isEqualTo(entityCreationHelper.getDEFAULT_EMAIL());
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getBirthdate())
            .isEqualTo(entityCreationHelper.getUPDATED_DATE_OF_BIRTH());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void updateTeacherForbidden() throws Exception {
        restTeacherMockMvc.perform(put("/api/teachers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TeacherDTO(teacher))))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void updateTeacherSettingsByAdmin() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher
        Teacher updatedTeacher = teacherRepository.findById(teacher.getId()).get();
        // Disconnect from session so that the updates on updatedTeacher are not directly saved in db
        em.detach(updatedTeacher);
        MyAccountDTO teacherDTO = new MyAccountDTO(updatedTeacher);
        teacherDTO.setActive(false);
        teacherDTO.setDeactivatedDaysLeft(10);
        teacherDTO.setId(null);

        // check for BadRequest if MyAccountId == null
        restTeacherMockMvc.perform(put("/api/teachers/settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isBadRequest());

        teacherDTO.setId(Long.MAX_VALUE);
        // check for not found if teacherId not exists
        restTeacherMockMvc.perform(put("/api/teachers/settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isNotFound());

        // check correct update
        teacherDTO.setId(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getId());
        restTeacherMockMvc.perform(put("/api/teachers/settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate-1).isActive()).isEqualTo(false);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void updateTeacherSettingsByTeacher() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher
        Teacher updatedTeacher = teacherRepository.findById(teacher.getId()).get();
        // Disconnect from session so that the updates on updatedTeacher are not directly saved in db
        em.detach(updatedTeacher);
        MyAccountDTO teacherDTO = new MyAccountDTO(updatedTeacher);
        teacherDTO.setActive(false);
        teacherDTO.setDeactivatedDaysLeft(10);

        // check correct update
        teacherDTO.setId(teacherRepository.findAll().get(databaseSizeBeforeUpdate - 1).getId());
        restTeacherMockMvc.perform(put("/api/teachers/settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeUpdate-1).isActive()).isEqualTo(false);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void updateTeacherSettingsForbidden() throws Exception {
        restTeacherMockMvc.perform(put("/api/teachers/settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new MyAccountDTO(teacher))))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void deleteTeacherByAdmin() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();

        int databaseSizeBeforeDelete = teacherRepository.findAll().size();

        // Delete the teacher
        restTeacherMockMvc.perform(delete("/api/teachers/{id}", teacher.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());
        assertThat(teacherRepository.findAll().size()).isEqualTo(databaseSizeBeforeDelete);
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeDelete-1).getUser().getActivated()).isEqualTo(false);
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeDelete-1).isActive()).isEqualTo(false);
        assertThat(teacherRepository.findAll().get(databaseSizeBeforeDelete-1).getDeactivatedUntil()).isNull();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void deleteTeacherForbiddenT() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        restTeacherMockMvc.perform(delete("/api/teachers/{id}", teacher.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void deleteTeacherForbiddenS() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        restTeacherMockMvc.perform(delete("/api/teachers/{id}", teacher.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllTeachersByAdmin() throws Exception{
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        teacher.getUser().setActivated(false);
        int dbsize = teacherRepository.findAll().size();

        restTeacherMockMvc.perform(get("/api/teachers", true)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(dbsize-1)));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllTeachersByTeacher() throws Exception{
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        int dbsize = teacherRepository.findAll().size();

        restTeacherMockMvc.perform(get("/api/teachers", false)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(dbsize)));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllTeachersForbidden() throws Exception{
        restTeacherMockMvc.perform(get("/api/teachers", true)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getTeacherByAdmin() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        int dbsize = teacherRepository.findAll().size();

        // check for non existing teacher id
        restTeacherMockMvc.perform(get("/api/teachers/{id}", Long.MAX_VALUE)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
        teacherRepository.findAll().forEach(t -> assertThat(t.getId()).isNotEqualTo(Long.MAX_VALUE));

        // check correct
        restTeacherMockMvc.perform(get("/api/teachers/{id}", teacher.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(teacher.getId()))
            .andExpect(jsonPath("$.changedTimeSlots").value(teacher.isChangedTimeSlots()))
            .andExpect(jsonPath("$.user.firstName").value(teacher.getUser().getFirstName()))
            .andExpect(jsonPath("$.user.lastName").value(teacher.getUser().getLastName()))
            .andExpect(jsonPath("$.user.email").value(teacher.getUser().getEmail()));
        assertThat(teacherRepository.findAll().get(dbsize-1)).isEqualTo(teacher);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getTeacherByTeacher() throws Exception {
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        int dbsize = teacherRepository.findAll().size();

        // check correct
        restTeacherMockMvc.perform(get("/api/teachers/{id}", teacher.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(teacher.getId()))
            .andExpect(jsonPath("$.changedTimeSlots").value(teacher.isChangedTimeSlots()))
            .andExpect(jsonPath("$.user.firstName").value(teacher.getUser().getFirstName()))
            .andExpect(jsonPath("$.user.lastName").value(teacher.getUser().getLastName()))
            .andExpect(jsonPath("$.user.email").value(teacher.getUser().getEmail()));
        assertThat(teacherRepository.findAll().get(dbsize-1)).isEqualTo(teacher);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getTeacherForbidden() throws Exception {
        restTeacherMockMvc.perform(get("/api/teachers", Long.MAX_VALUE)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getCurrentTeacherByTeacher() throws Exception{
        // Initialize the database
        this.saveAndFlushLocUsTeach();
        int dbsize = teacherRepository.findAll().size();

        // check correct
        restTeacherMockMvc.perform(get("/api/teacher")
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(teacher.getId()))
            .andExpect(jsonPath("$.changedTimeSlots").value(teacher.isChangedTimeSlots()))
            .andExpect(jsonPath("$.user.firstName").value(teacher.getUser().getFirstName()))
            .andExpect(jsonPath("$.user.lastName").value(teacher.getUser().getLastName()))
            .andExpect(jsonPath("$.user.email").value(teacher.getUser().getEmail()));
        assertThat(teacherRepository.findAll().get(dbsize-1)).isEqualTo(teacher);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getCurrentTeacherForbidden() throws Exception {
        restTeacherMockMvc.perform(get("/api/teacher")
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(username = "usertest", roles = "STUDENT")
    public void getTeacherForStudentByStudent() throws Exception{
        // Initialize the database
        Student student = entityCreationHelper.createEntityStudent(true);
        this.saveAndFlushLocUsTeach();
        userRepository.saveAndFlush(student.getUser());
        studentRepository.saveAndFlush(student);
        student.setTeacher(teacher);
        int dbsize = teacherRepository.findAll().size();

        // check correct
        restTeacherMockMvc.perform(get("/api/teacher/student")
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(teacher.getId()))
            .andExpect(jsonPath("$.user.firstName").value(teacher.getUser().getFirstName()))
            .andExpect(jsonPath("$.user.lastName").value(teacher.getUser().getLastName()))
            .andExpect(jsonPath("$.user.email").value(teacher.getUser().getEmail()));
        assertThat(teacherRepository.findAll().get(dbsize-1)).isEqualTo(teacher);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getTeacherForStudentForbidden() throws Exception {
        restTeacherMockMvc.perform(get("/api/teacher/student")
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Teacher.class);
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        Teacher teacher2 = new Teacher();
        teacher2.setId(teacher1.getId());
        assertThat(teacher1).isEqualTo(teacher2);
        teacher2.setId(2L);
        assertThat(teacher1).isNotEqualTo(teacher2);
        teacher1.setId(null);
        assertThat(teacher1).isNotEqualTo(teacher2);
    }
}
