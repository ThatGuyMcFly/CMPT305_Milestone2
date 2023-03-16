package com.example.milestone2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PropertyAssessment implements Comparable<PropertyAssessment>{
    
    private final int accountNumber;

    private final Address address;

    private final boolean garage;

    private final String neighbourhoodId;

    private final String neighbourhood;

    private final String ward;

    private final int assessedValue;

    private final Location location;

    private final List<AssessmentClass> assessmentClassList;

    /**
     * Constructor for the Property Assessment Class
     *
     * @param propertyData - Array of strings holding the property assessment's data in the following format:
     *          0          1         2              4         5            6               7          8          9           10         11           12                  13                  14                     15                   16                  17                  18
     * { Account Number, Suite, House Number, Street name, Garage, Neighbourhood ID, Neighbourhood, Ward, Assessed Value, Latitude, Longitude, Point Location, Assessment Class % 1, Assessment Class % 2, Assessment Class % 3, Assessment Class 1, Assessment Class 2, Assessment Class 3 }
     *
     */
    public PropertyAssessment(String[] propertyData){
        this.accountNumber = Integer.parseInt(propertyData[0]);
        this.address = new Address(propertyData[1], propertyData[2], propertyData[3]);
        this.garage = this.extractHasGarage(propertyData[4]);
        this.neighbourhoodId = propertyData[5];
        this.neighbourhood = propertyData[6];
        this.ward = propertyData[7];
        this.assessedValue = Integer.parseInt(propertyData[8]);
        this.location = new Location(propertyData[9], propertyData[10]);
        this.assessmentClassList = this.createAssessmentClasses(propertyData);
    }

    /**
     * Private function for extracting the assessment class data from the property data
     *
     * @param propertyData - Array of strings holding the property assessment's data in the following format:
     *          0          1         2              4         5            6               7          8          9           10         11           12                  13                  14                     15                   16                  17                  18
     * { Account Number, Suite, House Number, Street name, Garage, Neighbourhood ID, Neighbourhood, Ward, Assessed Value, Latitude, Longitude, Point Location, Assessment Class % 1, Assessment Class % 2, Assessment Class % 3, Assessment Class 1, Assessment Class 2, Assessment Class 3 }
     *
     */
    private List<AssessmentClass> createAssessmentClasses(String[] propertyData) {
        List<AssessmentClass> assessmentClassList = new ArrayList<>();

        for(int i = 12; i < 15; i++) {
            String value = propertyData[i];
            if(!value.equals("")) {
                int assessmentClassPercentage = Integer.parseInt(propertyData[i]);
                String assessmentClassName = propertyData[i + 3];

                assessmentClassList.add(new AssessmentClass(assessmentClassName, assessmentClassPercentage));
            } else {
                break;
            }
        }

        return assessmentClassList;
    }

    public List<AssessmentClass> getAssessmentClassList() { return assessmentClassList; }

    /**
     * Private function for determining if the property has a garage
     *
     * @param hasGarage - A string of either N or Y
     * @return boolean of whether the property has a garage
     */
    private boolean extractHasGarage(String hasGarage) {
        return !hasGarage.equalsIgnoreCase("n");
    }

    /**
     * Getter for the property assessment's account number
     *
     * @return the property's account number
     */
    public int getAccountNumber() {
        return accountNumber;
    }

    /**
     * Getter for the property's address
     *
     * @return the property's address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Getter for whether the property has a garage
     *
     * @return whether the property has a garage
     */
    public boolean hasGarage() {
        return garage;
    }

    /**
     * Getter for the property's neighbourhood ID
     *
     * @return the property's neighbourhood ID
     */
    public String getNeighbourhoodId() {
        return neighbourhoodId;
    }

    /**
     * Getter for the property's neighbourhood
     *
     * @return the property's neighbourhood
     */
    public String getNeighbourhood() {
        if (!ward.isEmpty()){
            return neighbourhood + " (" + ward + ")";
        } else {
            return neighbourhood;
        }
    }

    /**
     * Getter for the property's ward
     *
     * @return the property's ward
     */
    public String getWard() {
        return ward;
    }

    /**
     * Getter for the property's assessed value
     *
     * @return the property's assessed value
     */
    public int getAssessedValue() {
        return assessedValue;
    }

    /**
     * Getter for the property's location
     *
     * @return the property's location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Override the compare method to compare the assessed value of 2 property assessments
     *
     * @param otherPropertyAssessment the object to be compared.
     * @return the difference in the assessed value of two property assessments
     */
    @Override
    public int compareTo(PropertyAssessment otherPropertyAssessment) {
        return this.assessedValue - otherPropertyAssessment.getAssessedValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyAssessment that = (PropertyAssessment) o;
        return accountNumber == that.accountNumber && garage == that.garage && assessedValue == that.assessedValue && Objects.equals(address, that.address) && Objects.equals(neighbourhoodId, that.neighbourhoodId) && Objects.equals(neighbourhood, that.neighbourhood) && Objects.equals(ward, that.ward) && Objects.equals(location, that.location) && Objects.equals(assessmentClassList, that.assessmentClassList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, address, garage, neighbourhoodId, neighbourhood, ward, assessedValue, location, assessmentClassList);
    }

    @Override
    public String toString() {
        return "PropertyAssessment{" +
                "accountNumber=" + accountNumber +
                ", address=" + address +
                ", garage=" + garage +
                ", neighbourhoodId='" + neighbourhoodId + '\'' +
                ", neighbourhood='" + neighbourhood + '\'' +
                ", ward='" + ward + '\'' +
                ", assessedValue=" + assessedValue +
                ", location=" + location +
                ", assessmentClass=" + assessmentClassList +
                '}';
    }
}
