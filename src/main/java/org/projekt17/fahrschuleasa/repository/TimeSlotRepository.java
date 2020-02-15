package org.projekt17.fahrschuleasa.repository;
import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Spring Data  repository for the TimeSlot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findAllByTeacherId(Long teacherId);

    List<TimeSlot> findAllByTeacherIdAndOptionalCategoriesContainingOrPreferredCategoriesContaining(Long teacherId,
                                                                                                    Set<DrivingCategory> optional,
                                                                                                    Set<DrivingCategory> preferred);

    List<TimeSlot> findAllByTeacherUserLogin(String login);

    Optional<TimeSlot> findByIdAndTeacherUserLogin(Long timeSlotId, String teacherLogin);

    @Query("select timeSlot from TimeSlot timeSlot where ((timeSlot.begin < timeSlot.end and timeSlot.begin < ?1 and timeSlot.end > ?2 and timeSlot.day like ?3) " +
        "or (timeSlot.begin > timeSlot.end and timeSlot.begin < ?1 and timeSlot.day like ?3) " +
        "or (timeSlot.begin > timeSlot.end and timeSlot.end > ?2 and timeSlot.day like ?4)) and timeSlot.teacher.id = ?5")
    List<TimeSlot> findAllTimeSlotsWithoutDayChange(Integer end, Integer begin, DayOfWeek day1, DayOfWeek day2, Long teacherId);

    @Query("select timeSlot from TimeSlot timeSlot where ((timeSlot.begin < 1440 and timeSlot.day like ?3) or (timeSlot.begin < ?1 and timeSlot.day like ?4)) " +
        "and ((timeSlot.end > ?2 and timeSlot.day like ?3) or (timeSlot.begin > timeSlot.end) or timeSlot.day like ?4) and timeSlot.teacher.id = ?5")
    List<TimeSlot> findAllTimeSlotsWithDayChange(Integer end, Integer begin, DayOfWeek day1, DayOfWeek day2, Long teacherId);
}
