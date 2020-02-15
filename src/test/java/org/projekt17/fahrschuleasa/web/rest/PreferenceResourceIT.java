package org.projekt17.fahrschuleasa.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.domain.Preference;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.repository.*;
import org.projekt17.fahrschuleasa.service.dto.PreferenceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PreferenceResource} REST controller.
 */
@SpringBootTest(classes = FahrschuleAsaApp.class)
public class PreferenceResourceIT {

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    //@Autowired
    //private MappingJackson2HttpMessageConverter jacksonMessageConverter;
//
    //@Autowired
    //private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
//
    //@Autowired
    //private ExceptionTranslator exceptionTranslator;
//
    @Autowired
    private EntityManager em;

    private MockMvc restPreferenceMockMvc;

    private Preference preference;

    private EntityCreationHelper entityCreationHelper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        entityCreationHelper = new EntityCreationHelper();
        MockitoAnnotations.initMocks(this);
        this.restPreferenceMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        preference = entityCreationHelper.createEntityPreference(false,true);
    }

    @Transactional
    public void safeAndFlushUserStudentTimeSlotTimeSlotTeacher(){
        userRepository.saveAndFlush(preference.getStudent().getUser());
        studentRepository.saveAndFlush(preference.getStudent());
        userRepository.saveAndFlush(preference.getTimeSlot().getTeacher().getUser());
        teacherRepository.saveAndFlush(preference.getTimeSlot().getTeacher());
        timeSlotRepository.saveAndFlush(preference.getTimeSlot());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void createPreference() throws Exception {
        int databaseSizeBeforeCreate = preferenceRepository.findAll().size();
        // to create a preference there must already exist a student with user(login is needed) and a timeSlot
        safeAndFlushUserStudentTimeSlotTimeSlotTeacher();

        // check for BadRequest if preferenceDTO has id
        PreferenceDTO preferenceDTO = new PreferenceDTO(preference);
        preferenceDTO.setId(1L);
        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preferenceDTO)))
            .andExpect(status().isBadRequest());
        assertThat(preferenceRepository.findAll()).hasSize(databaseSizeBeforeCreate);

        // check for for Dar Request if timeSLotDTO has no id
        preferenceDTO = new PreferenceDTO(preference);
        preferenceDTO.setTimeSlotId(null);
        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preferenceDTO)))
            .andExpect(status().isBadRequest());
        assertThat(preferenceRepository.findAll()).hasSize(databaseSizeBeforeCreate);

        // check that nothing is created if time slot with given id does not exist
        preferenceDTO = new PreferenceDTO(preference);
        preferenceDTO.setTimeSlotId(Long.MAX_VALUE);
        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preferenceDTO)))
            .andExpect(status().isNotFound());
        assertThat(preferenceRepository.findAll()).hasSize(databaseSizeBeforeCreate);

        // Create the Preference
        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PreferenceDTO(preference))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(preferenceRepository.findAll().get(databaseSizeBeforeCreate).getId()))
            .andExpect(jsonPath("$.studentId").value(preference.getStudent().getId()))
            .andExpect(jsonPath("$.timeSlotId").value(preference.getTimeSlot().getId()));

        // Validate the Preference in the database
        assertThat(preferenceRepository.findAll()).hasSize(databaseSizeBeforeCreate + 1);
        assertThat(preferenceRepository.findAll().get(databaseSizeBeforeCreate).getTimeSlot()).isEqualTo(preference.getTimeSlot());
        assertThat(preferenceRepository.findAll().get(databaseSizeBeforeCreate).getStudent().getId()).isEqualTo(preference.getStudent().getId());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void createPreferenceForbidden() throws Exception {
        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PreferenceDTO(preference))))
            .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllPreferencesForStudentByStudent() throws Exception {

        // init db
        safeAndFlushUserStudentTimeSlotTimeSlotTeacher();

        // Get all the preferenceList and make sure that for the corresponding student no preference is returned
        restPreferenceMockMvc.perform(get("/api/preferences?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(0)));
        preferenceRepository.findAll().forEach(p -> assertThat(p.getStudent()).isNotEqualTo(preference.getStudent()));

        // Initialize the database
        locationRepository.saveAndFlush(preference.getPickup());
        locationRepository.saveAndFlush(preference.getDestination());
        preferenceRepository.saveAndFlush(preference);
        int dbSize = preferenceRepository.findAll().size();

        // Get all the preferenceList
        restPreferenceMockMvc.perform(get("/api/preferences?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(preference.getId().intValue()))
            .andExpect(jsonPath("$.[0].studentId").value(preference.getStudent().getId()))
            .andExpect(jsonPath("$.[0].timeSlotId").value(preference.getTimeSlot().getId()));

        assertThat(preferenceRepository.findAll().get(dbSize-1).getTimeSlot()).isEqualTo(preference.getTimeSlot());
        assertThat(preferenceRepository.findAll().get(dbSize-1).getStudent().getId()).isEqualTo(preference.getStudent().getId());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllPreferencesForStudentForbidden() throws Exception {
        restPreferenceMockMvc.perform(get("/api/preferences?sort=id,desc"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getAllPreferencesForStudentIdByAdmin() throws Exception {

        // init db
        safeAndFlushUserStudentTimeSlotTimeSlotTeacher();
        locationRepository.saveAndFlush(preference.getPickup());
        locationRepository.saveAndFlush(preference.getDestination());
        Student student = preference.getStudent();
        preference.setStudent(null);
        preferenceRepository.saveAndFlush(preference);
        int dbSize = preferenceRepository.findAll().size();

        // check for empty List if student not exists
        restPreferenceMockMvc.perform(get("/api/preferences/teacher/{id}", Long.MAX_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
        studentRepository.findAll().forEach(s -> assertThat(s.getId()).isNotEqualTo(Long.MAX_VALUE));

        // check for empty list if student has no preferences
        restPreferenceMockMvc.perform(get("/api/preferences/teacher/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
        preferenceRepository.findAll().forEach(p -> assertThat(p.getStudent()).isNotEqualTo(student));

        // check normal
        preference.setStudent(student);
        restPreferenceMockMvc.perform(get("/api/preferences/teacher/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(preference.getId()))
            .andExpect(jsonPath("$.[0].studentId").value(student.getId()));
        assertThat(preferenceRepository.findAll().get(dbSize-1).getStudent()).isEqualTo(student);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getAllPreferencesForStudentIdForbidden() throws Exception {
        restPreferenceMockMvc.perform(get("/api/preferences/teacher/{id}", Long.MAX_VALUE))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void getPreferenceByAdmin() throws Exception {
        // Initialize the database
        safeAndFlushUserStudentTimeSlotTimeSlotTeacher();
        locationRepository.saveAndFlush(preference.getPickup());
        locationRepository.saveAndFlush(preference.getDestination());
        preferenceRepository.saveAndFlush(preference);
        int dbSize = preferenceRepository.findAll().size();

        restPreferenceMockMvc.perform(get("/api/preferences/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
        preferenceRepository.findAll().forEach(p -> assertThat(p.getId()).isNotEqualTo(Long.MAX_VALUE));

        // Get the preference
        restPreferenceMockMvc.perform(get("/api/preferences/{id}", preference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(preference.getId().intValue()))
            .andExpect(jsonPath("$.studentId").value(preference.getStudent().getId()))
            .andExpect(jsonPath("$.timeSlotId").value(preference.getTimeSlot().getId()))
            .andExpect(jsonPath("$.id").value(preference.getId()));

        assertThat(preferenceRepository.findAll().get(dbSize -1).getId()).isEqualTo(preference.getId());
        assertThat(preferenceRepository.findAll().get(dbSize -1).getTimeSlot()).isEqualTo(preference.getTimeSlot());
        assertThat(preferenceRepository.findAll().get(dbSize -1).getStudent().getId()).isEqualTo(preference.getStudent().getId());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void getPreferenceByStudent() throws Exception {
        // Initialize the database
        safeAndFlushUserStudentTimeSlotTimeSlotTeacher();
        Student student = preference.getStudent();
        preference.setStudent(null);
        preferenceRepository.saveAndFlush(preference);
        int dbSize = preferenceRepository.findAll().size();

        // test for forbidden if the corrsponding preference does not belong to this particular student
        restPreferenceMockMvc.perform(get("/api/preferences/{id}", preference.getId()))
            .andExpect(status().isForbidden());
        assertThat(preferenceRepository.findAll().get(dbSize-1).getStudent()).isNotEqualTo(student);

        // Get the preference
        preference.setStudent(student);
        restPreferenceMockMvc.perform(get("/api/preferences/{id}", preference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(preference.getId().intValue()));
        assertThat(preferenceRepository.findAll().get(dbSize-1).getStudent()).isEqualTo(student);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void updatePreference() throws Exception {
        // Initialize the database
        safeAndFlushUserStudentTimeSlotTimeSlotTeacher();
        locationRepository.saveAndFlush(preference.getPickup());
        locationRepository.saveAndFlush(preference.getDestination());
        preferenceRepository.saveAndFlush(preference);
        Student student = preference.getStudent();
        preference.setStudent(null);
        int databaseSizeBeforeUpdate = preferenceRepository.findAll().size();

        // check for BadRequest if preference has no id
        PreferenceDTO preferenceDTO = new PreferenceDTO(preference);
        preferenceDTO.setId(null);
        restPreferenceMockMvc.perform(put("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preferenceDTO)))
            .andExpect(status().isBadRequest());

        // check for Forbidden if its not the students preference
        restPreferenceMockMvc.perform(put("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PreferenceDTO(preference))))
            .andExpect(status().isForbidden());

        // Update the preference
        preference.setStudent(student);
        preferenceRepository.saveAndFlush(preference);
        Preference updatedPreference = preferenceRepository.findById(preference.getId()).get();
        // Disconnect from session so that the updates on updatedPreference are not directly saved in db
        em.detach(updatedPreference);
        Location updaP = preference.getPickup();
        Location updD = preference.getDestination();
        updaP.setStreet(entityCreationHelper.getUPDATED_STREET());
        updD.setStreet(entityCreationHelper.getUPDATED_STREET());
        updatedPreference.setPickup(updaP);
        updatedPreference.setDestination(updD);

        restPreferenceMockMvc.perform(put("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PreferenceDTO(updatedPreference))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(preference.getId()))
            .andExpect(jsonPath("$.pickup.street").value(locationRepository.findAll().get(locationRepository.findAll().size()-1).getStreet()))
            .andExpect(jsonPath("$.destination.street").value(locationRepository.findAll().get(locationRepository.findAll().size()-1).getStreet()));

        // Validate the Preference in the database
        assertThat(preferenceRepository.findAll()).hasSize(databaseSizeBeforeUpdate);
        assertThat(preferenceRepository.findAll().get(databaseSizeBeforeUpdate-1).getId()).isEqualTo(preference.getId());
        assertThat(preferenceRepository.findAll().get(databaseSizeBeforeUpdate-1).getPickup().getStreet())
            .isEqualTo(entityCreationHelper.getUPDATED_STREET());
        assertThat(preferenceRepository.findAll().get(databaseSizeBeforeUpdate-1).getDestination().getStreet())
            .isEqualTo(entityCreationHelper.getUPDATED_STREET());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void updatePreferenceForbidden() throws Exception {
        restPreferenceMockMvc.perform(put("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PreferenceDTO(preference))))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockStudent
    public void deletePreference() throws Exception {
        // Initialize the database
        safeAndFlushUserStudentTimeSlotTimeSlotTeacher();
        locationRepository.saveAndFlush(preference.getPickup());
        locationRepository.saveAndFlush(preference.getDestination());
        preferenceRepository.saveAndFlush(preference);
        Student student = preference.getStudent();
        preference.setStudent(null);
        int databaseSizeBeforeDelete = preferenceRepository.findAll().size();

        restPreferenceMockMvc.perform(delete("/api/preferences/{id}", preference.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
        assertThat(studentRepository.findAll().get(studentRepository.findAll().size()-1).isChangedPreferences()).isEqualTo(false);

        // Delete the preference
        preference.setStudent(student);
        restPreferenceMockMvc.perform(delete("/api/preferences/{id}", preference.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());
        assertThat(studentRepository.findAll().get(studentRepository.findAll().size()-1).isChangedPreferences()).isEqualTo(true);
        assertThat(preferenceRepository.findAll()).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockTeacher
    public void deletePreferenceForbiddenTeacher() throws Exception {
        restPreferenceMockMvc.perform(delete("/api/preferences/{id}", Long.MAX_VALUE)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void deletePreferenceForbiddenAdmin() throws Exception {
        restPreferenceMockMvc.perform(delete("/api/preferences/{id}", Long.MAX_VALUE)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Preference.class);
        Preference preference1 = new Preference();
        preference1.setId(1L);
        Preference preference2 = new Preference();
        preference2.setId(preference1.getId());
        assertThat(preference1).isEqualTo(preference2);
        preference2.setId(2L);
        assertThat(preference1).isNotEqualTo(preference2);
        preference1.setId(null);
        assertThat(preference1).isNotEqualTo(preference2);
    }
}
