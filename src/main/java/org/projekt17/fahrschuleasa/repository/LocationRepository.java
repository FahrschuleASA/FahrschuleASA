package org.projekt17.fahrschuleasa.repository;
import org.projekt17.fahrschuleasa.domain.Location;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Location entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {


    @Query("select l from Location l where l.additional = :ad and " +
        "l.country = :co and l.houseNumber = :hn and l.latitude = :la " +
        "and l.longitude = :lo and l.postal = :po  and l.street = :st and l.town = :to")
    Optional<Location> findLocationByAllButNotId(@Param("lo") Double longitude, @Param("la") Double latitude,
                                       @Param("to") String town, @Param("st") String street,
                                       @Param("po") String postal, @Param("hn") Integer houseNumber,
                                       @Param("co") String country, @Param("ad") String additional);
}
