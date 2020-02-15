package org.projekt17.fahrschuleasa.config.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import java.io.IOException;

public class SchoolConfigurationSerializer extends StdSerializer<SchoolConfiguration> {

    public SchoolConfigurationSerializer() {
        this(null);
    }

    public SchoolConfigurationSerializer(Class<SchoolConfiguration> t) {
        super(t);
    }

    @Override
    public void serialize(SchoolConfiguration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("initial_lessons", SchoolConfiguration.getInitialLessons());
        gen.writeNumberField("max_inactive", SchoolConfiguration.getMaxInactive());
        gen.writeNumberField("deadline_missed_lesson", SchoolConfiguration.getDeadlineMissedLesson());
        gen.writeStringField("cron_expression", SchoolConfiguration.getCronExpression().getCronExpression());
        gen.writeArrayFieldStart("available_categories");
        for (DrivingCategory drivingCategory : SchoolConfiguration.getAvailableCategories()) {
            gen.writeString(drivingCategory.name());
        }
        gen.writeEndArray();
        gen.writeStringField("email_signature", SchoolConfiguration.getEmailSignature());
        gen.writeEndObject();
    }
}
