package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.domain.Preference;
import org.projekt17.fahrschuleasa.repository.PreferenceRepository;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TimeSlotRepository;
import org.projekt17.fahrschuleasa.service.dto.PreferenceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class PreferenceService {

    private final Logger log = LoggerFactory.getLogger(PreferenceService.class);

    private final PreferenceRepository preferenceRepository;

    private final StudentRepository studentRepository;

    private final TimeSlotRepository timeSlotRepository;

    private final LocationService locationService;

    public PreferenceService(PreferenceRepository preferenceRepository, StudentRepository studentRepository,
                             TimeSlotRepository timeSlotRepository, LocationService locationService) {
        this.preferenceRepository = preferenceRepository;
        this.studentRepository = studentRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.locationService = locationService;
    }

    public Optional<PreferenceDTO> createPreference(PreferenceDTO preferenceDTO, String login) {
        Preference preference = new Preference();

        return timeSlotRepository.findById(preferenceDTO.getTimeSlotId())
            .flatMap(timeSlot -> studentRepository.findByUserLogin(login)
            .map(student -> {
                preference.setPickup(locationService.createLocation(preferenceDTO.getPickup()));
                preference.setDestination(locationService.createLocation(preferenceDTO.getDestination()));
                preference.setStudent(student);
                preference.setTimeSlot(timeSlot);
                student.setChangedPreferences(true);
                log.info("Es wurde eine neue Präferenz für den Fahrschüler {} angelegt: {}",
                    student.getUser().getFirstName() + " " + student.getUser().getLastName(),
                    preference);
                return new PreferenceDTO(preferenceRepository.save(preference));
            }));
    }

    public Optional<PreferenceDTO> updatePreference(PreferenceDTO preferenceDTO) {
        return preferenceRepository.findById(preferenceDTO.getId()).map(
            preference -> {
                preference.setPickup(locationService.createLocation(preferenceDTO.getPickup()));
                preference.setDestination(locationService.createLocation(preferenceDTO.getDestination()));
                log.info("Es wurde die Präferenz vom Fahrschüler {} abgeändert: {}",
                    preference.getStudent().getUser().getFirstName() + " " + preference.getStudent().getUser().getLastName(),
                    preference);
                return new PreferenceDTO(preference);
            });
    }

    public void deletePreference(Long id, String login) {
        preferenceRepository.deleteById(id);
        log.info("Es wurde eine Präferenz eines Fahrschülers gelöscht");
        studentRepository.findByUserLogin(login).orElseThrow(() -> new IllegalArgumentException(
            String.format("Could not update changedPreferences of Student %s, because the Student is not found", login)))
            .setChangedPreferences(true);
    }

    @Transactional(readOnly = true)
    public List<PreferenceDTO> getAllPreferencesForStudentId(Long id) {
        return preferenceRepository.findAllByStudentId(id).stream()
            .map(PreferenceDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PreferenceDTO> getAllPreferencesForStudentLogin(String login) {
        return preferenceRepository.findAllByStudentUserLogin(login).stream()
            .map(PreferenceDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PreferenceDTO> getPreference(Long id) {
        return preferenceRepository.findById(id).map(PreferenceDTO::new);
    }

    @Transactional(readOnly = true)
    public boolean isPreferenceOfStudent(Long preferenceId, String studentLogin) {
        return preferenceRepository.findByIdAndStudentUserLogin(preferenceId, studentLogin).isPresent();
    }
}
