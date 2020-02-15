package org.projekt17.fahrschuleasa.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.service.dto.TimeSlotDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class TimeSlotMapperIT {

    private Long id = 0L;
    private Integer begin = 0;
    private Integer end = 1;
    private DayOfWeek day = DayOfWeek.MO;
    private Long teacherId = 0L;
    private Set<DrivingCategory> preferredCategories = new HashSet<>();
    private Set<DrivingCategory> optionalCategories = new HashSet<>();
    private Set<LocalDate> blockedDates = new HashSet<>();

    private TimeSlot timeSlot;
    private TimeSlotDTO timeSlotDTO;

    private TimeSlot timeSlotTest;
    private TimeSlotDTO timeSlotDTOTest;

    private LocalDate date;

    @BeforeEach
    public void init(){
        timeSlot = new TimeSlot();
        timeSlot.setId(id);
        timeSlot.setBegin(begin);
        timeSlot.setEnd(end);
        timeSlot.setDay(day);
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        timeSlot.setTeacher(teacher);

        preferredCategories.add(DrivingCategory.BE);
        timeSlot.setPreferredCategories(preferredCategories);

        optionalCategories.add(DrivingCategory.B);
        timeSlot.setOptionalCategories(optionalCategories);

        blockedDates.add(LocalDate.of(1998,2,22));
        date = LocalDate.now().plusDays(4);
        blockedDates.add(date);
        timeSlot.setBlockedDates(blockedDates);

        timeSlotDTO = new TimeSlotDTO(timeSlot);

    }

    @Test
    public void simpleTestTimeSlotToTimeSlotDto(){

        timeSlotDTOTest = TimeSlotMapper.INSTANCE.toDto(timeSlot);

        assertThat(timeSlotDTOTest).isNotNull();
        assertThat(timeSlotDTOTest.getId()).isEqualTo(id);
        assertThat(timeSlotDTOTest.getBegin()).isEqualTo(begin);
        assertThat(timeSlotDTOTest.getEnd()).isEqualTo(end);
        assertThat(timeSlotDTOTest.getDay()).isEqualTo(day);
        assertThat(timeSlotDTOTest.getPreferredCategories().size()).isEqualTo(1);
        assertThat(timeSlotDTOTest.getOptionalCategories().size()).isEqualTo(1);
        assertThat(timeSlotDTOTest.getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlotDTOTest.getBlockedDates().get(0)).isEqualTo(date);
    }

    @Test
    public void simpleTestTimeSlotDtoToTimeSlot(){

        timeSlotTest = TimeSlotMapper.INSTANCE.toEntity(timeSlotDTO);

        assertThat(timeSlotTest).isNotNull();
        assertThat(timeSlotTest.getId()).isEqualTo(id);
        assertThat(timeSlotTest.getBegin()).isEqualTo(begin);
        assertThat(timeSlotTest.getEnd()).isEqualTo(end);
        assertThat(timeSlotTest.getDay()).isEqualTo(day);
        assertThat(timeSlotTest.getPreferredCategories().size()).isEqualTo(1);
        assertThat(timeSlotTest.getOptionalCategories().size()).isEqualTo(1);
        assertThat(timeSlotTest.getBlockedDates().size()).isEqualTo(1);
        assertThat(timeSlotTest.getBlockedDates().contains(date)).isTrue();
    }
}
