package org.projekt17.fahrschuleasa.config.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SchoolConfigurationDeserializer extends StdDeserializer<SchoolConfiguration> {

    public SchoolConfigurationDeserializer() {
        this(null);
    }

    public SchoolConfigurationDeserializer(Class<SchoolConfiguration> t) {
        super(t);
    }

    @Override
    public SchoolConfiguration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        int initialLessons = node.get("initial_lessons").asInt();
        int maxInactive = node.get("max_inactive").asInt();
        int deadlineMissedLesson = node.get("deadline_missed_lesson").asInt();
        CronExpression cronExpression = new CronExpression(node.get("cron_expression").asText());
        Set<String> availableCategoriesStr = new HashSet<>();
        node.get("available_categories").elements().forEachRemaining(c -> availableCategoriesStr.add(c.asText()));
        Set<DrivingCategory> availableCategories = availableCategoriesStr.stream().map(DrivingCategory::valueOf).collect(Collectors.toSet());
        String emailSignature = node.get("email_signature").asText();

        SchoolConfiguration.setInitialLessons(initialLessons);
        SchoolConfiguration.setMaxInactive(maxInactive);
        SchoolConfiguration.setDeadlineMissedLesson(deadlineMissedLesson);
        SchoolConfiguration.setCronExpression(cronExpression);
        SchoolConfiguration.setAvailableCategories(availableCategories);
        SchoolConfiguration.setEmailSignature(emailSignature);

        return null;
    }
}
