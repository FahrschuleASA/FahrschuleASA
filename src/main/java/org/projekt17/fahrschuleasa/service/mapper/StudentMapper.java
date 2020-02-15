package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.service.dto.StudentDTO;

@Mapper(componentModel = "spring")
public interface StudentMapper extends EntityMapper<StudentDTO, Student> {

    StudentMapper INSTANCE = Mappers.getMapper( StudentMapper.class );

    default StudentDTO toDto(Student student){
        return new StudentDTO(student);
    }

    default Student toEntity(StudentDTO studentDTO){
        if (studentDTO == null) {
            return null;
        }

        Student student = new Student();
        UserMapper userMapper = new UserMapper();
        User user = userMapper.userDTOToUser(studentDTO.getUser());

        student.setUser(user);
        student.setId(studentDTO.getId());
        student.setActive(studentDTO.getActive());
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setBirthdate(studentDTO.getBirthdate());
        student.setAddress(studentDTO.getAddress());

        student.setCategory(studentDTO.getCategory());
        student.setReadyForTheory(studentDTO.getReadyForTheory());
        student.setNotifyForFreeLesson(studentDTO.getNotifyForFreeLesson());
        student.setWantedLessons(studentDTO.getWantedLessons());
        student.setAllowedLessons(studentDTO.getAllowedLessons());
        student.setChangedPreferences(studentDTO.getChangedPreferences());
        student.setNewEmail(studentDTO.getNewEmail());

        /* remind that teacher here has just an id nothing else */
        Teacher teacher = new Teacher();
        teacher.setId(studentDTO.getTeacherId());
        student.setTeacher(teacher);

        return student;
    }

    default Student fromId(Long id) {
        if (id == null) {
            return null;
        }
        Student student = new Student();
        student.setId(id);
        return student;
    }
}
