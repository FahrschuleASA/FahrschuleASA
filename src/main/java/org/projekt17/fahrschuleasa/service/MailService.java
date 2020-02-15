package org.projekt17.fahrschuleasa.service;

import io.github.jhipster.config.JHipsterProperties;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.DrivingLesson;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailService(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender,
            MessageSource messageSource, SpringTemplateEngine templateEngine) {

        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        }  catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        Context context = new Context();
        sendEmailFromTemplate(user, templateName, titleKey, context);
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey, Context context) {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        context.setLocale(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        context.setVariable("signature", SchoolConfiguration.getEmailSignature());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
    }

    @Async
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
    }

    @Async
    public void sendCancelDrivingLessonTeacherMail(Teacher teacher, Student student, DrivingLesson drivingLesson,
                                                   boolean self, boolean bookable) {
        log.debug("Sending cancel driving lesson email to teacher '{}'", teacher.getUser().getEmail());
        Context context = new Context();
        context.setVariable("studentName", String.format("%s %s", student.getUser().getFirstName(), student.getUser().getLastName()));
        context.setVariable("date", drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        context.setVariable("beginTime", drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")));
        context.setVariable("endTime", drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")));
        if (self) {
            if (bookable)
                sendEmailFromTemplate(teacher.getUser(), "mail/cancelEmailTeacherTeacherBookable", "email.cancelTeacherTeacher.title", context);
            else
                sendEmailFromTemplate(teacher.getUser(), "mail/cancelEmailTeacherTeacher", "email.cancelTeacherTeacher.title", context);
        } else
            sendEmailFromTemplate(teacher.getUser(), "mail/cancelEmailTeacher", "email.cancelTeacher.title", context);
    }

    @Async
    public void sendCancelDrivingLessonStudentMail(Student student, DrivingLesson drivingLesson, boolean self, boolean optional) {
        log.debug("Sending cancel driving lesson email to student '{}'", student.getUser().getEmail());
        Context context = new Context();
        context.setVariable("date", drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        context.setVariable("beginTime", drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")));
        context.setVariable("endTime", drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")));
        if (optional)
            sendEmailFromTemplate(student.getUser(), "mail/cancelEmailOptionalStudent", "email.cancelOptionalStudent.title", context);
        else {
            if (self)
                sendEmailFromTemplate(student.getUser(), "mail/cancelEmailStudentStudent", "email.cancelStudentStudent.title", context);
            else
                sendEmailFromTemplate(student.getUser(), "mail/cancelEmailStudentTeacher", "email.cancelStudentTeacher.title", context);
        }
    }

    @Async
    public void sendBookDrivingLessonTeacherMail(Teacher teacher, Student student, DrivingLesson drivingLesson, boolean self) {
        log.debug("Sending book driving lesson email to teacher '{}'", teacher.getUser().getEmail());
        Context context = new Context();
        context.setVariable("studentName", String.format("%s %s", student.getUser().getFirstName(), student.getUser().getLastName()));
        context.setVariable("date", drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        context.setVariable("endTime", drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")));
        context.setVariable("beginTime", drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")));
        if (self)
            sendEmailFromTemplate(teacher.getUser(), "mail/bookEmailTeacherTeacher", "email.bookTeacherTeacher.title", context);
        else
            sendEmailFromTemplate(teacher.getUser(), "mail/bookEmailTeacherStudent", "email.bookTeacherStudent.title", context);
    }

    @Async
    public void sendBookDrivingLessonStudentMail(Student student, DrivingLesson drivingLesson, boolean self) {
        log.debug("Sending book driving lesson email to student '{}'", student.getUser().getEmail());
        Context context = new Context();
        context.setVariable("date", drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        context.setVariable("endTime", drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")));
        context.setVariable("beginTime", drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")));
        if (self)
            sendEmailFromTemplate(student.getUser(), "mail/bookEmailStudentStudent", "email.bookStudentStudent.title", context);
        else
            sendEmailFromTemplate(student.getUser(), "mail/bookEmailStudentTeacher", "email.bookStudentTeacher.title", context);
    }

    @Async
    public void sendTimeSlotsChangedMail(Student student) {
        log.debug("Sending TimeSlots changed mail to student '{}'", student.getUser().getEmail());
        sendEmailFromTemplate(student.getUser(), "mail/timeSlotsChangedEmail", "email.timeSlotsChanged.title");
    }

    @Async
    public void sendCategoryChangedMail(Student student) {
        log.debug("Sending DrivingCategory changed mail to student '{}'", student.getUser().getEmail());
        sendEmailFromTemplate(student.getUser(), "mail/categoryChangedEmail", "email.categoryChanged.title");
    }

    @Async
    public void sendEmailActivationEmail(User user) {
        log.debug("Sending activation email for new email to user '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/emailActivationEmail", "email.emailActivation.title");
    }
}
