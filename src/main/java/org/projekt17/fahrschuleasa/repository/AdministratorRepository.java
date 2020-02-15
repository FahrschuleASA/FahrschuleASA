package org.projekt17.fahrschuleasa.repository;

import org.projekt17.fahrschuleasa.domain.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Administrator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    Optional<Administrator> findByUserLogin(String login);

    Optional<Administrator> findByUserId(Long id);

    List<Administrator> findAllByUserActivatedIsTrue();
}
