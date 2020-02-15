package org.projekt17.fahrschuleasa.service.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.Administrator;
import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.service.dto.AdministratorDTO;
import org.projekt17.fahrschuleasa.service.dto.UserDTO;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class AdministratorMapperIT {

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final Long DEFAULT_ID = 1L;

    private static final Long id = 0L;
    private static final Boolean active = true;
    private static final LocalDate birthdate = LocalDate.of(1998,2,22);
    private static final Location address = new Location();
    private static final String phoneNumber = "0000";

    private User user;
    private UserDTO userDTO;
    private Administrator administrator;
    private AdministratorDTO administratorDTO;

    private Administrator administratorTest;
    private AdministratorDTO administratorDTOTest;

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

        administrator = new Administrator();

        administrator.setUser(user);
        administrator.setId(id);
        administrator.setActive(active);
        administrator.setBirthdate(birthdate);
        administrator.setAddress(address);
        administrator.setPhoneNumber(phoneNumber);

        administratorDTO = new AdministratorDTO(administrator);
    }

    @Test
    public void simpleTestAdministratorToAdministratorDto() {
        administratorDTOTest = AdministratorMapper.INSTANCE.toDto(administrator);

        assertThat(administratorDTOTest).isNotNull();
        assertThat(administratorDTOTest.getId()).isEqualTo(id);
        assertThat(administratorDTOTest.getActive()).isEqualTo(active);
        assertThat(administratorDTOTest.getBirthdate()).isEqualTo(birthdate);
        assertThat(administratorDTOTest.getAddress()).isEqualTo(address);
        assertThat(administratorDTOTest.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(administratorDTOTest.getUser()).isNotNull();
    }

    @Test
    public void simpleTestAdministratorDtoToAdministrator() {
        administratorTest = AdministratorMapper.INSTANCE.toEntity(administratorDTO);

        assertThat(administratorTest).isNotNull();

        assertThat(administratorTest.getId()).isEqualTo(id);
        assertThat(administratorTest.isActive()).isEqualTo(active);
        assertThat(administratorTest.getBirthdate()).isEqualTo(birthdate);
        assertThat(administratorTest.getAddress()).isEqualTo(address);
        assertThat(administratorTest.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(administratorTest.getUser()).isNotNull();
    }

    @Test
    public void simpleTestFromId(){
        administratorTest = AdministratorMapper.INSTANCE.fromId(0L);
        assertThat(administratorTest).isNotNull();

        administratorTest = AdministratorMapper.INSTANCE.fromId(null);
        assertThat(administratorTest).isNull();
    }
}
