package org.projekt17.fahrschuleasa.service.mapper;

import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.service.dto.SchoolConfigurationDTO;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class SchoolConfigurationMapper {

    public SchoolConfigurationDTO configurationToConfigurationDTO() {
        return new SchoolConfigurationDTO();
    }

    public void configurationDTOToConfiguration(SchoolConfigurationDTO schoolConfigurationDTO) {
        SchoolConfiguration.setInitialLessons(schoolConfigurationDTO.getInitialLessons());
        SchoolConfiguration.getCronExpression().setCronExpression(schoolConfigurationDTO.getPlanningWeekday(),
            schoolConfigurationDTO.getPlanningHour(), schoolConfigurationDTO.getPlanningMinute());
        SchoolConfiguration.setMaxInactive(schoolConfigurationDTO.getMaxInactive());
        SchoolConfiguration.setDeadlineMissedLesson(schoolConfigurationDTO.getDeadlineMissedLesson());
        if (schoolConfigurationDTO.getAvailableCategories() != null) {
            SchoolConfiguration.setAvailableCategories(schoolConfigurationDTO.getAvailableCategories().stream()
                .map(DrivingCategory::valueOf).collect(Collectors.toSet()));
        }
        SchoolConfiguration.setEmailSignature(schoolConfigurationDTO.getEmailSignature());
    }
}
