package org.projekt17.fahrschuleasa.web.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.config.Constants;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.repository.AdministratorRepository;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.repository.UserRepository;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.service.MailService;
import org.projekt17.fahrschuleasa.service.StudentService;
import org.projekt17.fahrschuleasa.service.UserService;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.service.dto.PasswordChangeDTO;
import org.projekt17.fahrschuleasa.service.dto.UserDTO;
import org.projekt17.fahrschuleasa.web.rest.errors.ExceptionTranslator;
import org.projekt17.fahrschuleasa.web.rest.vm.KeyAndPasswordVM;
import org.projekt17.fahrschuleasa.web.rest.vm.ManagedUserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AccountResource} REST controller.
 */
@SpringBootTest(classes = FahrschuleAsaApp.class)
public class AccountResourceIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpMessageConverter<?>[] httpMessageConverters;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Mock
    private UserService mockUserService;

    @Mock
    private MailService mockMailService;

    @Mock
    private StudentService mockStudentService;

    private MockMvc restMvc;

    private MockMvc restUserMockMvc;

    private EntityCreationHelper entityCreationHelper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendActivationEmail(any());
        AccountResource accountResource =
            new AccountResource(userService, studentService, mockMailService);

        AccountResource accountUserMockResource =
            new AccountResource(mockUserService, mockStudentService, mockMailService);
        this.restMvc = MockMvcBuilders.standaloneSetup(accountResource)
            .setMessageConverters(httpMessageConverters)
            .setControllerAdvice(exceptionTranslator)
            .build();
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountUserMockResource)
            .setControllerAdvice(exceptionTranslator)
            .build();

        entityCreationHelper = new EntityCreationHelper();
    }

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate")
            .with(request -> {
                request.setRemoteUser("test");
                return request;
            })
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("test"));
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(authority);

        User user = new User();
        user.setLogin("test");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail("john.doe@jhipster.com");
        user.setImageUrl("http://placehold.it/50x50");
        user.setLangKey("en");
        user.setAuthorities(authorities);
        when(mockUserService.getUserWithAuthorities()).thenReturn(Optional.of(user));

        restUserMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.login").value("test"))
            .andExpect(jsonPath("$.firstName").value("john"))
            .andExpect(jsonPath("$.lastName").value("doe"))
            .andExpect(jsonPath("$.email").value("john.doe@jhipster.com"))
            .andExpect(jsonPath("$.imageUrl").value("http://placehold.it/50x50"))
            .andExpect(jsonPath("$.langKey").value("en"))
            .andExpect(jsonPath("$.authorities").value(AuthoritiesConstants.ADMIN));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        when(mockUserService.getUserWithAuthorities()).thenReturn(Optional.empty());

        restUserMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isInternalServerError());
    }

    private void checkMyAccount() throws Exception {
        restMvc.perform(get("/api/my-account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.active").value(entityCreationHelper.getDEFAULT_ACTIVE()))
            .andExpect(jsonPath("$.birthdate").value(entityCreationHelper.getDEFAULT_DATE_OF_BIRTH().toString()))
            .andExpect(jsonPath("$.address.town").value(entityCreationHelper.getDEFAULT_TOWN()))
            .andExpect(jsonPath("$.address.street").value(entityCreationHelper.getDEFAULT_STREET()))
            .andExpect(jsonPath("$.address.postal").value(entityCreationHelper.getDEFAULT_POSTAL()))
            .andExpect(jsonPath("$.address.houseNumber").value(entityCreationHelper.getDEFAULT_HOUSE_NUMBER()))
            .andExpect(jsonPath("$.address.country").value(entityCreationHelper.getDEFAULT_COUNTRY()))
            .andExpect(jsonPath("$.address.additional").value(entityCreationHelper.getDEFAULT_ADDITIONAL()))
            .andExpect(jsonPath("$.phoneNumber").value(entityCreationHelper.getDEFAULT_PHONE_NUMBER()))
            .andExpect(jsonPath("$.user.login").value(entityCreationHelper.getDEFAULT_LOGIN()))
            .andExpect(jsonPath("$.user.firstName").value(entityCreationHelper.getDEFAULT_FIRSTNAME()))
            .andExpect(jsonPath("$.user.lastName").value(entityCreationHelper.getDEFAULT_LASTNAME()))
            .andExpect(jsonPath("$.user.email").value(entityCreationHelper.getDEFAULT_EMAIL()));
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void testGetExistingMyAccountAdmin() throws Exception {
        Administrator administrator = entityCreationHelper.createEntityAdministrator(false);
        administratorRepository.save(administrator);
        checkMyAccount();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void testGetExistingMyAccountTeacher() throws Exception {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        checkMyAccount();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void testGetExistingMyAccountStudent() throws Exception {
        Student student = entityCreationHelper.createEntityStudent(false);
        studentRepository.save(student);
        checkMyAccount();
    }

    @Test
    @Transactional
    @EntityCreationHelper.WithMockAdmin
    public void testGetUnkownMyAccount() throws Exception {
        restMvc.perform(get("/api/my-account")
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testActivateNewEmail() throws Exception {
        final String activationKey = "some activation key";
        User user = new User();
        user.setLogin("activate-email");
        user.setEmail("activate-email@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setActivationKey(activationKey);

        Administrator administrator = new Administrator();
        administrator.setUser(user);
        administrator.setNewEmail("new-email@example.com");

        administratorRepository.saveAndFlush(administrator);

        restMvc.perform(get("/api/activate/email?key={activationKey}", activationKey))
            .andExpect(status().isOk());

        Optional<Administrator> myAccount = administratorRepository.findByUserLogin(user.getLogin());
        assertThat(myAccount.isPresent()).isTrue();
        assertThat(myAccount.get().getNewEmail()).isEqualTo(null);
        assertThat(myAccount.get().getUser().getEmail()).isEqualTo("new-email@example.com");
        assertThat(myAccount.get().getUser().getActivationKey()).isEqualTo(null);
    }

    @Test
    @Transactional
    public void testActivateNewEmailWithWrongKey() throws Exception {
        restMvc.perform(get("/api/activate/email?key=wrongActivationKey"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    @WithMockUser("save-account")
    public void testSaveMyAccount() throws Exception {
        User user = new User();
        user.setLogin("save-account");
        user.setEmail("save-account@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setLangKey(Constants.DEFAULT_LANGUAGE);

        Administrator administrator = new Administrator();
        administrator.setUser(user);
        administrator.setBirthdate(LocalDate.of(2000, 4, 23));
        administrator.setAddress(entityCreationHelper.createEntityLocation());
        administrator.setPhoneNumber("1234567890");
        administrator.setActive(true);

        administratorRepository.saveAndFlush(administrator);

        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("not-used");
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("new-email@example.com");
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        MyAccountDTO myAccountDTO = new MyAccountDTO();
        myAccountDTO.setId(1L);
        myAccountDTO.setUser(userDTO);
        myAccountDTO.setBirthdate(LocalDate.of(2002, 3, 2));
        myAccountDTO.setAddress(entityCreationHelper.createAlterEntityLocation());
        myAccountDTO.setPhoneNumber("0987654321");
        myAccountDTO.setActive(false);

        restMvc.perform(
            put("/api/my-account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(myAccountDTO)))
            .andExpect(status().isOk());

        Optional<Administrator> optionalAdministrator = administratorRepository.findByUserLogin(user.getLogin());
        assertThat(optionalAdministrator.isPresent()).isTrue();
        administrator = optionalAdministrator.get();
        assertThat(administrator.getUser().getFirstName()).isEqualTo(userDTO.getFirstName());
        assertThat(administrator.getUser().getLastName()).isEqualTo(userDTO.getLastName());
        assertThat(administrator.getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(administrator.getNewEmail()).isEqualTo(userDTO.getEmail());
        assertThat(administrator.getUser().getPassword()).isEqualTo(user.getPassword());
        assertThat(administrator.getUser().getActivated()).isEqualTo(true);
        assertThat(administrator.getUser().getAuthorities()).isEmpty();
        assertThat(administrator.getUser().getActivationKey()).isNotNull();
        assertThat(administrator.getBirthdate()).isEqualTo(myAccountDTO.getBirthdate());
        assertThat(administrator.getAddress().checkSimilar(myAccountDTO.getAddress())).isTrue();
        assertThat(administrator.getPhoneNumber()).isEqualTo(myAccountDTO.getPhoneNumber());
        assertThat(administrator.isActive()).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("save-invalid-email")
    public void testSaveInvalidEmail() throws Exception {
        User user = new User();
        user.setLogin("save-invalid-email");
        user.setEmail("save-invalid-email@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        Administrator administrator = new Administrator();
        administrator.setUser(user);
        administrator.setBirthdate(LocalDate.of(2000, 4, 23));
        administrator.setAddress(entityCreationHelper.createEntityLocation());
        administrator.setPhoneNumber("1234567890");
        administrator.setActive(true);

        administratorRepository.saveAndFlush(administrator);

        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("not-used");
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("invalid email");
        userDTO.setActivated(false);
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        MyAccountDTO myAccountDTO = new MyAccountDTO();
        myAccountDTO.setId(1L);
        myAccountDTO.setUser(userDTO);
        myAccountDTO.setBirthdate(LocalDate.of(2002, 3, 2));
        myAccountDTO.setAddress(entityCreationHelper.createAlterEntityLocation());
        myAccountDTO.setPhoneNumber("0987654321");

        restMvc.perform(
            put("/api/my-account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(myAccountDTO)))
            .andExpect(status().isBadRequest());

        assertThat(userRepository.findOneByEmailIgnoreCase("invalid email")).isNotPresent();
    }

    @Test
    @Transactional
    @WithMockUser("change-password-wrong-existing-password")
    public void testChangePasswordWrongExistingPassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password-wrong-existing-password");
        user.setEmail("change-password-wrong-existing-password@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO("1"+currentPassword, "new password"))))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change-password-wrong-existing-password").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("change-password")
    public void testChangePassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password");
        user.setEmail("change-password@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, "new password"))))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin("change-password").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("change-password-too-small")
    public void testChangePasswordTooSmall() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password-too-small");
        user.setEmail("change-password-too-small@example.com");
        userRepository.saveAndFlush(user);

        String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MIN_LENGTH - 1);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, newPassword))))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change-password-too-small").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    @WithMockUser("change-password-too-long")
    public void testChangePasswordTooLong() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password-too-long");
        user.setEmail("change-password-too-long@example.com");
        userRepository.saveAndFlush(user);

        String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MAX_LENGTH + 1);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, newPassword))))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change-password-too-long").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    @WithMockUser("change-password-empty")
    public void testChangePasswordEmpty() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("change-password-empty");
        user.setEmail("change-password-empty@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change-password")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, ""))))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change-password-empty").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    public void testRequestPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setLogin("password-reset");
        user.setEmail("password-reset@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/reset-password/init")
            .content("password-reset@example.com"))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testRequestPasswordResetUpperCaseEmail() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setLogin("password-reset");
        user.setEmail("password-reset@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/reset-password/init")
            .content("password-reset@EXAMPLE.COM"))
            .andExpect(status().isOk());
    }

    @Test
    public void testRequestPasswordResetWrongEmail() throws Exception {
        restMvc.perform(
            post("/api/account/reset-password/init")
                .content("password-reset-wrong-email@example.com"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testFinishPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("finish-password-reset");
        user.setEmail("finish-password-reset@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("new password");

        restMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    public void testFinishPasswordResetTooSmall() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("finish-password-reset-too-small");
        user.setEmail("finish-password-reset-too-small@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key too small");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("foo");

        restMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isFalse();
    }


    @Test
    @Transactional
    public void testFinishPasswordResetWrongKey() throws Exception {
        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey("wrong reset key");
        keyAndPassword.setNewPassword("new password");

        restMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isInternalServerError());
    }
}
