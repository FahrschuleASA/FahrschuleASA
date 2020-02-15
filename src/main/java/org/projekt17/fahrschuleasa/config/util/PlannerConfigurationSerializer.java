package org.projekt17.fahrschuleasa.config.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.projekt17.fahrschuleasa.config.PlannerConfiguration;

import java.io.IOException;

public class PlannerConfigurationSerializer extends StdSerializer<PlannerConfiguration> {

    public PlannerConfigurationSerializer() {
        this(null);
    }

    public PlannerConfigurationSerializer(Class<PlannerConfiguration> t) {
        super(t);
    }

    @Override
    public void serialize(PlannerConfiguration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("maximal_time_owner", PlannerConfiguration.getMaximalTimeOwner());
        gen.writeNumberField("maximal_time_teacher", PlannerConfiguration.getMaximalTimeTeacher());
        gen.writeNumberField("minimal_rest_time", PlannerConfiguration.getMinimalRestTime());
        gen.writeNumberField("consecutive_lessons_parameter", PlannerConfiguration.getConsecutiveLessonsParameter());
        gen.writeNumberField("transformation_upper_bound", PlannerConfiguration.getTransformationUpperBound());
        gen.writeNumberField("weight_different_wanted_lessons", PlannerConfiguration.getWeightDifferentWantedLessons());
        gen.writeNumberField("weight_different_categories", PlannerConfiguration.getWeightDifferentCategories());
        gen.writeNumberField("weight_level", PlannerConfiguration.getWeightLevel());
        gen.writeNumberField("weight_vehicle_changes", PlannerConfiguration.getWeightVehicleChanges());
        gen.writeNumberField("weight_multiple_lessons", PlannerConfiguration.getWeightMultipleLessons());
        gen.writeNumberField("weight_distance", PlannerConfiguration.getWeightDistance());
        gen.writeNumberField("weight_blocks", PlannerConfiguration.getWeightBlocks());
//        gen.writeNumberField("weight_idle_time", PlannerConfiguration.getWeightIdleTime());
        gen.writeNumberField("weight_open_slots", PlannerConfiguration.getWeightOpenSlots());
        gen.writeNumberField("diff_wanted_lessons_parameter", PlannerConfiguration.getDiffWantedLessonsParameter());
        gen.writeNumberField("weight_basic", PlannerConfiguration.getWeightBasic());
        gen.writeNumberField("weight_advanced", PlannerConfiguration.getWeightAdvanced());
        gen.writeNumberField("weight_performance", PlannerConfiguration.getWeightPerformance());
        gen.writeNumberField("weight_independence", PlannerConfiguration.getWeightIndependence());
        gen.writeNumberField("weight_overland", PlannerConfiguration.getWeightOverland());
        gen.writeNumberField("weight_autobahn", PlannerConfiguration.getWeightAutobahn());
        gen.writeNumberField("weight_night", PlannerConfiguration.getWeightNight());
        gen.writeNumberField("diff_parameter_soft", PlannerConfiguration.getDiffParameterSoft());
        gen.writeNumberField("diff_parameter_medium", PlannerConfiguration.getDiffParameterMedium());
        gen.writeNumberField("diff_parameter_hard", PlannerConfiguration.getDiffParameterHard());
        gen.writeEndObject();
    }
}
