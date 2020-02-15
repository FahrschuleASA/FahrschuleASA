package org.projekt17.fahrschuleasa.service.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.domain.MyAccount;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.service.dto.UserDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class MyAccountMapperIT {


    private static final String DEFAULT_LOGIN = "johndoe";
    private static final Long DEFAULT_ID = 1L;

    private static final Long id = 0L;
    private static final Boolean active = true;
    private static final LocalDate birthdate = LocalDate.of(1998,2,22);
    private static final Location address = new Location();
    private static final String phoneNumber = "0000";

    private User user;
    private UserDTO userDTO;
    private MyAccount myAccount;
    private MyAccountDTO myAccountDTO;

    private MyAccount myAccountTest;
    private MyAccountDTO myAccountDTOTest;

    @BeforeEach
    public void init(){
        user = new User();

        user.setLogin(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail("johndoe@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("image_url");
        user.setLangKey("en");

        userDTO = new UserDTO(user);

        myAccount = new MyAccount();

        address.setId(0L);
        address.setAdditional("test");
        address.setCountry("germany");
        address.setPostal("66693");
        address.setHouseNumber(51);
        address.setStreet("Brauerstrasse");
        address.setLatitude(0.0);
        address.setLongitude(0.0);

        myAccount.setUser(user);
        myAccount.setId(id);
        myAccount.setActive(active);
        myAccount.setBirthdate(birthdate);
        myAccount.setAddress(address);
        myAccount.setPhoneNumber(phoneNumber);

        myAccountDTO = new MyAccountDTO(myAccount);
    }

    @Test
    public void simpleTestMyAccountToMyAccountDTO(){
        myAccountDTOTest = MyAccountMapper.INSTANCE.toDto(myAccount);

        assertThat(myAccountDTOTest).isNotNull();
        assertThat(myAccountDTOTest.getActive()).isEqualTo(true);
        assertThat(myAccountDTOTest.getAddress()).isEqualTo(address);
        assertThat(myAccountDTOTest.getBirthdate()).isEqualTo(birthdate);
        assertThat(myAccountDTOTest.getId()).isEqualTo(id);
        assertThat(myAccountDTOTest.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void simpleTestMyAccountDTOToMyAccount() {
        myAccountTest = MyAccountMapper.INSTANCE.toEntity(myAccountDTO);

        assertThat(myAccountTest).isNotNull();
        assertThat(myAccountTest.isActive()).isEqualTo(true);
        assertThat(myAccountTest.getAddress()).isEqualTo(address);
        assertThat(myAccountTest.getBirthdate()).isEqualTo(birthdate);
        assertThat(myAccountTest.getId()).isEqualTo(id);
        assertThat(myAccountTest.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void simpleTestFromId(){

        myAccount = MyAccountMapper.INSTANCE.fromId(DEFAULT_ID);

        assertThat(myAccount.getId()).isEqualTo(DEFAULT_ID);
    }

}
