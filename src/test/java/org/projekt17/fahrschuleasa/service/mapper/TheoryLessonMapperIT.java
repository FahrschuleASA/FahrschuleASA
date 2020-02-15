package org.projekt17.fahrschuleasa.service.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.service.dto.TheoryLessonDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class TheoryLessonMapperIT {


    private String subject;
    private Teacher teacher;
    private Set<Student> students = new HashSet<>();

    private Location location;
    private User user;
    private TeachingDiagram teachingDiagram;

    private TheoryLesson theoryLesson;
    private TheoryLessonDTO theoryLessonDTO;

    private TheoryLesson theoryLessonTest;
    private TheoryLessonDTO theoryLessonDTOTest;


    @BeforeEach
    public void init(){

        theoryLesson = new TheoryLesson();

        subject = "test";
        teacher = new Teacher();
        teacher.setId(0L);

        location = new Location();
        location.setId(0L);
        location.setLongitude(0.0);
        location.setLatitude(0.0);
        location.setTown("sb");
        location.setStreet("brauerstrasse");
        location.setPostal("66663");
        location.setHouseNumber(4);
        location.setCountry("england");
        location.setAdditional("test");

        user = new User();
        user.setLogin("blaa");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail("johndoe@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("image_url");
        user.setLangKey("en");

        teachingDiagram = new TeachingDiagram();
        teachingDiagram.setId(0L);
        teachingDiagram.setBasic(1);
        teachingDiagram.setAdvanced(1);
        teachingDiagram.setPerformance(1);
        teachingDiagram.setIndependence(1);
        teachingDiagram.setOverland(1);
        teachingDiagram.setAutobahn(1);
        teachingDiagram.setNight(1);

        Student student = new Student();
        student.setId(0L);
        student.setActive(true);
        student.setBirthdate(LocalDate.of(2000,12,4));
        student.setAddress(location);
        student.setPhoneNumber("000000");
        student.setUser(user);
        student.setCategory(DrivingCategory.B);
        student.setReadyForTheory(true);
        student.setWantedLessons(1);
        student.setChangedPreferences(false);
        student.setTeachingDiagram(teachingDiagram);
        student.setPreferences(new HashSet<>());
        student.setTheoryLessons(new HashSet<>());
        student.setTeacher(teacher);
        student.setDrivingLessons(new HashSet<>());
        student.setLastScheduledTimeSlots(new HashSet<>());

        students.add(student);
        theoryLesson.setStudents(students);
        theoryLesson.setSubject(subject);
        theoryLesson.setTeacher(teacher);
        theoryLesson.setId(0L);
        theoryLesson.setBegin(LocalDateTime.of(2000,12,4,13,0));
        theoryLesson.setEnd(LocalDateTime.of(2000,12,4,15,0));

        theoryLessonDTO = new TheoryLessonDTO(theoryLesson);
    }

    @Test
    public void simpleTestTheoryLessonToTheoryLessonDto(){

        theoryLessonDTOTest = TheoryLessonMapper.INSTANCE.toDto(theoryLesson);

        assertThat(theoryLessonDTOTest).isNotNull();
        assertThat(theoryLessonDTOTest.getBegin())
            .isEqualTo(LocalDateTime.of(2000,12,4,13,0));
        assertThat(theoryLessonDTOTest.getEnd())
            .isEqualTo(LocalDateTime.of(2000,12,4,15,0));
        assertThat(theoryLessonDTOTest.getId()).isEqualTo(0L);
        assertThat(theoryLessonDTOTest.getTeacherId()).isEqualTo(0L);
        assertThat(theoryLessonDTOTest.getSubject()).isEqualTo("test");
        assertThat(theoryLessonDTOTest.getStudents().size()).isEqualTo(1);
    }


    @Test
    public void simpleTestTheoryLessonDtoToTheoryLesson(){

        theoryLessonTest = TheoryLessonMapper.INSTANCE.toEntity(theoryLessonDTO);

        assertThat(theoryLessonTest).isNotNull();
        assertThat(theoryLessonTest.getBegin())
            .isEqualTo(LocalDateTime.of(2000,12,4,13,0));
        assertThat(theoryLessonTest.getEnd())
            .isEqualTo(LocalDateTime.of(2000,12,4,15,0));
        assertThat(theoryLessonTest.getId()).isEqualTo(0L);
        assertThat(theoryLessonTest.getTeacher().getId()).isEqualTo(0L);
        assertThat(theoryLessonTest.getSubject()).isEqualTo("test");
    }

}
