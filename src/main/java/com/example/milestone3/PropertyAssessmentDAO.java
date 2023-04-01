package com.example.milestone3;

import java.util.List;
import java.util.Set;

public interface PropertyAssessmentDAO {
    PropertyAssessment getByAccountNumber(int accountNumber);
    List<PropertyAssessment> getByNeighbourhood(String neighbourhood);
    List<PropertyAssessment> getByAddress(String address);
    List<PropertyAssessment> getByAssessmentClass(String assessmentClass);
    List<PropertyAssessment> getPropertyAssessments();
    List<PropertyAssessment> getPropertyAssessments(Filter filter);
    Set<String> getAssessmentClasses();
    List<PropertyAssessment> getByAssessedValueMinimum(int min);
    List<PropertyAssessment> getByAssessedValueMaximum(int max);
    double max(Filter filter);
    double min(Filter filter);
    double average(Filter filter);
    class HistoricalAssessmentsDAO{}
}
