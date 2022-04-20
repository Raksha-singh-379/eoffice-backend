package com.techvg.eoffice.domain.enumeration;

/**
 * The DakStatus enumeration.
 */
public enum DakStatus {
    CREATED("Created"),
    UPDATED("Updated"),
    ASSIGNED("Assigned"),
    AWAITED("Awaited"),
    HEARING("Hearing"),
    HEARING_AWAITED("Hearing_Awaited"),
    HEARING_COMPLETED("Hearing_Completed"),
    PENDING("Pending"),
    AWAITED_FOR_ORDER("Awaited_For_Order"),
    CLEARED("Cleared");

    private final String value;

    DakStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
