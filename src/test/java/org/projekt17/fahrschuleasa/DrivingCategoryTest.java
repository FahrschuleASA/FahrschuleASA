package org.projekt17.fahrschuleasa;

import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import static org.junit.jupiter.api.Assertions.*;

public class DrivingCategoryTest {

    @Test
    public void compareTest() {
        assertTrue(DrivingCategory.A.isCompatibleWith(DrivingCategory.A));
        assertFalse(DrivingCategory.B.isCompatibleWith(DrivingCategory.L));
    }
}
