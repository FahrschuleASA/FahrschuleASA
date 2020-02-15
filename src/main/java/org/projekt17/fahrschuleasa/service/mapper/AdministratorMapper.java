package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.Administrator;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.service.dto.AdministratorDTO;


@Mapper(componentModel = "spring")
public interface AdministratorMapper extends EntityMapper<AdministratorDTO, Administrator> {

    AdministratorMapper INSTANCE = Mappers.getMapper(AdministratorMapper.class);

    default AdministratorDTO toDto(Administrator administrator){
        if (administrator == null) {
            return null;
        }
        return new AdministratorDTO(administrator);
    }

    default Administrator toEntity(AdministratorDTO administratorDTO){
        if (administratorDTO == null) {
            return null;
        }
        Administrator administrator = new Administrator();
        UserMapper userMapper = new UserMapper();
        User user = userMapper.userDTOToUser(administratorDTO.getUser());

        administrator.setUser(user);
        administrator.setId(administratorDTO.getId());
        administrator.setActive(administratorDTO.getActive());
        administrator.setBirthdate(administratorDTO.getBirthdate());
        administrator.setAddress(administratorDTO.getAddress());
        administrator.setPhoneNumber(administratorDTO.getPhoneNumber());
        administrator.setNewEmail(administratorDTO.getNewEmail());
        return administrator;
    }

    default Administrator fromId(Long id) {
        if (id == null) {
            return null;
        }
        Administrator admin = new Administrator();
        admin.setId(id);
        return admin;
    }
}
