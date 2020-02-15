package org.projekt17.fahrschuleasa.repository;

import org.projekt17.fahrschuleasa.domain.DrivingLesson;
import org.projekt17.fahrschuleasa.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Spring Data  repository for the DrivingLesson entity.
 */
@Repository
public interface DrivingLessonRepository extends JpaRepository<DrivingLesson, Long> {

    List<DrivingLesson> findAllByDriverIsNotNullAndDriverId(Long id);

    List<DrivingLesson> findAllByBeginEquals(LocalDateTime begin);

    List<DrivingLesson> findAllByBeginGreaterThanEqual(LocalDateTime begin);

    List<DrivingLesson> findAllByDriverIsNullAndTeacherIdAndBeginGreaterThan(Long id, LocalDateTime baseDate);

    List<DrivingLesson> findAllByDriverIsNullAndTeacherIdAndBeginGreaterThanAndBookableIsTrue(Long id, LocalDateTime baseDate);

    List<DrivingLesson> findAllByDriverIsNullAndTeacherUserLoginAndBeginGreaterThan(String login, LocalDateTime baseDate);

    List<DrivingLesson> findAllByDriverIsNullAndBeginGreaterThan(LocalDateTime baseDate);

    Optional<DrivingLesson> findByIdAndDriverNotNullAndDriverUserLogin(Long drivingLessonId, String login);

    List<DrivingLesson> findAllByMissingStudentsContaining(Set<Student> student);

    List<DrivingLesson> findAllByDriverIsNotNullAndTeacherId(Long id);

    List<DrivingLesson> findAllByDriverIsNotNullAndTeacherUserLogin(String login);

    List<DrivingLesson> findAllByDriverIsNotNull();

    List<DrivingLesson> findAllByDriverIsNullAndBeginAfterAndEndBefore(LocalDateTime begin, LocalDateTime end);

    List<DrivingLesson> findAllByDriverIsNotNullAndTeacherIdAndBeginGreaterThanEqualAndBeginBefore(Long teacherId, LocalDateTime begin, LocalDateTime end);

    List<DrivingLesson> findAllByDriverIsNotNullAndTeacherIdAndBeginAfterAndEndLessThanEqualOrderByEndDesc(Long teacherId, LocalDateTime begin, LocalDateTime end);

    List<DrivingLesson> findAllByDriverIsNotNullAndTeacherIdAndBeginGreaterThanEqualAndBeginBeforeOrderByBeginAsc(Long teacherId, LocalDateTime begin, LocalDateTime end);
}
