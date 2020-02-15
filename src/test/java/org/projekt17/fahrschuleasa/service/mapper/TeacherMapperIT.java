package org.projekt17.fahrschuleasa.service.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.service.dto.TeacherDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class TeacherMapperIT {

    private Set<Student> students = new HashSet<>();
    private Set<TimeSlot> timeSlots = new HashSet<>();
    private Set<TheoryLesson> theoryLessons = new HashSet<>();
    private Boolean active = true;
    private LocalDate birthdate = LocalDate.of(2000,12,4);
    private Location location = new Location();
    private String phoneNumber = "00000";
    private User user;
    private TimeSlot timeSlot;

    private Long id = 0L;
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private String town = "sb";
    private String street = "brauerstrasse";
    private String postal = "66692";
    private Integer houseNumber = 51;
    private String country = "germany";
    private String additional = "test";

    private Set<DrivingCategory> preferredCategories = new HashSet<>();
    private Set<DrivingCategory> optionalCategories = new HashSet<>();
    private Set<LocalDate> blockedDates = new HashSet<>();

    private Teacher teacher;
    private TeacherDTO teacherDTO;

    private Teacher teacherTest;
    private TeacherDTO teacherDTOTest;

    @BeforeEach
    public void init(){
        user = new User();
        user.setLogin("jdfbhjkdbk");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail("johndoe@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("image_url");
        user.setLangKey("en");
        user.setId(0L);

        location.setId(0L);
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        location.setTown(town);
        location.setStreet(street);
        location.setPostal(postal);
        location.setHouseNumber(houseNumber);
        location.setCountry(country);
        location.setAdditional(additional);

        timeSlot = new TimeSlot();
        timeSlot.setId(id);
        timeSlot.setBegin(0);
        timeSlot.setEnd(1);
        timeSlot.setDay(DayOfWeek.MO);
        Teacher teacherz = new Teacher();
        teacherz.setId(0L);
        timeSlot.setTeacher(teacherz);
        Student student = new Student();
        student.setId(0L);
        timeSlot.setLastScheduledStudent(student);
        preferredCategories.add(DrivingCategory.BE);
        timeSlot.setPreferredCategories(preferredCategories);
        optionalCategories.add(DrivingCategory.B);
        timeSlot.setOptionalCategories(optionalCategories);
        blockedDates.add(LocalDate.of(1998,2,22));
        timeSlot.setBlockedDates(blockedDates);

        teacher = new Teacher();
        teacher.setUser(user);
        teacher.setId(0L);
        teacher.setActive(active);
        teacher.setBirthdate(birthdate);
        teacher.setAddress(location);
        teacher.setPhoneNumber(phoneNumber);
        teacher.setChangedTimeSlots(active);
        teacher.setSchoolOwner(active);
        teacher.setStudents(students);
        timeSlots.add(timeSlot);
        teacher.setTimeSlots(timeSlots);
        teacher.setTheoryLessons(theoryLessons);

        teacherDTO = new TeacherDTO(teacher);
    }

    @Test
    public void simpleTestTeacherToTeacherDto(){

        teacherDTOTest = TeacherMapper.INSTANCE.toDto(teacher);

        assertThat(teacherDTOTest).isNotNull();
        assertThat(teacherDTOTest.getId()).isEqualTo(0L);
        assertThat(teacherDTOTest.getUser().getLogin()).isEqualTo(user.getLogin());
        assertThat(teacherDTOTest.getActive()).isEqualTo(active);
        assertThat(teacherDTOTest.getBirthdate()).isEqualTo(birthdate);
        assertThat(teacherDTOTest.getAddress()).isEqualTo(location);
        assertThat(teacherDTOTest.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(teacherDTOTest.getChangedTimeSlots()).isEqualTo(active);
        assertThat(teacherDTOTest.getSchoolOwner()).isEqualTo(active);
    }

    @Test
    public void simpleTestTeacherDtoToTeacher(){

        teacherTest = TeacherMapper.INSTANCE.toEntity(teacherDTO);

        assertThat(teacherTest).isNotNull();
        assertThat(teacherTest.getId()).isEqualTo(0L);
        assertThat(teacherTest.getUser()).isEqualTo(user);
        assertThat(teacherTest.isActive()).isEqualTo(active);
        assertThat(teacherTest.getBirthdate()).isEqualTo(birthdate);
        assertThat(teacherTest.getAddress()).isEqualTo(location);
        assertThat(teacherTest.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(teacherTest.isChangedTimeSlots()).isEqualTo(active);
        assertThat(teacherTest.isSchoolOwner()).isEqualTo(active);
    }
}
