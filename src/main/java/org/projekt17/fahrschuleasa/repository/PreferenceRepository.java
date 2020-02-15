package org.projekt17.fahrschuleasa.repository;

import org.projekt17.fahrschuleasa.domain.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Preference entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    Optional<Preference> findByStudentIdAndTimeSlotId(Long studentId, Long timeSlotId);

    List<Preference> findAllByStudentId(Long id);

    List<Preference> findAllByStudentUserLogin(String login);

    Optional<Preference> findByIdAndStudentUserLogin(Long preferenceId, String studentLogin);

    List<Preference> findAllByTimeSlotId(Long id);

}
