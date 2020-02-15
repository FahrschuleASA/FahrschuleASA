package org.projekt17.fahrschuleasa.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.service.dto.PreferenceDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class PreferenceMapperIT {

    private Long id = 0L;
    private Location pickup;
    private Location destination;
    private TimeSlot timeSlot;
    private Student student;

    private Integer begin = 0;
    private Integer end = 1;
    private DayOfWeek day = DayOfWeek.MO;
    private Boolean night = true;
    private Long teacherId = 0L;
    private Long lastScheduledStudentId = 0L;
    private Set<DrivingCategory> preferredCategories = new HashSet<>();
    private Set<DrivingCategory> optionalCategories = new HashSet<>();
    private Set<LocalDate> blockedDates = new HashSet<>();

    private Preference preference;
    private PreferenceDTO preferenceDTO;

    private Preference preferenceTest;
    private PreferenceDTO preferenceDTOTest;

    @BeforeEach
    public void init(){
        preference = new Preference();

        preference.setId(id);

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
        destination.setId(1L);
        destination.setAdditional("test");
        destination.setCountry("germany");
        destination.setPostal("66693");
        destination.setHouseNumber(51);
        destination.setStreet("Brauerstrasse");
        destination.setLatitude(0.0);
        destination.setLongitude(0.0);
        destination.setTown("sb");
        preference.setPickup(pickup);
        preference.setDestination(destination);

        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setBegin(begin);
        timeSlot.setEnd(end);
        timeSlot.setDay(day);
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        timeSlot.setTeacher(teacher);
        Student student = new Student();
        student.setId(lastScheduledStudentId);
        timeSlot.setLastScheduledStudent(student);
        preferredCategories.add(DrivingCategory.BE);
        timeSlot.setPreferredCategories(preferredCategories);
        optionalCategories.add(DrivingCategory.B);
        timeSlot.setOptionalCategories(optionalCategories);
        blockedDates.add(LocalDate.of(1998,2,22));
        timeSlot.setBlockedDates(blockedDates);
        preference.setTimeSlot(timeSlot);

        student = new Student();
        student.setId(1L);
        preference.setStudent(student);

        preferenceDTO = new PreferenceDTO(preference);
    }

    @Test
    public void simpleTestPreferenceToPreferenceDto(){

        preferenceDTOTest = PreferenceMapper.INSTANCE.toDto(preference);

        assertThat(preferenceDTOTest).isNotNull();
        assertThat(preferenceDTOTest.getId()).isEqualTo(0L);
        assertThat(preferenceDTOTest.getPickup().getId()).isEqualTo(1L);
        assertThat(preferenceDTOTest.getDestination().getId()).isEqualTo(1L);
        assertThat(preferenceDTOTest.getTimeSlotId()).isEqualTo(1L);
        assertThat(preferenceDTOTest.getStudentId()).isEqualTo(1L);
    }

    @Test
    public void simpleTestPreferenceDtoToPreference(){

        preferenceTest = PreferenceMapper.INSTANCE.toEntity(preferenceDTO);

        assertThat(preferenceTest).isNotNull();
        assertThat(preferenceTest.getId()).isEqualTo(0L);
        assertThat(preferenceTest.getPickup().getId()).isEqualTo(1L);
        assertThat(preferenceTest.getDestination().getId()).isEqualTo(1L);
        assertThat(preferenceTest.getTimeSlot().getId()).isEqualTo(1L);
        assertThat(preferenceTest.getStudent().getId()).isEqualTo(1L);
    }
}
