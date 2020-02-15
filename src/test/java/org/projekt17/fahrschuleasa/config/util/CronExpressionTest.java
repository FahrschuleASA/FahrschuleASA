package org.projekt17.fahrschuleasa.config.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CronExpressionTest {

    @Test
    public void testDefaultCronExpression() {
        CronExpression cronExpression = new CronExpression();
        assertEquals(22, cronExpression.getHour());
        assertEquals(0, cronExpression.getMinute());
        assertEquals(7, cronExpression.getWeekday());
        assertEquals("0 0 22 ? * 7", cronExpression.getCronExpression());
    }

    @Test
    public void testSetCronExpressionWithString() {
        CronExpression cronExpression = new CronExpression("0 37 17 * * 4");
        assertEquals(17, cronExpression.getHour());
        assertEquals(37, cronExpression.getMinute());
        assertEquals(4, cronExpression.getWeekday());
        assertEquals("0 37 17 ? * 4", cronExpression.getCronExpression());

        cronExpression.setCronExpression("0 0 22 ? * 0");
        checkIfDefault(cronExpression);
    }

    @Test
    public void testSetCronExpressionWithStringSunday() {
        CronExpression cronExpression = new CronExpression("0 00 4 * * 0");
        assertEquals(4, cronExpression.getHour());
        assertEquals(0, cronExpression.getMinute());
        assertEquals(7, cronExpression.getWeekday());
        assertEquals("0 0 4 ? * 7", cronExpression.getCronExpression());
    }

    @Test
    public void testSetCronExpressionWithInts() {
        CronExpression cronExpression = new CronExpression(4,17,37);
        assertEquals(17, cronExpression.getHour());
        assertEquals(37, cronExpression.getMinute());
        assertEquals(4, cronExpression.getWeekday());
        assertEquals("0 37 17 ? * 4", cronExpression.getCronExpression());
    }

    @Test
    public void testSetCronExpressionWithIntsSunday() {
        CronExpression cronExpression = new CronExpression(0,4,0);
        assertEquals(4, cronExpression.getHour());
        assertEquals(0, cronExpression.getMinute());
        assertEquals(7, cronExpression.getWeekday());
        assertEquals("0 0 4 ? * 7", cronExpression.getCronExpression());
    }

    @Test
    public void testSetCronExpressionWithInvalidStrings() {
        CronExpression cronExpression = new CronExpression("3 37 17 * * 4");
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression("0 37 17 1 * 4");
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression("0 37 17 * 3 4");
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression("a 37 17 * * 4");
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression("0 45 13 ? ? 6");
        checkIfDefault(cronExpression);
    }

    @Test
    public void testSetCronExpressionWithInvalidInts() {
        CronExpression cronExpression = new CronExpression(-1,17,37);
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression(8,17,37);
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression(0,-1,37);
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression(0,25,37);
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression(0,17,-1);
        checkIfDefault(cronExpression);

        cronExpression.setCronExpression(0,17,61);
        checkIfDefault(cronExpression);
    }

    private void checkIfDefault(CronExpression cronExpression) {
        assertEquals(22, cronExpression.getHour());
        assertEquals(0, cronExpression.getMinute());
        assertEquals(7, cronExpression.getWeekday());
        assertEquals("0 0 22 ? * 7", cronExpression.getCronExpression());
    }
}
