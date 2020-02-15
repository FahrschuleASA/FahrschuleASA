package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.Administrator;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.repository.AdministratorRepository;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.service.dto.AdministratorDTO;
import org.projekt17.fahrschuleasa.service.dto.SchoolConfigurationDTO;
import org.projekt17.fahrschuleasa.service.mapper.SchoolConfigurationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdministratorService {

    private final Logger log = LoggerFactory.getLogger(AdministratorService.class);

    private final SchoolConfigurationMapper schoolConfigurationMapper;

    private final UserService userService;

    private final AdministratorRepository administratorRepository;

    private final MailService mailService;

    public AdministratorService(SchoolConfigurationMapper schoolConfigurationMapper, UserService userService,
                                AdministratorRepository administratorRepository, MailService mailService) {
        this.schoolConfigurationMapper = schoolConfigurationMapper;
        this.userService = userService;
        this.administratorRepository = administratorRepository;
        this.mailService = mailService;
    }

    public AdministratorDTO createAdministrator(AdministratorDTO administratorDTO) {
        Administrator administrator = new Administrator();

        Set<String> authorities = new HashSet<>();
        authorities.add(AuthoritiesConstants.USER);
        authorities.add(AuthoritiesConstants.ADMIN);
        administratorDTO.getUser().setAuthorities(authorities);

        userService.createMyAccount(administrator, administratorDTO);

        administratorRepository.save(administrator);
        mailService.sendCreationEmail(administrator.getUser());

        log.debug("Created Information for Administrator: {}", administrator);
        log.info("Ein neuer Administrator wurde angelegt: {}", administrator);
        return new AdministratorDTO(administrator);
    }

    public SchoolConfigurationDTO updateConfiguration(SchoolConfigurationDTO schoolConfigurationDTO) {
        schoolConfigurationMapper.configurationDTOToConfiguration(schoolConfigurationDTO);
        SchoolConfiguration.save();
        log.info("Die Fahrschulkonfiguration wurde angepasst: {}", SchoolConfiguration.debugString());
        return schoolConfigurationMapper.configurationToConfigurationDTO();
    }

    public String deleteAdministrator(Long id, String login) {
        User user = administratorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Administrator %d not found", id)))
            .getUser();
        Administrator currentAdmin = administratorRepository.findByUserLogin(login)
            .orElseThrow(() -> new IllegalArgumentException("Current administrator not found"));
        if (currentAdmin.getUser().getId().equals(user.getId()))
            throw new IllegalStateException("Administrator could not delete himself!");
        user.setActivated(false);
        userService.clearUserCaches(user);
        log.info("Administrator mit id {} wurde deaktiviert", id);
        return user.getFirstName() + " " + user.getLastName();
    }

    @Transactional(readOnly = true)
    public List<AdministratorDTO> getAllAdministrators(boolean activeOnly) {
        if (activeOnly)
            return administratorRepository.findAllByUserActivatedIsTrue().stream()
                .map(AdministratorDTO::new).collect(Collectors.toList());

    return administratorRepository.findAll().stream().map(AdministratorDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AdministratorDTO> getAdministratorByLogin(String login) {
        return administratorRepository.findByUserLogin(login).map(AdministratorDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<AdministratorDTO> getAdministratorById(Long id) {
        return administratorRepository.findById(id).map(AdministratorDTO::new);
    }
}
