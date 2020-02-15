package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.service.dto.TeacherDTO;

@Mapper(componentModel = "spring")
public interface TeacherMapper extends EntityMapper<TeacherDTO, Teacher> {

    TeacherMapper INSTANCE = Mappers.getMapper( TeacherMapper.class );

    default TeacherDTO toDto(Teacher teacher){
        return new TeacherDTO(teacher);
    }

    default Teacher toEntity(TeacherDTO teacherDTO){
        if (teacherDTO == null) {
            return null;
        }

        Teacher teacher = new Teacher();
        UserMapper userMapper = new UserMapper();
        User user = userMapper.userDTOToUser(teacherDTO.getUser());
        teacher.setUser(user);

        teacher.setId(teacherDTO.getId());
        teacher.setActive(teacherDTO.getActive());
        teacher.setBirthdate(teacherDTO.getBirthdate());
        teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        teacher.setAddress(teacherDTO.getAddress());
        teacher.setNewEmail(teacherDTO.getNewEmail());

        teacher.setChangedTimeSlots(teacherDTO.getChangedTimeSlots());
        teacher.setSchoolOwner(teacherDTO.getSchoolOwner());

        return teacher;
    }


    default Teacher fromId(Long id) {
        if (id == null) {
            return null;
        }
        Teacher teacher = new Teacher();
        teacher.setId(id);
        return teacher;
    }
}
