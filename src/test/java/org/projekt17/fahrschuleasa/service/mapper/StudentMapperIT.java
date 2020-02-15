package org.projekt17.fahrschuleasa.service.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.service.dto.StudentDTO;
import org.projekt17.fahrschuleasa.service.dto.UserDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class StudentMapperIT {


    private static final String DEFAULT_LOGIN = "johndoe";
    private static final Long DEFAULT_ID = 1L;

    private static final Long id = 0L;
    private static final Boolean active = true;
    private static final LocalDate birthdate = LocalDate.of(1998,2,22);
    private static final Location location = new Location();
    private static final String phoneNumber = "0000";

    private DrivingCategory category = DrivingCategory.B96;
    private Boolean readyForTheory = true;
    private Integer wantedLessons = 2;
    private Boolean changedPreferences = false;
    private TeachingDiagram teachingDiagram;
    private Set<Preference> preferences = new HashSet<>();
    private Set<TheoryLesson> theoryLessons = new HashSet<>();
    private Teacher teacher;
    private Set<DrivingLesson> drivingLessons = new HashSet<>();
    private Set<DrivingLesson> missedLessons = new HashSet<>();
    private Set<TimeSlot> lastScheduledTimeSlots = new HashSet<>();

    private Integer basic = 0;
    private Integer advanced = 0;
    private Integer performance = 0;
    private Integer independence = 0;
    private Integer overland = 0;
    private Integer autobahn = 0;
    private Integer night = 0;

    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private String town = "sb";
    private String street = "brauerstrasse";
    private String postal = "66692";
    private Integer houseNumber = 51;
    private String country = "germany";
    private String additional = "test";

    private User user;
    private UserDTO userDTO;

    private Student student;
    private StudentDTO studentDTO;

    private Student studentTest;
    private StudentDTO studentDTOTest;

    @BeforeEach
    public void init(){
        user = new User();

        user.setLogin(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail("johndoe@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("image_url");
        user.setLangKey("en");

        teachingDiagram = new TeachingDiagram();
        teachingDiagram.setId(id);
        teachingDiagram.setBasic(basic);
        teachingDiagram.setAdvanced(advanced);
        teachingDiagram.setPerformance(performance);
        teachingDiagram.setIndependence(independence);
        teachingDiagram.setOverland(overland);
        teachingDiagram.setAutobahn(autobahn);
        teachingDiagram.setNight(night);

        teacher = new Teacher();
        teacher.setId(0L);

        location.setId(id);
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        location.setTown(town);
        location.setStreet(street);
        location.setPostal(postal);
        location.setHouseNumber(houseNumber);
        location.setCountry(country);
        location.setAdditional(additional);

        student = new Student();
        student.setId(id);
        student.setActive(active);
        student.setBirthdate(birthdate);
        student.setAddress(location);
        student.setPhoneNumber(phoneNumber);
        student.setUser(user);
        student.setCategory(category);
        student.setReadyForTheory(readyForTheory);
        student.setWantedLessons(wantedLessons);
        student.setChangedPreferences(changedPreferences);
        student.setTeachingDiagram(teachingDiagram);
        student.setPreferences(preferences);
        student.setTheoryLessons(theoryLessons);
        student.setTeacher(teacher);
        student.setDrivingLessons(drivingLessons);
        student.setMissedLessons(missedLessons);
        student.setLastScheduledTimeSlots(lastScheduledTimeSlots);
        studentDTO = new StudentDTO(student);
    }

    @Test
    public void simpleTestStudentToStudentDTO(){

        studentDTOTest = StudentMapper.INSTANCE.toDto(student);

        assertThat(studentDTOTest).isNotNull();
        assertThat(studentDTOTest.getId()).isEqualTo(id);
        assertThat(studentDTOTest.getActive()).isEqualTo(active);
        assertThat(studentDTOTest.getBirthdate()).isEqualTo(birthdate);

        assertThat(studentDTOTest.getAddress().getCountry()).isEqualTo(location.getCountry());
        assertThat(studentDTOTest.getAddress().getAdditional()).isEqualTo(location.getAdditional());
        assertThat(studentDTOTest.getAddress().getHouseNumber()).isEqualTo(location.getHouseNumber());
        assertThat(studentDTOTest.getAddress().getId()).isEqualTo(location.getId());
        assertThat(studentDTOTest.getAddress().getLatitude()).isEqualTo(location.getLatitude());
        assertThat(studentDTOTest.getAddress().getLongitude()).isEqualTo(location.getLongitude());
        assertThat(studentDTOTest.getAddress().getPostal()).isEqualTo(location.getPostal());
        assertThat(studentDTOTest.getAddress().getStreet()).isEqualTo(location.getStreet());
        assertThat(studentDTOTest.getAddress().getTown()).isEqualTo(location.getTown());

        assertThat(studentDTOTest.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(studentDTOTest.getUser().getLogin()).isEqualTo(user.getLogin());
        assertThat(studentDTOTest.getCategory()).isEqualTo(category);
        assertThat(studentDTOTest.getReadyForTheory()).isEqualTo(readyForTheory);
        assertThat(studentDTOTest.getWantedLessons()).isEqualTo(wantedLessons);
        assertThat(studentDTOTest.getChangedPreferences()).isEqualTo(changedPreferences);
        assertThat(studentDTOTest.getTeacherId()).isEqualTo(teacher.getId());
    }

    @Test
    public void simpleTestStudentDTOtoStudent(){

        studentTest = StudentMapper.INSTANCE.toEntity(studentDTO);

        assertThat(studentTest).isNotNull();
        assertThat(studentTest.getId()).isEqualTo(id);
        assertThat(studentTest.isActive()).isEqualTo(active);
        assertThat(studentTest.getBirthdate()).isEqualTo(birthdate);

        assertThat(studentTest.getAddress().getCountry()).isEqualTo(location.getCountry());
        assertThat(studentTest.getAddress().getAdditional()).isEqualTo(location.getAdditional());
        assertThat(studentTest.getAddress().getHouseNumber()).isEqualTo(location.getHouseNumber());
        assertThat(studentTest.getAddress().getLatitude()).isEqualTo(location.getLatitude());
        assertThat(studentTest.getAddress().getLongitude()).isEqualTo(location.getLongitude());
        assertThat(studentTest.getAddress().getPostal()).isEqualTo(location.getPostal());
        assertThat(studentTest.getAddress().getStreet()).isEqualTo(location.getStreet());
        assertThat(studentTest.getAddress().getTown()).isEqualTo(location.getTown());

        assertThat(studentTest.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(studentTest.getUser().getLogin()).isEqualTo(user.getLogin());
        assertThat(studentTest.getCategory()).isEqualTo(category);
        assertThat(studentTest.isReadyForTheory()).isEqualTo(readyForTheory);
        assertThat(studentTest.getWantedLessons()).isEqualTo(wantedLessons);
        assertThat(studentTest.isChangedPreferences()).isEqualTo(changedPreferences);
        assertThat(studentTest.getPreferences().size()).isEqualTo(0);
        assertThat(studentTest.getTheoryLessons().size()).isEqualTo(0);
        assertThat(studentTest.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(studentTest.getDrivingLessons().size()).isEqualTo(0);
        assertThat(studentTest.getMissedLessons().size()).isEqualTo(0);
        assertThat(studentTest.getLastScheduledTimeSlots().size()).isEqualTo(0);
    }
}
