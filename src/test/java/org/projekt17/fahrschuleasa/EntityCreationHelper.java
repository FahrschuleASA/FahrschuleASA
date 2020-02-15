package org.projekt17.fahrschuleasa;

import org.apache.commons.lang3.RandomStringUtils;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EntityCreationHelper {

    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(value = "johndoe", roles = "TEACHER")
    public @interface WithMockTeacher {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(value = "johndoe", roles = "ADMIN")
    public @interface WithMockAdmin {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(value = "johndoe", roles = "STUDENT")
    public @interface WithMockStudent {
    }

    // data for user
    private final String DEFAULT_LOGIN = "johndoe";
    private final String ALTER_LOGIN = "usertest";
    private final String DEFAULT_EMAIL = "johndoe@localhost";
    private final String UPDATED_EMAIL = "updated@localhost";
    private final String DEFAULT_FIRSTNAME = "john";
    private final String UPDATED_FIRSTNAME = "johnUpdated";
    private final String DEFAULT_LASTNAME = "doe";
    private final String UPDATED_LASTNAME = "doeUpdated";
    private final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
    private final String DEFAULT_LANGKEY = "en";
    private Set<Authority> authorities = new HashSet<>();

    // data for address
    private final Double DEFAULT_LONGITUDE = 1D;
    private final Double DEFAULT_LATITUDE = 1D;
    private final String DEFAULT_TOWN = "AAAAAAAAAA";
    private final String DEFAULT_STREET = "AAAAAAAAAA";
    private final String UPDATED_STREET = "testtesttest";
    private final String DEFAULT_POSTAL = "AAAAAAAAAA";
    private final Integer DEFAULT_HOUSE_NUMBER = 1;
    private final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private final String DEFAULT_ADDITIONAL = "AAAAAAAAAA";

    private final Double ALTER_LONGITUDE = 2D;
    private final Double ALTER_LATITUDE = 2D;
    private final String ALTER_TOWN = "BBBBBBBBBB";
    private final String ALTER_STREET = "BBBBBBBBBB";
    private final String ALTER_POSTAL = "BBBBBBBBBB";
    private final Integer ALTER_HOUSE_NUMBER = 2;
    private final String ALTER_COUNTRY = "BBBBBBBBBB";
    private final String ALTER_ADDITIONAL = "BBBBBBBBBB";

    // data for a timeSlot
    private final Integer DEFAULT_BEGIN = 840;
    private final Integer DEFAULT_END = 960;
    private final DayOfWeek DEFAULT_DAY = DayOfWeek.MO;
    private final Set<DrivingCategory> DEFAULT_PREFERRED_CATEGORIES = new HashSet<>();
    private final Set<DrivingCategory> DEFAULT_OPTIONAL_CATEGORIES = new HashSet<>();
    private final Integer UPDATED_BEGIN = 900;
    private final Integer UPDATED_END = 1220;
    private final DayOfWeek UPDATED_DAY = DayOfWeek.TU;

    // data for MyAccount
    private final Boolean DEFAULT_ACTIVE = true;
    private final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);
    private final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.of(2001, 1, 1);
    private final String DEFAULT_PHONE_NUMBER = "1234567890";
    private final Boolean UPDATED_ACTIVE = false;

    // data for a student
    private final DrivingCategory DEFAULT_CATEGORY = DrivingCategory.BE;
    private final Boolean DEFAULT_READY_FOR_THEORY = false;
    private final Integer DEFAULT_WANTED_LESSONS = 4;
    private final Boolean DEFAULT_CHANGED_PREFERENCES = false;
    private final Integer DEFAULT_ALLOWED_LESSONS = 5;
    private final Boolean DEFAULT_MISSION_ACCOMPLISHED = false;
    private final DrivingCategory UPDATED_CATEGORY = DrivingCategory.C1E;
    private final boolean DEFAULT_NOTIFY_FOR_FREE_LESSON = true;
    private final Boolean UPDATED_READY_FOR_THEORY = true;
    private final Integer UPDATED_WANTED_LESSONS = 2000;
    private final Boolean UPDATED_CHANGED_PREFERENCES = true;
    private final boolean UPDATED_NOTIFY_FOR_FREE_LESSON = false;


    // data for Teacher
    private final Boolean DEFAULT_CHANGED_TIME_SLOTS = false;
    private final Boolean UPDATED_CHANGED_TIME_SLOTS = true;
    private final Boolean DEFAULT_SCHOOL_OWNER = false;

    // data for theoryLesson
    private final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private final String UPDATED_SUBJECT = "BBBBBBBBBB";
    private final LocalDateTime DEFAULT_THEORY_LESSON_BEGIN = LocalDateTime.now();
    private final LocalDateTime DEFAULT_THEORY_LESSON_END = LocalDateTime.now();

    // data for TeachingDiagram
    private final Integer DEFAULT_ADVANCED = 30;
    private final Integer DEFAULT_AUTOBAHN = 40;
    private final Integer DEFAULT_BASIC = 70;
    private final Integer DEFAULT_INDEPENDENCE = 50;
    private final Integer DEFAULT_NIGHT = 10;
    private final Integer DEFAULT_OVERLAND = 40;
    private final Integer DEFAULT_PERFORMANCE = 20;

    // data for DrivingLesson
    private final Boolean DEFAULT_BOOKABLE = true;
    private final DrivingLessonType DEFAULT_LESSON_TYPE = DrivingLessonType.NORMAL;
    private final DrivingLessonType ALTER_LESSON_TYPE = DrivingLessonType.NIGHT;
    private final LocalDateTime DEFAULT_DRIVING_LESSON_BEGIN = LocalDateTime.of(2000, 3, 6, 14, 0);
    private final LocalDateTime ALTER_DRIVING_LESSON_BEGIN = LocalDateTime.of(2000, 5, 6, 14, 0);
    private final LocalDateTime DEFAULT_DRIVING_LESSON_END = LocalDateTime.of(2000, 3, 6, 15, 30);
    private final LocalDateTime ALTER_DRIVING_LESSON_END = LocalDateTime.of(2000, 5, 6, 15, 30);

    private void addTeacherAuthority() {
        authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.TEACHER);
        authorities.add(authority);
    }

    private void addStudentAuthority() {
        authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.STUDENT);
        authorities.add(authority);
    }

    private void addAdminAuthority() {
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(authority);
    }

    private User createEntityUser(Set<Authority> authorities, boolean alter) {
        User user = new User();
        if (alter) {
            user.setLogin(ALTER_LOGIN);
        } else {
            user.setLogin(DEFAULT_LOGIN);
        }
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(DEFAULT_EMAIL);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);
        user.setAuthorities(authorities);
        return user;
    }

    public Location createEntityLocation() {
        return new Location()
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .town(DEFAULT_TOWN)
            .street(DEFAULT_STREET)
            .postal(DEFAULT_POSTAL)
            .houseNumber(DEFAULT_HOUSE_NUMBER)
            .country(DEFAULT_COUNTRY)
            .additional(DEFAULT_ADDITIONAL);
    }

    public Location createAlterEntityLocation() {
        return new Location()
            .longitude(ALTER_LONGITUDE)
            .latitude(ALTER_LATITUDE)
            .town(ALTER_TOWN)
            .street(ALTER_STREET)
            .postal(ALTER_POSTAL)
            .houseNumber(ALTER_HOUSE_NUMBER)
            .country(ALTER_COUNTRY)
            .additional(ALTER_ADDITIONAL);
    }

    public Teacher createEntityTeacher(boolean alter) {
        Teacher teacher = new Teacher()
            .changedTimeSlots(DEFAULT_CHANGED_TIME_SLOTS)
            .schoolOwner(DEFAULT_SCHOOL_OWNER);
        teacher.setBirthdate(DEFAULT_DATE_OF_BIRTH);
        this.addTeacherAuthority();
        teacher.setUser(createEntityUser(authorities, alter));
        if (alter)
            teacher.setAddress(createAlterEntityLocation());
        else
            teacher.setAddress(createEntityLocation());
        teacher.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        teacher.setActive(DEFAULT_ACTIVE);
        return teacher;
    }

    public Student createEntityStudent(boolean alter) {
        Student student = new Student()
            .category(DEFAULT_CATEGORY)
            .readyForTheory(DEFAULT_READY_FOR_THEORY)
            .wantedLessons(DEFAULT_WANTED_LESSONS)
            .changedPreferences(DEFAULT_CHANGED_PREFERENCES)
            .allowedLessons(DEFAULT_ALLOWED_LESSONS)
            .missionAccomplished(DEFAULT_MISSION_ACCOMPLISHED);
        student.setActive(DEFAULT_ACTIVE);
        student.setNotifyForFreeLesson(DEFAULT_NOTIFY_FOR_FREE_LESSON);
        this.addStudentAuthority();
        student.setUser(createEntityUser(authorities, alter));
        if (alter)
            student.setAddress(createAlterEntityLocation());
        else
            student.setAddress(createEntityLocation());
        student.setBirthdate(DEFAULT_DATE_OF_BIRTH);
        student.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        student.setActive(DEFAULT_ACTIVE);
        student.setTeachingDiagram(createEntityTeachingDiagram());
        return student;
    }

    public Student createEntityStudentWithLessons(boolean alter) {
        Student student = new Student()
            .category(DEFAULT_CATEGORY)
            .readyForTheory(DEFAULT_READY_FOR_THEORY)
            .wantedLessons(DEFAULT_WANTED_LESSONS)
            .changedPreferences(DEFAULT_CHANGED_PREFERENCES)
            .allowedLessons(DEFAULT_ALLOWED_LESSONS)
            .missionAccomplished(DEFAULT_MISSION_ACCOMPLISHED);
        this.addStudentAuthority();
        student.setUser(createEntityUser(authorities, alter));
        if (alter)
            student.setAddress(createAlterEntityLocation());
        else
            student.setAddress(createEntityLocation());
        student.setBirthdate(DEFAULT_DATE_OF_BIRTH);
        student.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        student.setActive(DEFAULT_ACTIVE);

        Set<DrivingLesson> drivingLessons = new HashSet<>();
        drivingLessons.add(new DrivingLesson().lessonType(DrivingLessonType.NORMAL));
        student.setDrivingLessons(drivingLessons);
        Set<DrivingLesson> missedLessons = new HashSet<>();
        missedLessons.add(new DrivingLesson());
        student.setMissedLessons(missedLessons);
        Set<DrivingLesson> lateMissedLesson = new HashSet<>();
        lateMissedLesson.add(new DrivingLesson());
        student.setLateMissedLessons(lateMissedLesson);
        Set<TheoryLesson> theoryLessons = new HashSet<>();
        theoryLessons.add(new TheoryLesson());
        student.setTheoryLessons(theoryLessons);

        student.setTeachingDiagram(createEntityTeachingDiagram());
        return student;
    }

    public Preference createEntityPreference(boolean alterStudent, boolean alterTeacher) {
        Preference preference = new Preference();
        preference.setPickup(createEntityLocation());
        preference.setDestination(createEntityLocation());
        preference.setStudent(createEntityStudent(alterStudent));
        preference.setTimeSlot(createEntityTimeSlot(alterTeacher));
        return preference;
    }

    public TimeSlot createEntityTimeSlot(boolean alter) {
        TimeSlot timeSlot = new TimeSlot()
            .begin(DEFAULT_BEGIN)
            .end(DEFAULT_END)
            .day(DEFAULT_DAY);
        DEFAULT_PREFERRED_CATEGORIES.add(DrivingCategory.BE);
        DEFAULT_OPTIONAL_CATEGORIES.add(DrivingCategory.MOFA);
        timeSlot.setPreferredCategories(DEFAULT_PREFERRED_CATEGORIES);
        timeSlot.setOptionalCategories(DEFAULT_OPTIONAL_CATEGORIES);
        timeSlot.setTeacher(createEntityTeacher(alter));
        return timeSlot;
    }

    public TheoryLesson createEntityTheoryLesson(boolean alter) {
        TheoryLesson theoryLesson = new TheoryLesson()
            .subject(DEFAULT_SUBJECT);
        theoryLesson.setBegin(DEFAULT_THEORY_LESSON_BEGIN);
        theoryLesson.setEnd(DEFAULT_THEORY_LESSON_END);
        theoryLesson.setTeacher(createEntityTeacher(alter));
        return theoryLesson;
    }

    public Administrator createEntityAdministrator(boolean alter) {
        Administrator administrator = new Administrator();
        administrator.setUser(createEntityUser(authorities, alter));
        addAdminAuthority();
        if (alter)
            administrator.setAddress(createAlterEntityLocation());
        else
            administrator.setAddress(createEntityLocation());
        administrator.setBirthdate(DEFAULT_DATE_OF_BIRTH);
        administrator.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        administrator.setActive(DEFAULT_ACTIVE);
        return administrator;
    }

    public TeachingDiagram createEntityTeachingDiagram() {
        TeachingDiagram teachingDiagram = new TeachingDiagram();
        teachingDiagram.setAdvanced(DEFAULT_ADVANCED);
        teachingDiagram.setAutobahn(DEFAULT_AUTOBAHN);
        teachingDiagram.setBasic(DEFAULT_BASIC);
        teachingDiagram.setIndependence(DEFAULT_INDEPENDENCE);
        teachingDiagram.setNight(DEFAULT_NIGHT);
        teachingDiagram.setOverland(DEFAULT_OVERLAND);
        teachingDiagram.setPerformance(DEFAULT_PERFORMANCE);
        return teachingDiagram;
    }

    public DrivingLesson createEntityDrivingLesson(boolean alter) {
        DrivingLesson drivingLesson = new DrivingLesson();
        drivingLesson.setBookable(DEFAULT_BOOKABLE);
        if (alter) {
            drivingLesson.setLessonType(ALTER_LESSON_TYPE);
            drivingLesson.setBegin(ALTER_DRIVING_LESSON_BEGIN);
            drivingLesson.setEnd(ALTER_DRIVING_LESSON_END);
        } else {
            drivingLesson.setLessonType(DEFAULT_LESSON_TYPE);
            drivingLesson.setBegin(DEFAULT_DRIVING_LESSON_BEGIN);
            drivingLesson.setEnd(DEFAULT_DRIVING_LESSON_END);
        }
        drivingLesson.setManualLesson(false);
        drivingLesson.setPickup(createEntityLocation());
        drivingLesson.setDestination(createAlterEntityLocation());
        return drivingLesson;
    }

    public String getDEFAULT_LOGIN() {
        return DEFAULT_LOGIN;
    }

    public String getDEFAULT_EMAIL() {
        return DEFAULT_EMAIL;
    }

    public String getDEFAULT_FIRSTNAME() {
        return DEFAULT_FIRSTNAME;
    }

    public String getDEFAULT_LASTNAME() {
        return DEFAULT_LASTNAME;
    }

    public String getDEFAULT_IMAGEURL() {
        return DEFAULT_IMAGEURL;
    }

    public String getDEFAULT_LANGKEY() {
        return DEFAULT_LANGKEY;
    }

    public Double getDEFAULT_LONGITUDE() {
        return DEFAULT_LONGITUDE;
    }

    public Double getDEFAULT_LATITUDE() {
        return DEFAULT_LATITUDE;
    }

    public String getDEFAULT_TOWN() {
        return DEFAULT_TOWN;
    }

    public String getDEFAULT_STREET() {
        return DEFAULT_STREET;
    }

    public String getDEFAULT_POSTAL() {
        return DEFAULT_POSTAL;
    }

    public Integer getDEFAULT_HOUSE_NUMBER() {
        return DEFAULT_HOUSE_NUMBER;
    }

    public String getDEFAULT_COUNTRY() {
        return DEFAULT_COUNTRY;
    }

    public String getDEFAULT_ADDITIONAL() {
        return DEFAULT_ADDITIONAL;
    }

    public Double getALTER_LONGITUDE() {
        return ALTER_LONGITUDE;
    }

    public Double getALTER_LATITUDE() {
        return ALTER_LATITUDE;
    }

    public String getALTER_TOWN() {
        return ALTER_TOWN;
    }

    public String getALTER_STREET() {
        return ALTER_STREET;
    }

    public String getALTER_POSTAL() {
        return ALTER_POSTAL;
    }

    public Integer getALTER_HOUSE_NUMBER() {
        return ALTER_HOUSE_NUMBER;
    }

    public String getALTER_COUNTRY() {
        return ALTER_COUNTRY;
    }

    public String getALTER_ADDITIONAL() {
        return ALTER_ADDITIONAL;
    }

    public Integer getDEFAULT_BEGIN() {
        return DEFAULT_BEGIN;
    }

    public Integer getDEFAULT_END() {
        return DEFAULT_END;
    }

    public DayOfWeek getDEFAULT_DAY() {
        return DEFAULT_DAY;
    }

    public Set<DrivingCategory> getDEFAULT_PREFERRED_CATEGORIES() {
        return DEFAULT_PREFERRED_CATEGORIES;
    }

    public Boolean getDEFAULT_ACTIVE() {
        return DEFAULT_ACTIVE;
    }

    public String getDEFAULT_PHONE_NUMBER() {
        return DEFAULT_PHONE_NUMBER;
    }

    public DrivingCategory getDEFAULT_CATEGORY() {
        return DEFAULT_CATEGORY;
    }

    public Boolean getDEFAULT_READY_FOR_THEORY() {
        return DEFAULT_READY_FOR_THEORY;
    }

    public Integer getDEFAULT_WANTED_LESSONS() {
        return DEFAULT_WANTED_LESSONS;
    }

    public Boolean getDEFAULT_CHANGED_PREFERENCES() {
        return DEFAULT_CHANGED_PREFERENCES;
    }

    public Boolean getDEFAULT_CHANGED_TIME_SLOTS() {
        return DEFAULT_CHANGED_TIME_SLOTS;
    }

    public String getALTER_LOGIN() {
        return ALTER_LOGIN;
    }

    public Integer getUPDATED_BEGIN() {
        return UPDATED_BEGIN;
    }

    public Integer getUPDATED_END() {
        return UPDATED_END;
    }

    public DayOfWeek getUPDATED_DAY() {
        return UPDATED_DAY;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public Set<DrivingCategory> getDEFAULT_OPTIONAL_CATEGORIES() {
        return DEFAULT_OPTIONAL_CATEGORIES;
    }

    public String getDEFAULT_SUBJECT() {
        return DEFAULT_SUBJECT;
    }

    public String getUPDATED_SUBJECT() {
        return UPDATED_SUBJECT;
    }

    public LocalDateTime getDEFAULT_THEORY_LESSON_BEGIN() {
        return DEFAULT_THEORY_LESSON_BEGIN;
    }

    public LocalDateTime getDEFAULT_THEORY_LESSON_END() {
        return DEFAULT_THEORY_LESSON_END;
    }

    public Boolean getUPDATED_CHANGED_TIME_SLOTS() {
        return UPDATED_CHANGED_TIME_SLOTS;
    }

    public String getUPDATED_EMAIL() {
        return UPDATED_EMAIL;
    }

    public String getUPDATED_FIRSTNAME() {
        return UPDATED_FIRSTNAME;
    }

    public String getUPDATED_LASTNAME() {
        return UPDATED_LASTNAME;
    }

    public Boolean getDEFAULT_SCHOOL_OWNER() {
        return DEFAULT_SCHOOL_OWNER;
    }

    public LocalDate getDEFAULT_DATE_OF_BIRTH() {
        return DEFAULT_DATE_OF_BIRTH;
    }

    public LocalDate getUPDATED_DATE_OF_BIRTH() {
        return UPDATED_DATE_OF_BIRTH;
    }

    public String getUPDATED_STREET() {
        return UPDATED_STREET;
    }

    public Integer getDEFAULT_ALLOWED_LESSONS() {
        return DEFAULT_ALLOWED_LESSONS;
    }

    public Integer getDEFAULT_ADVANCED() {
        return DEFAULT_ADVANCED;
    }

    public Integer getDEFAULT_AUTOBAHN() {
        return DEFAULT_AUTOBAHN;
    }

    public Integer getDEFAULT_BASIC() {
        return DEFAULT_BASIC;
    }

    public Integer getDEFAULT_INDEPENDENCE() {
        return DEFAULT_INDEPENDENCE;
    }

    public Integer getDEFAULT_NIGHT() {
        return DEFAULT_NIGHT;
    }

    public Integer getDEFAULT_OVERLAND() {
        return DEFAULT_OVERLAND;
    }

    public Integer getDEFAULT_PERFORMANCE() {
        return DEFAULT_PERFORMANCE;
    }

    public Boolean getDEFAULT_MISSION_ACCOMPLISHED() {
        return DEFAULT_MISSION_ACCOMPLISHED;
    }

    public Boolean getDEFAULT_BOOKABLE() {
        return DEFAULT_BOOKABLE;
    }

    public DrivingLessonType getDEFAULT_LESSON_TYPE() {
        return DEFAULT_LESSON_TYPE;
    }

    public LocalDateTime getDEFAULT_DRIVING_LESSON_BEGIN() {
        return DEFAULT_DRIVING_LESSON_BEGIN;
    }

    public LocalDateTime getDEFAULT_DRIVING_LESSON_END() {
        return DEFAULT_DRIVING_LESSON_END;
    }

    public DrivingLessonType getALTER_LESSON_TYPE() {
        return ALTER_LESSON_TYPE;
    }

    public LocalDateTime getALTER_DRIVING_LESSON_BEGIN() {
        return ALTER_DRIVING_LESSON_BEGIN;
    }

    public LocalDateTime getALTER_DRIVING_LESSON_END() {
        return ALTER_DRIVING_LESSON_END;
    }

    public DrivingCategory getUPDATED_CATEGORY() {
        return UPDATED_CATEGORY;
    }

    public Boolean getUPDATED_READY_FOR_THEORY() {
        return UPDATED_READY_FOR_THEORY;
    }

    public Integer getUPDATED_WANTED_LESSONS() {
        return UPDATED_WANTED_LESSONS;
    }

    public Boolean getUPDATED_CHANGED_PREFERENCES() {
        return UPDATED_CHANGED_PREFERENCES;
    }

    public Boolean getUPDATED_ACTIVE() {
        return UPDATED_ACTIVE;
    }

    public boolean getDEFAULT_NOTIFY_FOR_FREE_LESSON() {
        return DEFAULT_NOTIFY_FOR_FREE_LESSON;
    }

    public boolean getUPDATED_NOTIFY_FOR_FREE_LESSON() {
        return UPDATED_NOTIFY_FOR_FREE_LESSON;
    }
}

