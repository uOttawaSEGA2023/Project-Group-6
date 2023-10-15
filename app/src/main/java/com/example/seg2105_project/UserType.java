package com.example.seg2105_project;

public enum UserType {
    ADMINISTRATOR("admin"),
    DOCTOR("doctor"),
    PATIENT("patient");

    public final String type;

    UserType(String type) {
        this.type = type;
    }

    public static UserType fromString(String type) throws IllegalArgumentException {
        for (UserType userType : UserType.values()) {
            if (userType.type.equalsIgnoreCase(type)) {
                return userType;
            }
        }
        throw new IllegalArgumentException("No UserType for string: " + type);
    }
}