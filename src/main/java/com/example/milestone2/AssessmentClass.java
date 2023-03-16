package com.example.milestone2;

import java.util.Objects;

public class AssessmentClass {

    private final String assessmentClassName;
    private final int assessmentClassPercentage;

    /**
     * Constructor for AssessmentClass Class
     *
     * @param assessmentClassName The name of the assessment class
     * @param assessmentClassPercentage the percentage of the assessment class
     */
    public AssessmentClass(String assessmentClassName, int assessmentClassPercentage){
        this.assessmentClassName = assessmentClassName;
        this.assessmentClassPercentage = assessmentClassPercentage;
    }

    /**
     * Getter for the assessment class name
     *
     * @return the assessment class name
     */
    public String getAssessmentClassName() {
        return assessmentClassName;
    }

    /**
     * Getter for the assessment class percentage
     *
     * @return The assessment class percentage
     */
    public int getAssessmentClassPercentage() {
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
    public int hashCode() {
        return Objects.hash(assessmentClassName, assessmentClassPercentage);
    }

    @Override
    public String toString() {
        return "AssessmentClass{" +
                "assessmentClass='" + assessmentClassName + '\'' +
                ", assessmentClassPercentage=" + assessmentClassPercentage +
                '}';
    }
}
