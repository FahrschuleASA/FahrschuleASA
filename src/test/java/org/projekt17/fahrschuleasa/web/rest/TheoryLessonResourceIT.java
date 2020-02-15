package org.projekt17.fahrschuleasa.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.TheoryLesson;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.repository.TheoryLessonRepository;
import org.projekt17.fahrschuleasa.repository.UserRepository;
import org.projekt17.fahrschuleasa.service.TheoryLessonService;
import org.projekt17.fahrschuleasa.service.dto.TheoryLessonDTO;
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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link TheoryLessonResource} REST controller.
 */
@SpringBootTest(classes = FahrschuleAsaApp.class)
public class TheoryLessonResourceIT {

    @Autowired
    private TheoryLessonRepository theoryLessonRepository;

    @Autowired
    private TheoryLessonService theoryLessonService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTheoryLessonMockMvc;

    private EntityCreationHelper entityCreationHelper;

    private TheoryLesson theoryLesson;

    private Student student;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        entityCreationHelper = new EntityCreationHelper();

        MockitoAnnotations.initMocks(this);
        this.restTheoryLessonMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @BeforeEach
    public void initTest() {
        student = entityCreationHelper.createEntityStudent(true);
        theoryLesson = entityCreationHelper.createEntityTheoryLesson(false);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void createTheoryLesson() throws Exception {
        // initialize database with a teacher and user
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());

        int databaseSizeBeforeCreate = theoryLessonRepository.findAll().size();

        // test that the creation with existing id throws BadRequest
        theoryLesson.setId(10L);
        restTheoryLessonMockMvc.perform(post("/api/theory-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TheoryLessonDTO(theoryLesson))))
            .andExpect(status().isBadRequest());
        // Validate the TheoryLesson in the database
        List<TheoryLesson> theoryLessonList = theoryLessonRepository.findAll();
        assertThat(theoryLessonList).hasSize(databaseSizeBeforeCreate);
        theoryLesson.setId(null);

        // test BadRequest when begin or end null
        theoryLesson.setBegin(null);
        restTheoryLessonMockMvc.perform(post("/api/theory-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TheoryLessonDTO(theoryLesson))))
            .andExpect(status().isBadRequest());

        theoryLesson.setBegin(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        theoryLesson.setEnd(null);

        restTheoryLessonMockMvc.perform(post("/api/theory-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TheoryLessonDTO(theoryLesson))))
            .andExpect(status().isBadRequest());
        theoryLesson.setEnd(entityCreationHelper.getDEFAULT_THEORY_LESSON_END());
        // Validate the TheoryLesson in the database
        theoryLessonList = theoryLessonRepository.findAll();
        assertThat(theoryLessonList).hasSize(databaseSizeBeforeCreate);

        // Create the TheoryLesson
        restTheoryLessonMockMvc.perform(post("/api/theory-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TheoryLessonDTO(theoryLesson))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(theoryLessonRepository.findAll().get(databaseSizeBeforeCreate).getId().toString()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.subject").value(theoryLesson.getSubject()));

        // Validate the TheoryLesson in the database
        theoryLessonList = theoryLessonRepository.findAll();
        assertThat(theoryLessonList).hasSize(databaseSizeBeforeCreate + 1);
        TheoryLesson testTheoryLesson = theoryLessonList.get(theoryLessonList.size() - 1);
        assertThat(testTheoryLesson.getSubject()).isEqualTo(entityCreationHelper.getDEFAULT_SUBJECT());
        assertThat(theoryLesson.getEnd()).isEqualTo(entityCreationHelper.getDEFAULT_THEORY_LESSON_END());
        assertThat(theoryLesson.getBegin()).isEqualTo(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        assertThat(theoryLesson.getTeacher().getId()).isEqualTo(theoryLesson.getTeacher().getId());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void createTheoryLessonByAdmin() throws Exception {
        // initialize database with a teacher and user
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());

        int databaseSizeBeforeCreate = theoryLessonRepository.findAll().size();

        // check for Bad request if in the case a admin calls createTheoryLesson, BadRequest is thrown
        TheoryLessonDTO theoryLessonDTO = new TheoryLessonDTO(theoryLesson);
        theoryLessonDTO.setTeacherId(null);
        restTheoryLessonMockMvc.perform(post("/api/theory-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(theoryLessonDTO)))
            .andExpect(status().isBadRequest());
        assertThat(theoryLessonRepository.findAll().size()).isEqualTo(databaseSizeBeforeCreate);

        // Create the TheoryLesson
        restTheoryLessonMockMvc.perform(post("/api/theory-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TheoryLessonDTO(theoryLesson))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(theoryLessonRepository.findAll().get(databaseSizeBeforeCreate).getId().toString()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.subject").value(theoryLesson.getSubject()));

        // Validate the TheoryLesson in the database
        List<TheoryLesson> theoryLessonList = theoryLessonRepository.findAll();
        assertThat(theoryLessonList).hasSize(databaseSizeBeforeCreate + 1);
        TheoryLesson testTheoryLesson = theoryLessonList.get(theoryLessonList.size() - 1);
        assertThat(testTheoryLesson.getSubject()).isEqualTo(entityCreationHelper.getDEFAULT_SUBJECT());
        assertThat(theoryLesson.getEnd()).isEqualTo(entityCreationHelper.getDEFAULT_THEORY_LESSON_END());
        assertThat(theoryLesson.getBegin()).isEqualTo(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        assertThat(theoryLesson.getTeacher().getId()).isEqualTo(theoryLesson.getTeacher().getId());
    }

    @Test
    @EntityCreationHelper.WithMockStudent
    public void createTheoryLessonForbidden() throws Exception {
        restTheoryLessonMockMvc.perform(post("/api/theory-lessons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new TheoryLessonDTO(theoryLesson))))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void addStudentToTheoryLessonByTeacher() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        int beforeDbSize = theoryLessonRepository.findAll().size();
        int amountStudents = theoryLesson.getStudents().size();

        // test for internal Server error if student does not exist
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/add-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", String.valueOf(Long.MAX_VALUE)))
            .andExpect(status().isInternalServerError());

        // test for notFound if timeSlot not exists
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/add-student")
            .param("lessonId", String.valueOf(Long.MAX_VALUE))
            .param("studentId", student.getId().toString()))
            .andExpect(status().isNotFound());

        // test for BadRequest if lessonId or studentId are null
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/add-student")
            .param("lessonId", "")
            .param("studentId", student.getId().toString()))
            .andExpect(status().isBadRequest());

        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/add-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", ""))
            .andExpect(status().isBadRequest());

        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/add-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", student.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().toString()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.subject").value(theoryLesson.getSubject()))
            .andExpect(jsonPath("$.students", hasSize(1)))
            .andExpect(jsonPath("$.students.[0].studentId").value(student.getId().toString()));

        assertThat(this.theoryLesson.getStudents()).hasSize(amountStudents + 1);
        assertThat(theoryLessonRepository.findAll().size()).isEqualTo(beforeDbSize);
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize - 1).getId()).isEqualTo(theoryLesson.getId());
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize - 1).getStudents().size()).isEqualTo(1);
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize - 1).getStudents()).contains(student);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void addStudentToTheoryLessonByAdmin() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        int beforeDbSize = theoryLessonRepository.findAll().size();
        int amountStudents = theoryLesson.getStudents().size();

        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/add-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", student.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().toString()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.subject").value(theoryLesson.getSubject()))
            .andExpect(jsonPath("$.students", hasSize(1)))
            .andExpect(jsonPath("$.students.[0].studentId").value(student.getId().toString()));

        assertThat(this.theoryLesson.getStudents()).hasSize(amountStudents + 1);
        assertThat(theoryLessonRepository.findAll().size()).isEqualTo(beforeDbSize);
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize - 1).getId()).isEqualTo(theoryLesson.getId());
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize - 1).getStudents().size()).isEqualTo(1);
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize - 1).getStudents()).contains(student);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void addStudentToTheoryLessonForbidden() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/add-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", student.getId().toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void changeTheoryLessonSubjectByTeacher() throws Exception {
        // initialize database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        int beforeDbSize = theoryLessonRepository.findAll().size();

        // check for NotFound if lesson with given id is not in db
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/change-subject")
            .param("lessonId", String.valueOf(Long.MAX_VALUE))
            .param("subject", entityCreationHelper.getUPDATED_SUBJECT()))
            .andExpect(status().isNotFound());

        // check for BadRequest if lessonId null
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/change-subject")
            .param("lessonId", "")
            .param("subject", entityCreationHelper.getUPDATED_SUBJECT()))
            .andExpect(status().isBadRequest());
        assertThat(theoryLessonRepository.findAll().size()).isEqualTo(beforeDbSize);

        // update the TheoryLesson
        TheoryLesson updatedTheoryLesson = theoryLessonRepository.findById(theoryLesson.getId()).get();
        // detach here to not persist the changes directly
        em.detach(updatedTheoryLesson);
        updatedTheoryLesson.setSubject(entityCreationHelper.getUPDATED_SUBJECT());

        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/change-subject")
            .param("lessonId", theoryLesson.getId().toString())
            .param("subject", entityCreationHelper.getUPDATED_SUBJECT()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().toString()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.subject").value(entityCreationHelper.getUPDATED_SUBJECT()));

        assertThat(this.theoryLessonRepository.findAll().size()).isEqualTo(beforeDbSize);
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize -1).getSubject())
            .isEqualTo(entityCreationHelper.getUPDATED_SUBJECT());
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize -1).getBegin())
            .isEqualTo(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize -1).getStudents().size())
            .isEqualTo(0);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void changeTheoryLessonSubjectByAdmin() throws Exception {
        // initialize database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        int beforeDbSize = theoryLessonRepository.findAll().size();

        // update the TheoryLesson
        TheoryLesson updatedTheoryLesson = theoryLessonRepository.findById(theoryLesson.getId()).get();
        // detach here to not persist the changes directly
        em.detach(updatedTheoryLesson);
        updatedTheoryLesson.setSubject(entityCreationHelper.getUPDATED_SUBJECT());

        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/change-subject")
            .param("lessonId", theoryLesson.getId().toString())
            .param("subject", entityCreationHelper.getUPDATED_SUBJECT()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().toString()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.subject").value(entityCreationHelper.getUPDATED_SUBJECT()));

        assertThat(this.theoryLessonRepository.findAll().size()).isEqualTo(beforeDbSize);
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize -1).getSubject())
            .isEqualTo(entityCreationHelper.getUPDATED_SUBJECT());
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize -1).getBegin())
            .isEqualTo(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize -1).getStudents().size())
            .isEqualTo(0);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void changeTheoryLessonSubjectForbidden() throws Exception {
        // initialize database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/change-subject")
            .param("lessonId", theoryLesson.getId().toString())
            .param("subject", entityCreationHelper.getUPDATED_SUBJECT()))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void removeStudentFromTheoryLessonByTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        studentRepository.saveAndFlush(student);
        theoryLessonRepository.saveAndFlush(theoryLesson);

        // add a student to the theory lesson to remove it later
        theoryLesson.addStudent(student);

        int beforeDbSize = theoryLessonRepository.findAll().size();

        // check for BadRequest if studentId null
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/remove-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", ""))
            .andExpect(status().isBadRequest());

        // check for BadRequest if lessonId null
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/remove-student")
            .param("lessonId", "")
            .param("studentId", student.getId().toString()))
            .andExpect(status().isBadRequest());

        // check for internalServerError if student wit given id not exists in db
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/remove-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", String.valueOf(Long.MAX_VALUE)))
            .andExpect(status().isInternalServerError());

        // check for notFound if theoryLesson with given id does not exist in db
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/remove-student")
            .param("lessonId", String.valueOf(Long.MAX_VALUE))
            .param("studentId", student.getId().toString()))
            .andExpect(status().isNotFound());

        assertThat(theoryLessonRepository.findAll().size()).isEqualTo(beforeDbSize);

        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/remove-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", student.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().toString()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.subject").value(theoryLesson.getSubject()))
            .andExpect(jsonPath("$.students", hasSize(0)));

        assertThat(theoryLessonRepository.findAll().size()).isEqualTo(beforeDbSize);
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize - 1).getId()).isEqualTo(theoryLesson.getId());
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize -1).getStudents()).hasSize(0);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void removeStudentFromTheoryLessonByAdmin() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        studentRepository.saveAndFlush(student);
        theoryLessonRepository.saveAndFlush(theoryLesson);

        // add a student to the theory lesson to remove it later
        theoryLesson.addStudent(student);

        int beforeDbSize = theoryLessonRepository.findAll().size();
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/remove-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", student.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().toString()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.subject").value(theoryLesson.getSubject()))
            .andExpect(jsonPath("$.students", hasSize(0)));

        assertThat(theoryLessonRepository.findAll().size()).isEqualTo(beforeDbSize);
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize - 1).getId()).isEqualTo(theoryLesson.getId());
        assertThat(theoryLessonRepository.findAll().get(beforeDbSize -1).getStudents()).hasSize(0);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void removeStudentFromTheoryLessonForbidden() throws Exception{
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        studentRepository.saveAndFlush(student);
        theoryLessonRepository.saveAndFlush(theoryLesson);
        // add a student to the theory lesson to remove it later
        theoryLesson.addStudent(student);
        restTheoryLessonMockMvc.perform(put("/api/theory-lessons/remove-student")
            .param("lessonId", theoryLesson.getId().toString())
            .param("studentId", student.getId().toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllTheoryLessonsByTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);
        // Get all the theoryLessonList
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(theoryLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(entityCreationHelper.getDEFAULT_SUBJECT())))
            .andExpect(jsonPath("$.[*]", hasSize(theoryLessonRepository.findAll().size())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllTheoryLessonsByAdmin() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);
        // Get all the theoryLessonList
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(theoryLesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(entityCreationHelper.getDEFAULT_SUBJECT())))
            .andExpect(jsonPath("$.[*]", hasSize(theoryLessonRepository.findAll().size())));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllTheoryLessonsForbidden() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);
        // Get all the theoryLessonList
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons?sort=id,desc"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getTheoryLessonByTeacher() throws Exception {
        // Initialize the database
        Teacher teacher = theoryLesson.getTeacher();
        teacherRepository.saveAndFlush(teacher);
        theoryLessonRepository.saveAndFlush(theoryLesson);
        int dbSize = theoryLessonRepository.findAll().size();

        // check whether nothing is returned if theoryLesson with given id does not exist
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
        theoryLessonRepository.findAll().forEach(l -> assertThat(l.getId()).isNotEqualTo(Long.MAX_VALUE));

        // Get the theoryLesson
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/{id}", theoryLesson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(entityCreationHelper.getDEFAULT_SUBJECT()))
            .andExpect(jsonPath("$.teacherId").value(teacher.getId()));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getTeacher()).isEqualTo(teacher);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getTheoryLessonByAdmin() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);
        // Get the theoryLesson
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/{id}", theoryLesson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(entityCreationHelper.getDEFAULT_SUBJECT()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId()));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getTheoryLessonByStudent() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        studentRepository.saveAndFlush(student);
        theoryLessonRepository.saveAndFlush(theoryLesson);

        // check that only a theoryLesson is returned that not contains any student
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/{id}", theoryLesson.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.students", hasSize(0)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(theoryLesson.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(entityCreationHelper.getDEFAULT_SUBJECT()))
            .andExpect(jsonPath("$.teacherId").value(theoryLesson.getTeacher().getId()));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllTheoryLessonsByTeacherIdByTeacher() throws Exception {
        // Initialize the database
        Teacher teacher = theoryLesson.getTeacher();
        teacherRepository.saveAndFlush(teacher);
        theoryLessonRepository.saveAndFlush(theoryLesson);
        theoryLesson.setTeacher(null);
        int dbSize = theoryLessonRepository.findAll().size();

        // check that nothing is returned if the given teacher id is no teacher in the db
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/teacher/{id}", Long.MAX_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$" ,hasSize(0)));
        teacherRepository.findAll().forEach(t -> assertThat(t.getId()).isNotEqualTo(Long.MAX_VALUE));

        // check that no lesson is returned if the teacher has no theoryLesson so far
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/teacher/{id}", teacher.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$" ,hasSize(0)));
        theoryLessonRepository.findAll().forEach(l -> assertThat(l.getTeacher()).isNotEqualTo(teacher));

        // check correct request
        theoryLesson.setTeacher(teacher);
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/teacher/{id}", teacher.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$" ,hasSize(1)))
            .andExpect(jsonPath("$.[0].teacherId").value(teacher.getId().toString()));
        assertThat(theoryLessonRepository.findAllByTeacherId(teacher.getId()).get(0)).isEqualTo(theoryLesson);

    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllTheoryLessonsByTeacherIdByStudent() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        // make sure that if a student makes the call, now students are in the result
        // check correct request
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/teacher/{id}", theoryLesson.getTeacher().getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$" , hasSize(1)))
            .andExpect(jsonPath("$.[0].teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.[0].students", hasSize(0)));
        assertThat(theoryLessonRepository.findAllByTeacherId(theoryLesson.getTeacher().getId()).get(0)).isEqualTo(theoryLesson);

    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllTheoryLessonsByTeacherLoginByTeacher() throws Exception {
        // Initialize the database
        Teacher teacher = theoryLesson.getTeacher();
        teacherRepository.saveAndFlush(teacher);
        theoryLessonRepository.saveAndFlush(theoryLesson);
        theoryLesson.setTeacher(null);
        int dbSize = theoryLessonRepository.findAll().size();

        // check that result is empty if the teacher has no lessons
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/teacher"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$" , hasSize(0)));
        theoryLessonRepository.findAll().forEach(t -> assertThat(t.getTeacher()).isNotEqualTo(teacher));

        theoryLesson.setTeacher(teacher);
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/teacher"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$" , hasSize(1)))
            .andExpect(jsonPath("$.[0].teacherId").value(theoryLesson.getTeacher().getId().toString()));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getTeacher()).isEqualTo(teacher);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllTheoryLessonsByTeacherLoginForbidden() throws Exception {
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/teacher"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(username = "usertest", roles = "STUDENT")
    public void getAllTheoryLessonsByStudentLoginByStudent() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);
        studentRepository.saveAndFlush(student);

        int dbSize = theoryLessonRepository.findAll().size();

        // check empty result if student has no theoryLesson
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/student"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$" , hasSize(0)));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getStudents()).doesNotContain(student);

        theoryLesson.addStudent(student);
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/student"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$" , hasSize(1)))
            .andExpect(jsonPath("$.[0].students", hasSize(0)));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getStudents()).contains(student);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllTheoryLessonsByStudentLoginForbidden() throws Exception {
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/student"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllFutureTheoryLessonsByTeacherIdByTeacher() throws Exception {
        // Initialize the database
        Teacher teacher = theoryLesson.getTeacher();
        teacherRepository.saveAndFlush(teacher);
        theoryLessonRepository.saveAndFlush(theoryLesson);
        theoryLesson.setTeacher(null);

        int dbSize = theoryLessonRepository.findAll().size();

        // check that no lesson is found, because the teacher has no
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/future/{id}", teacher.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getTeacher()).isNotEqualTo(teacher);

        // check that no lesson is found because the theoryLesson is not in the future
        theoryLesson.setTeacher(teacher);
        theoryLesson.setBegin(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN().minusDays(10L));
        theoryLesson.setEnd(entityCreationHelper.getDEFAULT_THEORY_LESSON_END().minusDays(9L));
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/future/{id}", teacher.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getBegin()).isBeforeOrEqualTo(
            entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getEnd()).isBeforeOrEqualTo(
            entityCreationHelper.getDEFAULT_THEORY_LESSON_END());

        // check correct request
        theoryLesson.setBegin(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN().plusDays(10L));
        theoryLesson.setEnd(entityCreationHelper.getDEFAULT_THEORY_LESSON_END().plusDays(11L));
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/future/{id}", teacher.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].teacherId").value(teacher.getId().toString()));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getTeacher()).isEqualTo(teacher);
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getBegin()).isAfter(
            entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getEnd()).isAfter(
            entityCreationHelper.getDEFAULT_THEORY_LESSON_END());
    }
    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllFutureTheoryLessonsByTeacherIdByAdmin() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        int dbSize = theoryLessonRepository.findAll().size();

        theoryLesson.setBegin(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN().plusDays(10L));
        theoryLesson.setEnd(entityCreationHelper.getDEFAULT_THEORY_LESSON_END().plusDays(11L));
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/future/{id}", theoryLesson.getTeacher().getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].teacherId").value(theoryLesson.getTeacher().getId().toString()));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getTeacher()).isEqualTo(theoryLesson.getTeacher());
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getBegin()).isAfter(
            entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getEnd()).isAfter(
            entityCreationHelper.getDEFAULT_THEORY_LESSON_END());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllFutureTheoryLessonsByTeacherIdByStudent() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        int dbSize = theoryLessonRepository.findAll().size();

        theoryLesson.setBegin(entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN().plusDays(10L));
        theoryLesson.setEnd(entityCreationHelper.getDEFAULT_THEORY_LESSON_END().plusDays(11L));
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/future/{id}", theoryLesson.getTeacher().getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].teacherId").value(theoryLesson.getTeacher().getId().toString()))
            .andExpect(jsonPath("$.[0].students", hasSize(0)));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getTeacher()).isEqualTo(theoryLesson.getTeacher());
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getBegin()).isAfter(
            entityCreationHelper.getDEFAULT_THEORY_LESSON_BEGIN());
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getEnd()).isAfter(
            entityCreationHelper.getDEFAULT_THEORY_LESSON_END());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void getAllTheoryLessonsByStudentIdByTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);
        int dbSize = theoryLessonRepository.findAll().size();

        // check not found if student not exists
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/student/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());

        // check that result is empty if students has no theoryLesson
        studentRepository.saveAndFlush(student);
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/student/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getStudents()).doesNotContain(student);

        // check correct
        theoryLesson.addStudent(student);
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/student/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].students", hasSize(1)))
            .andExpect(jsonPath("$.[0].students.[0].studentId").value(student.getId()));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getStudents()).containsOnly(student);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllTheoryLessonsByStudentIdByAdmin() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);
        studentRepository.saveAndFlush(student);
        int dbSize = theoryLessonRepository.findAll().size();

        // check correct
        theoryLesson.addStudent(student);
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/student/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].students", hasSize(1)))
            .andExpect(jsonPath("$.[0].students.[0].studentId").value(student.getId()));
        assertThat(theoryLessonRepository.findAll().get(dbSize-1).getStudents()).containsOnly(student);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllTheoryLessonsByStudentIdByForbidden() throws Exception {
        restTheoryLessonMockMvc.perform(get("/api/theory-lessons/student/{id}", 1L))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void deleteTheoryLesson() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(theoryLesson.getTeacher());
        theoryLessonRepository.saveAndFlush(theoryLesson);

        int databaseSizeBeforeDelete = theoryLessonRepository.findAll().size();

        // Delete the theoryLesson
        restTheoryLessonMockMvc.perform(delete("/api/theory-lessons/{id}", theoryLesson.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TheoryLesson> theoryLessonList = theoryLessonRepository.findAll();
        assertThat(theoryLessonList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TheoryLesson.class);
        TheoryLesson theoryLesson1 = new TheoryLesson();
        theoryLesson1.setId(1L);
        TheoryLesson theoryLesson2 = new TheoryLesson();
        theoryLesson2.setId(theoryLesson1.getId());
        assertThat(theoryLesson1).isEqualTo(theoryLesson2);
        theoryLesson2.setId(2L);
        assertThat(theoryLesson1).isNotEqualTo(theoryLesson2);
        theoryLesson1.setId(null);
        assertThat(theoryLesson1).isNotEqualTo(theoryLesson2);
    }
}
