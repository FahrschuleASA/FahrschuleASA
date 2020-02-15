package org.projekt17.fahrschuleasa.repository;
import org.projekt17.fahrschuleasa.domain.TheoryLesson;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Spring Data  repository for the TheoryLesson entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TheoryLessonRepository extends JpaRepository<TheoryLesson, Long> {

    List<TheoryLesson> findAllByTeacherId(Long id);

    List<TheoryLesson> findAllByTeacherUserLogin(String login);

    List<TheoryLesson> findAllByTeacherIdAndBeginAfter(Long id, LocalDateTime begin);

}
