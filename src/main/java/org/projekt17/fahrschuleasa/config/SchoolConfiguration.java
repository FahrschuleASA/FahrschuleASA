package org.projekt17.fahrschuleasa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.projekt17.fahrschuleasa.config.util.CronExpression;
import org.projekt17.fahrschuleasa.config.util.SchoolConfigurationDeserializer;
import org.projekt17.fahrschuleasa.config.util.SchoolConfigurationSerializer;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SchoolConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SchoolConfiguration.class);

    private static final String filename = "school.conf";

    private static int initialLessons = 2;

    private static CronExpression cronExpression = new CronExpression();

    private static int maxInactive = 21;

    private static int deadlineMissedLesson = 24;

    private static Set<DrivingCategory> availableCategories = new HashSet<>(Arrays.asList(DrivingCategory.values()));

    private static String emailSignature = "Deine Fahrschule des Vertrauens";

    public static int getInitialLessons() {
        return initialLessons;
    }

    public static void setInitialLessons(int initialLessons) {
        SchoolConfiguration.initialLessons = initialLessons;
    }

    public static CronExpression getCronExpression() {
        return cronExpression;
    }

    public static void setCronExpression(CronExpression cronExpression) {
        SchoolConfiguration.cronExpression = cronExpression;
    }

    public static int getMaxInactive() {
        return maxInactive;
    }

    public static void setMaxInactive(int maxInactive) {
        SchoolConfiguration.maxInactive = maxInactive;
    }

    public static int getDeadlineMissedLesson() {
        return deadlineMissedLesson;
    }

    public static void setDeadlineMissedLesson(int deadlineMissedLesson) {
        SchoolConfiguration.deadlineMissedLesson = deadlineMissedLesson;
    }

    public static Set<DrivingCategory> getAvailableCategories() {
        return availableCategories;
    }

    public static void setAvailableCategories(Set<DrivingCategory> availableCategories) {
        SchoolConfiguration.availableCategories = availableCategories;
    }

    public static String getEmailSignature() {
        return emailSignature;
    }

    public static void setEmailSignature(String emailSignature) {
        SchoolConfiguration.emailSignature = emailSignature;
    }

    public static void save() {
        File configFile = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(SchoolConfiguration.class, new SchoolConfigurationSerializer());
        objectMapper.registerModule(simpleModule);

        try {
            objectMapper.writeValue(configFile, new SchoolConfiguration());
        } catch (IOException e) {
            logger.error("Failure while saving school configuration file!");
            e.printStackTrace();
        }
        logger.debug(String.format("current school configuration settings: initialLessons: %d; cronExpression: %s; " +
                "maxInactive: %d; deadlineMissedLesson: %d; availableCategories: %s; emailSignature: %s", initialLessons,
            cronExpression.getCronExpression(), maxInactive, deadlineMissedLesson,
            availableCategories.stream().map(Enum::toString).collect(Collectors.joining(", ")), emailSignature));
    }

    public static void load() {
        File configFile = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(SchoolConfiguration.class, new SchoolConfigurationDeserializer());
        objectMapper.registerModule(simpleModule);

        try {
            if (configFile.exists() && !configFile.isDirectory()) {
                logger.debug("School configuration file found. Load...");
                try {
                    objectMapper.readValue(configFile, SchoolConfiguration.class);
                } catch (Exception e) {
                    logger.error("Failure while loading school configuration file! Fallback to previous configuration.");
                    e.printStackTrace();
                }
                logger.debug(String.format("current school configuration settings: initialLessons: %d; cronExpression: %s; " +
                        "maxInactive: %d; deadlineMissedLesson: %d; availableCategories: %s; emailSignature: %s", initialLessons,
                    cronExpression.getCronExpression(), maxInactive, deadlineMissedLesson,
                    availableCategories.stream().map(Enum::toString).collect(Collectors.joining(", ")), emailSignature));
            } else {
                logger.debug(String.format("No school configuration file found. Create new configuration file '%s' with default configuration.", filename));
                SchoolConfiguration.save();
            }
        } catch (SecurityException e) {
            logger.error("Failed to load school configuration!");
            e.printStackTrace();
        }
    }

    public static String debugString() {
        return "SchoolConfiguration{" +
            "initialLesson=" + initialLessons +
            ", cronExpression=" + cronExpression.getCronExpression() +
            ", maxInactive=" + maxInactive +
            ", deadlineMissedLesson=" + deadlineMissedLesson +
            ", availableCategories=" + availableCategories +
            ", emailSignature=" + emailSignature +
            '}';
    }
}
