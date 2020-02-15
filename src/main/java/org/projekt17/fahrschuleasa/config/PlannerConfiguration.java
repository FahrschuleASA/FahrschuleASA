package org.projekt17.fahrschuleasa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.projekt17.fahrschuleasa.config.util.PlannerConfigurationDeserializer;
import org.projekt17.fahrschuleasa.config.util.PlannerConfigurationSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class PlannerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(PlannerConfiguration.class);

    private static final String filename = "planner.conf";

    private static int maximalTimeOwner = 11*45;

    private static int maximalTimeTeacher = 10*45;

    private static int minimalRestTime = 11*60;

    private static int consecutiveLessonsParameter = 15;

    private static double transformationUpperBound = 100;

    private static int weightDifferentWantedLessons = 10;

    private static int weightDifferentCategories = 5;

    private static int weightLevel = 1;

    private static int weightVehicleChanges = 5;

    private static int weightMultipleLessons = 4;

    private static int weightDistance = 3;

    private static int weightBlocks = 2;

    // private static int weightIdleTime = 2;

    private static int weightOpenSlots = 1;

    private static double diffWantedLessonsParameter = 2.0;

    private static int weightBasic = 1;

    private static int weightAdvanced = 1;

    private static int weightPerformance = 1;

    private static int weightIndependence = 1;

    private static int weightOverland = 1;

    private static int weightAutobahn = 1;

    private static int weightNight = 1;

    private static double diffParameterSoft = 5.0;

    private static double diffParameterMedium = 10.0;

    private static double diffParameterHard = 20.0;

    public static int getMaximalTimeOwner() {
        return maximalTimeOwner;
    }

    public static void setMaximalTimeOwner(int maximalTimeOwner) {
        PlannerConfiguration.maximalTimeOwner = maximalTimeOwner;
    }

    public static int getMaximalTimeTeacher() {
        return maximalTimeTeacher;
    }

    public static void setMaximalTimeTeacher(int maximalTimeTeacher) {
        PlannerConfiguration.maximalTimeTeacher = maximalTimeTeacher;
    }

    public static int getMinimalRestTime() {
        return minimalRestTime;
    }

    public static void setMinimalRestTime(int minimalRestTime) {
        PlannerConfiguration.minimalRestTime = minimalRestTime;
    }

    public static int getConsecutiveLessonsParameter() {
        return consecutiveLessonsParameter;
    }

    public static void setConsecutiveLessonsParameter(int consecutiveLessonsParameter) {
        PlannerConfiguration.consecutiveLessonsParameter = consecutiveLessonsParameter;
    }

    public static double getTransformationUpperBound() {
        return transformationUpperBound;
    }

    public static void setTransformationUpperBound(double transformationUpperBound) {
        PlannerConfiguration.transformationUpperBound = transformationUpperBound;
    }

    public static int getWeightDifferentWantedLessons() {
        return weightDifferentWantedLessons;
    }

    public static void setWeightDifferentWantedLessons(int weightDifferentWantedLessons) {
        PlannerConfiguration.weightDifferentWantedLessons = weightDifferentWantedLessons;
    }

    public static int getWeightDifferentCategories() {
        return weightDifferentCategories;
    }

    public static void setWeightDifferentCategories(int weightDifferentCategories) {
        PlannerConfiguration.weightDifferentCategories = weightDifferentCategories;
    }

    public static int getWeightLevel() {
        return weightLevel;
    }

    public static void setWeightLevel(int weightLevel) {
        PlannerConfiguration.weightLevel = weightLevel;
    }

    public static int getWeightVehicleChanges() {
        return weightVehicleChanges;
    }

    public static void setWeightVehicleChanges(int weightVehicleChanges) {
        PlannerConfiguration.weightVehicleChanges = weightVehicleChanges;
    }

    public static int getWeightMultipleLessons() {
        return weightMultipleLessons;
    }

    public static void setWeightMultipleLessons(int weightMultipleLessons) {
        PlannerConfiguration.weightMultipleLessons = weightMultipleLessons;
    }

    public static int getWeightDistance() {
        return weightDistance;
    }

    public static void setWeightDistance(int weightDistance) {
        PlannerConfiguration.weightDistance = weightDistance;
    }

    public static int getWeightBlocks() {
        return weightBlocks;
    }

    public static void setWeightBlocks(int weightBlocks) {
        PlannerConfiguration.weightBlocks = weightBlocks;
    }

    //public static int getWeightIdleTime() {
    //    return weightIdleTime;
    //}
//
    //public static void setWeightIdleTime(int weightIdleTime) {
    //    PlannerConfiguration.weightIdleTime = weightIdleTime;
    //}

    public static int getWeightOpenSlots() {
        return weightOpenSlots;
    }

    public static void setWeightOpenSlots(int weightOpenSlots) {
        PlannerConfiguration.weightOpenSlots = weightOpenSlots;
    }

    public static double getDiffWantedLessonsParameter() {
        return diffWantedLessonsParameter;
    }

    public static void setDiffWantedLessonsParameter(double diffWantedLessonsParameter) {
        PlannerConfiguration.diffWantedLessonsParameter = diffWantedLessonsParameter;
    }

    public static int getWeightBasic() {
        return weightBasic;
    }

    public static void setWeightBasic(int weightBasic) {
        PlannerConfiguration.weightBasic = weightBasic;
    }

    public static int getWeightAdvanced() {
        return weightAdvanced;
    }

    public static void setWeightAdvanced(int weightAdvanced) {
        PlannerConfiguration.weightAdvanced = weightAdvanced;
    }

    public static int getWeightPerformance() {
        return weightPerformance;
    }

    public static void setWeightPerformance(int weightPerformance) {
        PlannerConfiguration.weightPerformance = weightPerformance;
    }

    public static int getWeightIndependence() {
        return weightIndependence;
    }

    public static void setWeightIndependence(int weightIndependence) {
        PlannerConfiguration.weightIndependence = weightIndependence;
    }

    public static int getWeightOverland() {
        return weightOverland;
    }

    public static void setWeightOverland(int weightOverland) {
        PlannerConfiguration.weightOverland = weightOverland;
    }

    public static int getWeightAutobahn() {
        return weightAutobahn;
    }

    public static void setWeightAutobahn(int weightAutobahn) {
        PlannerConfiguration.weightAutobahn = weightAutobahn;
    }

    public static int getWeightNight() {
        return weightNight;
    }

    public static void setWeightNight(int weightNight) {
        PlannerConfiguration.weightNight = weightNight;
    }

    public static double getDiffParameterSoft() {
        return diffParameterSoft;
    }

    public static void setDiffParameterSoft(double diffParameterSoft) {
        PlannerConfiguration.diffParameterSoft = diffParameterSoft;
    }

    public static double getDiffParameterMedium() {
        return diffParameterMedium;
    }

    public static void setDiffParameterMedium(double diffParameterMedium) {
        PlannerConfiguration.diffParameterMedium = diffParameterMedium;
    }

    public static double getDiffParameterHard() {
        return diffParameterHard;
    }

    public static void setDiffParameterHard(double diffParameterHard) {
        PlannerConfiguration.diffParameterHard = diffParameterHard;
    }

    private static void logCurrentConfiguration() {
        logger.debug(String.format("current planner configuration settings: maximalTimeOwner %d; maximalTimeTeacher %d;" +
                " minimalRestTime %d; consecutiveLessonsParameter %d;" +
                " transformationUpperBound %f; weightDifferentWantedLessons %d; weightDifferentCategories %d;" +
                " weightLevel %d; weightVehicleChanges %d; weightMultipleLessons %d; weightDistance %d;" +
                /* weightIdleTime %d;*/ "weightOpenSlots %d; diffWantedLessonsParameter %f; weightBasic %d; weightAdvanced %d;" +
                " weightPerformance %d; weightIndependence %d; weightOverland %d; weightAutobahn %d; weightNight %d;" +
                " diffParameterSoft %f; diffParameterMedium %f; diffParameterHard %f",
            maximalTimeOwner, maximalTimeTeacher, minimalRestTime,
            consecutiveLessonsParameter, transformationUpperBound, weightDifferentWantedLessons,
            weightDifferentCategories, weightLevel, weightVehicleChanges, weightMultipleLessons,
            weightDistance, /*weightIdleTime,*/ weightOpenSlots, diffWantedLessonsParameter, weightBasic,
            weightAdvanced, weightPerformance, weightIndependence, weightOverland, weightAutobahn, weightNight,
            diffParameterSoft, diffParameterMedium, diffParameterHard));
    }

    public static void save() {
        File configFile = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(PlannerConfiguration.class, new PlannerConfigurationSerializer());
        objectMapper.registerModule(simpleModule);

        try {
            objectMapper.writeValue(configFile, new PlannerConfiguration());
        } catch (IOException e) {
            logger.error("Failure while saving planner configuration file!");
            e.printStackTrace();
        }
        logCurrentConfiguration();
    }

    public static void load() {
        File configFile = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(PlannerConfiguration.class, new PlannerConfigurationDeserializer());
        objectMapper.registerModule(simpleModule);

        try {
            if (configFile.exists() && !configFile.isDirectory()) {
                logger.debug("Planner configuration file found. Load...");
                try {
                    objectMapper.readValue(configFile, PlannerConfiguration.class);
                } catch (Exception e) {
                    logger.error("Failure while loading planner configuration file! Fallback to previous configuration.");
                    e.printStackTrace();
                }
                logCurrentConfiguration();
            } else {
                logger.debug(String.format("No planner configuration file found. Create new configuration file '%s' with default configuration.", filename));
                PlannerConfiguration.save();
            }
        } catch (SecurityException e) {
            logger.error("Failed to load planner configuration!");
            e.printStackTrace();
        }
    }
}
