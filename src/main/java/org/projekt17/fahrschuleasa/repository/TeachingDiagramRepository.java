package org.projekt17.fahrschuleasa.repository;
import org.projekt17.fahrschuleasa.domain.TeachingDiagram;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the TeachingDiagram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeachingDiagramRepository extends JpaRepository<TeachingDiagram, Long> {

    //Optional<TeachingDiagram> findOneBy

}
