package org.projekt17.fahrschuleasa.service.mapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.service.dto.SchoolConfigurationDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class SchoolSchoolConfigurationMapperIT {

    private int initialLessons;

    private int dayOfWeek;

    private int hour;

    private int minutes;

    private int maxInactive;

    private int deadlineMissedLesson;

    private Set<DrivingCategory> availableCategories;


    private SchoolConfigurationDTO schoolConfigurationDTO;
    private SchoolConfigurationDTO schoolConfigurationDTOTest;

    private SchoolConfigurationMapper schoolConfigurationMapper;

    @BeforeEach
    public void init(){
        initialLessons = SchoolConfiguration.getInitialLessons();
        dayOfWeek = SchoolConfiguration.getCronExpression().getWeekday();
        hour = SchoolConfiguration.getCronExpression().getHour();
        minutes = SchoolConfiguration.getCronExpression().getMinute();
        maxInactive = SchoolConfiguration.getMaxInactive();
        deadlineMissedLesson = SchoolConfiguration.getDeadlineMissedLesson();
        availableCategories = SchoolConfiguration.getAvailableCategories();
        schoolConfigurationDTO = new SchoolConfigurationDTO();
        this.schoolConfigurationMapper = new SchoolConfigurationMapper();
    }

    @Test
    public void simpleTestSchoolToSchoolDto(){
    }

    @Test
    public void simpleTestSchoolDtoToSchool(){

        // no object will be created due to a complete static class
        schoolConfigurationMapper.configurationDTOToConfiguration(this.schoolConfigurationDTO);

        assertThat(SchoolConfiguration.getInitialLessons()).isEqualTo(initialLessons);
        assertThat(SchoolConfiguration.getCronExpression().getWeekday()).isEqualTo(dayOfWeek);
        assertThat(SchoolConfiguration.getCronExpression().getHour()).isEqualTo(hour);
        assertThat(SchoolConfiguration.getCronExpression().getMinute()).isEqualTo(minutes);
        assertThat(SchoolConfiguration.getMaxInactive()).isEqualTo(maxInactive);
        assertThat(SchoolConfiguration.getDeadlineMissedLesson()).isEqualTo(deadlineMissedLesson);
        assertThat(SchoolConfiguration.getAvailableCategories().size()).isEqualTo(availableCategories.size());
    }

}
