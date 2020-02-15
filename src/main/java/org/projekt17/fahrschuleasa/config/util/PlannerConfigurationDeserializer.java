package org.projekt17.fahrschuleasa.config.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.projekt17.fahrschuleasa.config.PlannerConfiguration;

import java.io.IOException;

public class PlannerConfigurationDeserializer extends StdDeserializer<PlannerConfiguration> {

    public PlannerConfigurationDeserializer() {
        this(null);
    }

    public PlannerConfigurationDeserializer(Class<PlannerConfiguration> t) {
        super(t);
    }

    @Override
    public PlannerConfiguration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        int maximalTimeOwner = node.get("maximal_time_owner").asInt();
        int maximalTimeTeacher = node.get("maximal_time_teacher").asInt();
        int minimalRestTime = node.get("minimal_rest_time").asInt();
        int consecutiveLessonsParameter = node.get("consecutive_lessons_parameter").asInt();
        double transformationUpperBound = node.get("transformation_upper_bound").asDouble();
        int weightDifferentWantedLessons = node.get("weight_different_wanted_lessons").asInt();
        int weightDifferentCategories = node.get("weight_different_categories").asInt();
        int weightLevel = node.get("weight_level").asInt();
        int weightVehicleChanges = node.get("weight_vehicle_changes").asInt();
        int weightMultipleLessons = node.get("weight_multiple_lessons").asInt();
        int weightDistance = node.get("weight_distance").asInt();
        int weightBlocks = node.get("weight_blocks").asInt();
//        int weightIdleTime = node.get("weight_idle_time").asInt();
        int weightOpenSlots = node.get("weight_open_slots").asInt();
        double diffWantedLessonsParameter = node.get("diff_wanted_lessons_parameter").asDouble();
        int weightBasic = node.get("weight_basic").asInt();
        int weightAdvanced = node.get("weight_advanced").asInt();
        int weightPerformance = node.get("weight_performance").asInt();
        int weightIndependence = node.get("weight_independence").asInt();
        int weightOverland = node.get("weight_overland").asInt();
        int weightAutobahn = node.get("weight_autobahn").asInt();
        int weightNight = node.get("weight_night").asInt();
        double diffParameterSoft = node.get("diff_parameter_soft").asDouble();
        double diffParameterMedium = node.get("diff_parameter_medium").asDouble();
        double diffParameterHard = node.get("diff_parameter_hard").asDouble();

        PlannerConfiguration.setMaximalTimeOwner(maximalTimeOwner);
        PlannerConfiguration.setMaximalTimeTeacher(maximalTimeTeacher);
        PlannerConfiguration.setMinimalRestTime(minimalRestTime);
        PlannerConfiguration.setConsecutiveLessonsParameter(consecutiveLessonsParameter);
        PlannerConfiguration.setTransformationUpperBound(transformationUpperBound);
        PlannerConfiguration.setWeightDifferentWantedLessons(weightDifferentWantedLessons);
        PlannerConfiguration.setWeightDifferentCategories(weightDifferentCategories);
        PlannerConfiguration.setWeightLevel(weightLevel);
        PlannerConfiguration.setWeightVehicleChanges(weightVehicleChanges);
        PlannerConfiguration.setWeightMultipleLessons(weightMultipleLessons);
        PlannerConfiguration.setWeightDistance(weightDistance);
        PlannerConfiguration.setWeightBlocks(weightBlocks);
//        PlannerConfiguration.setWeightIdleTime(weightIdleTime);
        PlannerConfiguration.setWeightOpenSlots(weightOpenSlots);
        PlannerConfiguration.setDiffWantedLessonsParameter(diffWantedLessonsParameter);
        PlannerConfiguration.setWeightBasic(weightBasic);
        PlannerConfiguration.setWeightAdvanced(weightAdvanced);
        PlannerConfiguration.setWeightPerformance(weightPerformance);
        PlannerConfiguration.setWeightIndependence(weightIndependence);
        PlannerConfiguration.setWeightOverland(weightOverland);
        PlannerConfiguration.setWeightAutobahn(weightAutobahn);
        PlannerConfiguration.setWeightNight(weightNight);
        PlannerConfiguration.setDiffParameterSoft(diffParameterSoft);
        PlannerConfiguration.setDiffParameterMedium(diffParameterMedium);
        PlannerConfiguration.setDiffParameterHard(diffParameterHard);

        return null;
    }
}
