package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.DrivingLesson;
import org.projekt17.fahrschuleasa.service.dto.DrivingLessonDTO;

@Mapper(componentModel = "spring")
public interface DrivingLessonMapper extends EntityMapper<DrivingLessonDTO, DrivingLesson>{

    DrivingLessonMapper INSTANCE = Mappers.getMapper(DrivingLessonMapper.class);

    default DrivingLessonDTO toDto(DrivingLesson drivingLesson){
        return new DrivingLessonDTO(drivingLesson);
    }

    default DrivingLesson toEntity(DrivingLessonDTO drivingLessonDTO){
        if (drivingLessonDTO == null) {
            return null;
        }

        DrivingLesson drivingLesson = new DrivingLesson();
        drivingLesson.setId(drivingLessonDTO.getId());
        drivingLesson.setBegin(drivingLessonDTO.getBegin());
        drivingLesson.setEnd(drivingLessonDTO.getEnd());
        drivingLesson.setLessonType(drivingLessonDTO.getLessonType());
        drivingLesson.setPickup(drivingLessonDTO.getPickup());
        drivingLesson.setDestination(drivingLessonDTO.getDestination());
        drivingLesson.setBookable(drivingLessonDTO.getBookable());

        return drivingLesson;
    }

    default DrivingLesson fromId(Long id) {
        if (id == null) {
            return null;
        }
        DrivingLesson lesson = new DrivingLesson();
        lesson.setId(id);
        return lesson;
    }
}
