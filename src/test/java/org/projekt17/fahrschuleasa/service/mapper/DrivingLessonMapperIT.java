package org.projekt17.fahrschuleasa.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;
import org.projekt17.fahrschuleasa.service.dto.DrivingLessonDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class DrivingLessonMapperIT {

    private DrivingLessonType lessonType = DrivingLessonType.AUTOBAHN;
    private Student driver = new Student();
    private Student missing = new Student();
    //private Preference preference = new Preference();
    private Set<Student> optionalStudents = new HashSet<>();
    private Location pickup;
    private Location destination;
    private TimeSlot timeSlot;
    private Integer begin = 0;
    private Integer end = 1;
    private DayOfWeek day = DayOfWeek.MO;
    private Boolean night = true;
    private Long teacherId = 0L;
    private Long lastScheduledStudentId = 0L;
    private Set<DrivingCategory> preferredCategories = new HashSet<>();
    private Set<DrivingCategory> optionalCategories = new HashSet<>();
    private List<LocalDate> blockedDates = new LinkedList<>();


    private DrivingLesson drivingLesson;
    private DrivingLessonDTO drivingLessonDTO;

    private DrivingLesson drivingLessonTest;
    private DrivingLessonDTO drivingLessonDTOTest;

    @BeforeEach
    public void init(){
        drivingLesson = new DrivingLesson();
        pickup = new Location();
        destination = new Location();
        pickup.setId(1L);
        pickup.setAdditional("test");
        pickup.setCountry("germany");
        pickup.setPostal("66693");
        pickup.setHouseNumber(51);
        pickup.setStreet("Brauerstrasse");
        pickup.setLatitude(0.0);
        pickup.setLongitude(0.0);
        pickup.setTown("sb");
        destination = pickup;

        drivingLesson.setId(0L);
        drivingLesson.setBegin(LocalDateTime.of(2000,12,4,13,0));
        drivingLesson.setEnd(LocalDateTime.of(2000,12,4,15,0));
        drivingLesson.setLessonType(lessonType);

        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        driver.setCategory(DrivingCategory.A);
        driver.setUser(user);
        drivingLesson.setDriver(driver);
        drivingLesson.addMissingStudent(missing);
        drivingLesson.setOptionalStudents(optionalStudents);
        drivingLesson.setPickup(pickup);
        drivingLesson.setDestination(destination);

        drivingLessonDTO = new DrivingLessonDTO(drivingLesson);
    }

    @Test
    public void simpleTestDrivingLessonToDrivingLessonDto(){

        drivingLessonDTOTest = DrivingLessonMapper.INSTANCE.toDto(drivingLesson);

        assertThat(drivingLessonDTOTest).isNotNull();
        assertThat(drivingLessonDTOTest.getId()).isEqualTo(0L);
        assertThat(drivingLessonDTOTest.getBegin())
            .isEqualTo(LocalDateTime.of(2000,12,4,13,0));
        assertThat(drivingLessonDTOTest.getEnd())
            .isEqualTo(LocalDateTime.of(2000,12,4,15,0));
        assertThat(drivingLessonDTOTest.getPickup().getId()).isEqualTo(pickup.getId());
        assertThat(drivingLessonDTOTest.getDestination().getId()).isEqualTo(destination.getId());
        assertThat(drivingLessonDTOTest.getDriver().getFirstname()).isEqualTo("firstName");
        assertThat(drivingLessonDTOTest.getDriver().getLastname()).isEqualTo("lastName");
        assertThat(drivingLessonDTOTest.getDrivingCategory()).isEqualTo(DrivingCategory.A);
    }

    @Test
    public void simpleTestDrivingLessonDtoToDrivingLesson(){

        drivingLessonTest = DrivingLessonMapper.INSTANCE.toEntity(drivingLessonDTO);

        assertThat(drivingLessonTest).isNotNull();
        assertThat(drivingLessonTest.getId()).isEqualTo(0L);
        assertThat(drivingLessonTest.getBegin())
            .isEqualTo(LocalDateTime.of(2000,12,4,13,0));
        assertThat(drivingLessonTest.getEnd())
            .isEqualTo(LocalDateTime.of(2000,12,4,15,0));
        assertThat(drivingLessonTest.getPickup().getId()).isEqualTo(pickup.getId());
        assertThat(drivingLessonTest.getDestination().getId()).isEqualTo(destination.getId());
    }
}










