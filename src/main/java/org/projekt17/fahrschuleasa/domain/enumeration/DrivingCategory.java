package org.projekt17.fahrschuleasa.domain.enumeration;

/**
 * The DrivingCategory enumeration.
 */
public enum DrivingCategory {
    C1, C1E, C, CE, D1, D1E, D, DE, T, A1, MOFA, B, AM, A2, A, B96, BE, L;

    public boolean isCompatibleWith(DrivingCategory other) {
        return this == other;
    }
}
