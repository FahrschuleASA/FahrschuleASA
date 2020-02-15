package org.projekt17.fahrschuleasa.repository;

import org.projekt17.fahrschuleasa.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Teacher entity.
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByUserLogin(String login);

    Optional<Teacher> findByUserId(Long id);

    List<Teacher> findAllByUserActivatedIsTrue();

    List<Teacher> findAllByDeactivatedUntilIsNotNullAndDeactivatedUntilLessThanEqual(LocalDate now);

}
