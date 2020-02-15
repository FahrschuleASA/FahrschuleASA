package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.service.dto.TimeSlotDTO;

@Mapper(componentModel = "spring")
public interface TimeSlotMapper extends EntityMapper<TimeSlotDTO, TimeSlot> {

    TimeSlotMapper INSTANCE = Mappers.getMapper(TimeSlotMapper.class);

    default TimeSlotDTO toDto(TimeSlot timeSlot) {
        return new TimeSlotDTO(timeSlot);
    }

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "lastScheduledStudent", ignore = true)
    TimeSlot toEntity(TimeSlotDTO timeSlotDTO);

    default TimeSlot fromId(Long id){
        if (id == null) {
            return null;
        }
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(id);
        return timeSlot;
    }
}
