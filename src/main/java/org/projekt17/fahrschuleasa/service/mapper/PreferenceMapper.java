package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.domain.Preference;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.service.dto.PreferenceDTO;

@Mapper(componentModel = "spring")
public interface PreferenceMapper extends EntityMapper<PreferenceDTO, Preference> {

    PreferenceMapper INSTANCE = Mappers.getMapper(PreferenceMapper.class);

    default PreferenceDTO toDto(Preference preference){
        return new PreferenceDTO(preference);
    }

    default Preference toEntity(PreferenceDTO preferenceDTO){
        if (preferenceDTO == null) {
            return null;
        }
        Preference preference = new Preference();
        preference.setId(preferenceDTO.getId());
        Location pickup = preferenceDTO.getPickup();
        Location destination = preferenceDTO.getDestination();
        preference.setPickup(pickup);
        preference.setDestination(destination);
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(preferenceDTO.getTimeSlotId());
        preference.setTimeSlot(timeSlot);
        Student student = new Student();
        student.setId(preferenceDTO.getStudentId());
        preference.setStudent(student);

        return preference;
    }

    default Preference fromId(Long id) {
        if (id == null) {
            return null;
        }
        Preference preference = new Preference();
        preference.setId(id);
        return preference;
    }
}
