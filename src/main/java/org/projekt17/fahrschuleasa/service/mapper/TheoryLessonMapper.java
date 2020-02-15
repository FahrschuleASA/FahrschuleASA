package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.TheoryLesson;
import org.projekt17.fahrschuleasa.service.dto.TheoryLessonDTO;

@Mapper(componentModel = "spring")
public interface TheoryLessonMapper extends EntityMapper<TheoryLessonDTO, TheoryLesson> {

    TheoryLessonMapper INSTANCE = Mappers.getMapper(TheoryLessonMapper.class);

    default TheoryLessonDTO toDto(TheoryLesson theoryLesson){
        return new TheoryLessonDTO(theoryLesson);
    }

    default TheoryLesson toEntity(TheoryLessonDTO theoryLessonDTO){
        if (theoryLessonDTO == null) {
            return null;
        }

        TheoryLesson theoryLesson = new TheoryLesson();
        theoryLesson.setId(theoryLessonDTO.getId());
        theoryLesson.setBegin(theoryLessonDTO.getBegin());
        theoryLesson.setEnd(theoryLessonDTO.getEnd());

        Teacher teacher = new Teacher();
        teacher.setId(theoryLessonDTO.getTeacherId());
        theoryLesson.setTeacher(teacher);
        theoryLesson.setSubject(theoryLessonDTO.getSubject());

        return theoryLesson;
    }

    default TheoryLesson fromId(Long id){
        if (id == null) {
            return null;
        }
        TheoryLesson theoryLesson = new TheoryLesson();
        theoryLesson.setId(id);
        return theoryLesson;
    }
}
