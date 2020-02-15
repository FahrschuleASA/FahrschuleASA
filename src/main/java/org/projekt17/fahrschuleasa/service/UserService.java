package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.config.Constants;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.Authority;
import org.projekt17.fahrschuleasa.domain.MyAccount;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.repository.*;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.service.dto.UserDTO;
import org.projekt17.fahrschuleasa.service.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final StudentRepository studentRepository;

    private final TeacherRepository teacherRepository;

    private final AdministratorRepository administratorRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    private final LocationService locationService;

    private final MailService mailService;

    public UserService(UserRepository userRepository, StudentRepository studentRepository,
                       TeacherRepository teacherRepository, AdministratorRepository administratorRepository,
                       PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository,
                       CacheManager cacheManager, LocationService locationService, MailService mailService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.locationService = locationService;
        this.mailService = mailService;
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    public User createUser(UserDTO userDTO) {
        log.debug("created user: {}", userDTO);
        User user = new User();

        String login = "";
        String firstName = userDTO.getFirstName().replaceAll("[^_.@A-Za-z0-9-]", "");
        String lastName = userDTO.getLastName().replaceAll("[^_.@A-Za-z0-9-]", "");
        if (firstName.length() < 4)
            login += firstName.toLowerCase();
        else
            login += firstName.toLowerCase().substring(0, 4);
        if (lastName.length() < 8 - login.length())
            login += lastName.toLowerCase();
        else
            login += lastName.toLowerCase().substring(0, 8 - login.length());
        int length = login.length();
        int count = 1;
        while (userRepository.findOneByLogin(login).isPresent())
            login = login.substring(0, length) + count++;
        user.setLogin(login);

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail().toLowerCase());
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void createMyAccount(MyAccount account, MyAccountDTO myAccountDTO) {
        User newUser = createUser(myAccountDTO.getUser());
        account.setUser(newUser);

        account.setBirthdate(myAccountDTO.getBirthdate());
        account.setActive(false);
        account.setAddress(locationService.createLocation(myAccountDTO.getAddress()));
        account.setPhoneNumber(myAccountDTO.getPhoneNumber());
    }

    public void updateEmail(MyAccountDTO myAccountDTO, MyAccount myAccount) {
        if (!myAccountDTO.getUser().getEmail().toLowerCase().equals(myAccount.getUser().getEmail().toLowerCase())) {
            myAccount.setNewEmail(myAccountDTO.getUser().getEmail().toLowerCase());
            myAccount.getUser().setActivationKey(RandomUtil.generateActivationKey());
            mailService.sendEmailActivationEmail(myAccount.getUser());
        }
    }

    public MyAccountDTO updateMyAccount(MyAccountDTO myAccountDTO, MyAccount myAccount) {
        myAccount.setBirthdate(myAccountDTO.getBirthdate());
        myAccount.setAddress(locationService.createLocation(myAccountDTO.getAddress()));
        myAccount.setPhoneNumber(myAccountDTO.getPhoneNumber());

        updateEmail(myAccountDTO, myAccount);

        User user = myAccount.getUser();
        user.setLastName(myAccountDTO.getUser().getLastName());
        user.setFirstName(myAccountDTO.getUser().getFirstName());
        return new MyAccountDTO(myAccount);
    }

    public Optional<MyAccountDTO> updateMyAccount(MyAccountDTO myAccountDTO, String login) {
        Optional<? extends MyAccount> account = teacherRepository.findByUserLogin(login);
        if (!account.isPresent())
            account = administratorRepository.findByUserLogin(login);

        return account.map(myAccount -> updateMyAccount(myAccountDTO, myAccount));
    }

    public Optional<MyAccountDTO> updateMyAccountTeacher(MyAccountDTO myAccountDTO) {
        return teacherRepository.findById(myAccountDTO.getId())
            .map(myAccount -> updateMyAccount(myAccountDTO, myAccount));
    }

    public Optional<MyAccountDTO> updateMyAccountAdmin(MyAccountDTO myAccountDTO) {
        return administratorRepository.findById(myAccountDTO.getId())
            .map(myAccount -> updateMyAccount(myAccountDTO, myAccount));
    }

    public void setActiveStatus(MyAccountDTO myAccountDTO, MyAccount myAccount) {
        myAccount.setActive(myAccountDTO.getActive());
        if (!myAccount.isActive()) {
            if (SchoolConfiguration.getMaxInactive() > 0) {
                if (myAccountDTO.getDeactivatedDaysLeft() != null && myAccountDTO.getDeactivatedDaysLeft() > 0) {
                    myAccount.setDeactivatedUntil(LocalDate.now().plusDays(
                        myAccountDTO.getDeactivatedDaysLeft() > SchoolConfiguration.getMaxInactive() ?
                            SchoolConfiguration.getMaxInactive() : myAccountDTO.getDeactivatedDaysLeft()));
                } else
                    myAccount.setDeactivatedUntil(LocalDate.now().plusDays(SchoolConfiguration.getMaxInactive()));
            } else if (myAccountDTO.getDeactivatedDaysLeft() != null && myAccountDTO.getDeactivatedDaysLeft() > 0)
                myAccount.setDeactivatedUntil(LocalDate.now().plusDays(myAccountDTO.getDeactivatedDaysLeft()));
            else
                myAccount.setDeactivatedUntil(null);
        } else
            myAccount.setDeactivatedUntil(null);
    }

    public void activateNewMail(String key) {
        userRepository.findOneByActivationKey(key)
            .map(user -> {
                Optional<? extends MyAccount> account = teacherRepository.findByUserId(user.getId());
                if (!account.isPresent()) {
                    account = administratorRepository.findByUserId(user.getId());
                    if (!account.isPresent())
                        account = studentRepository.findByUserId(user.getId());
                }
                MyAccount myAccount = account
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Account to user id %d not found", user.getId())));

                user.setEmail(myAccount.getNewEmail());
                myAccount.setNewEmail(null);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated new email address of user: {}", myAccount);
                return user;
            }).orElseThrow(() -> new IllegalArgumentException("No user for email activation key found"));
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    @Transactional(readOnly = true)
    public Optional<MyAccountDTO> getMyAccountByLogin(String login) {
        Optional<MyAccountDTO> account = studentRepository.findByUserLogin(login).map(MyAccountDTO::new);
        if (account.isPresent())
            return account;

        account = teacherRepository.findByUserLogin(login).map(MyAccountDTO::new);
        if (account.isPresent())
            return account;

        return administratorRepository.findByUserLogin(login).map(MyAccountDTO::new);
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }


    public void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
    }
}
