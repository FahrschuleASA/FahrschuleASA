package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.MyAccount;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;

@Mapper(componentModel = "spring")
public interface MyAccountMapper extends EntityMapper<MyAccountDTO, MyAccount> {

    MyAccountMapper INSTANCE = Mappers.getMapper(MyAccountMapper.class);

    default MyAccountDTO toDto(MyAccount myAccount) {
        return new MyAccountDTO(myAccount);
    }

    default MyAccount toEntity(MyAccountDTO myAccountDTO) {
        if (myAccountDTO == null) {
            return null;
        } else {
            MyAccount myAccount = new MyAccount();

            UserMapper userMapper = new UserMapper();
            User user = userMapper.userDTOToUser(myAccountDTO.getUser());

            myAccount.setPhoneNumber(myAccountDTO.getPhoneNumber());
            myAccount.setUser(user);
            myAccount.setId(myAccountDTO.getId());
            myAccount.setActive(myAccountDTO.getActive());
            myAccount.setBirthdate(myAccountDTO.getBirthdate());
            myAccount.setAddress(myAccountDTO.getAddress());
            return myAccount;
        }
    }

    default MyAccount fromId(Long id){
        if (id == null) {
            return null;
        }
        MyAccount myAccount = new MyAccount();
        myAccount.setId(id);
        return myAccount;
    }
}
