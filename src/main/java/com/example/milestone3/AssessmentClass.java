package com.example.milestone3;

import java.util.Objects;

public record AssessmentClass(String assessmentClassName, int assessmentClassPercentage) {

    /**
     * Constructor for AssessmentClass Class
     *
     * @param assessmentClassName       The name of the assessment class
     * @param assessmentClassPercentage the percentage of the assessment class
     */
    public AssessmentClass {
    }

    /**
     * Getter for the assessment class name
     *
     * @return the assessment class name
     */
    @Override
    public String assessmentClassName() {
        return assessmentClassName;
    }

    /**
     * Getter for the assessment class percentage
     *
     * @return The assessment class percentage
     */
    @Override
    public int assessmentClassPercentage() {
        return assessmentClassPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentClass that = (AssessmentClass) o;
        return assessmentClassPercentage == that.assessmentClassPercentage && Objects.equals(assessmentClassName, that.assessmentClassName);
    }

    @Override
    public String toString() {
        return "AssessmentClass{" +
                "assessmentClass='" + assessmentClassName + '\'' +
                ", assessmentClassPercentage=" + assessmentClassPercentage +
                '}';
    }
}
