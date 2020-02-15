package org.projekt17.fahrschuleasa.repository;
import org.projekt17.fahrschuleasa.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Student entity.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(value = "select distinct student from Student student left join fetch student.theoryLessons",
        countQuery = "select count(distinct student) from Student student")
    Page<Student> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct student from Student student left join fetch student.theoryLessons")
    List<Student> findAllWithEagerRelationships();

    @Query("select student from Student student left join fetch student.theoryLessons where student.id =:id")
    Optional<Student> findOneWithEagerRelationships(@Param("id") Long id);

    List<Student> findAllByTeacherId(Long teacherId);

    Optional<Student> findByTeacherUserLoginAndId(String teacherLogin, Long studentId);

    Optional<Student> findByUserLogin(String login);

    List<Student> findAllByUserActivatedIsTrue();

    List<Student> findAllByTeacherUserLogin(String login);

    List<Student> findAllByTeacherUserLoginAndUserActivatedIsTrue(String login);

    Optional<Student> findByTeachingDiagramId(Long id);

    Optional<Student> findByUserId(Long id);

    List<Student> findAllByDeactivatedUntilIsNotNullAndDeactivatedUntilLessThanEqual(LocalDate now);
}
