package com.example.milestone2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvPropertyAssessmentDAO implements PropertyAssessmentDAO{

    private final List<PropertyAssessment> propertyAssessments;

    /**
     * Default constructor for the CSV Property Assessment DAO Class
     */
    public CsvPropertyAssessmentDAO() {
        List<String[]> propertyAssessmentData;
        try {
            String defaultCsvFile = "Property_Assessment_Data_2022.csv";
            propertyAssessmentData = this.readInCsv(defaultCsvFile);
        } catch (IOException error) {
            propertyAssessments = new ArrayList<>();
            return;
        }

        this.propertyAssessments = this.createPropertyAssessments(propertyAssessmentData);
    }

    /**
     * Constructor for the Property Assessments Class
     *
     * @param csvFile path to a csv file
     */
    public CsvPropertyAssessmentDAO(String csvFile) {
        List<String[]> propertyAssessmentData;
        try {
            propertyAssessmentData = this.readInCsv(csvFile);
        } catch (IOException error) {
            propertyAssessments = new ArrayList<>();
            return;
        }

        this.propertyAssessments = this.createPropertyAssessments(propertyAssessmentData);
    }

    /**
     * Private method for processing the data is a specified CSV file
     *
     * @param csvFile path to the csv file to be processed
     * @return a list of string arrays the hold the property assessment data
     * @throws IOException throws an IO Exception if file can't be found
     */
    private List<String[]> readInCsv(String csvFile) throws IOException {
        List<String[]> propertyAssessmentData;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvFile))) {
            reader.readLine();

            propertyAssessmentData = new ArrayList<>();

            String line;

            while ((line = reader.readLine()) != null) {
                String[] propertyAssessment = line.split(",");

                propertyAssessmentData.add(propertyAssessment);
            }
        }

        return propertyAssessmentData;
    }

    /**
     * Creates a list of property assessments from the provided property assessment data
     *
     * @param propertyAssessmentData a list of string arrays that contain the property assessment data
     *                               in the following format:
     *          0          1         2              4         5            6               7          8          9           10         11           12                  13                  14                     15                   16                  17                  18
     * { Account Number, Suite, House Number, Street name, Garage, Neighbourhood ID, Neighbourhood, Ward, Assessed Value, Latitude, Longitude, Point Location, Assessment Class % 1, Assessment Class % 2, Assessment Class % 3, Assessment Class 1, Assessment Class 2, Assessment Class 3 }
     *
     * @return a list Property Assessment objects
     */
    private List<PropertyAssessment> createPropertyAssessments(List<String[]> propertyAssessmentData) {
        List<PropertyAssessment> propertyAssessments = new ArrayList<>();

        for (String[] propertyAssessmentDatum : propertyAssessmentData) {
            propertyAssessments.add(new PropertyAssessment(propertyAssessmentDatum));
        }

        return propertyAssessments;
    }

    /**
     * Gets the property assessment with the specified account number
     *
     * @param accountNumber the account number of a property assessment
     * @return a property assessment object or null if no such account number exists
     */
    @Override
    public PropertyAssessment getByAccountNumber(int accountNumber) {

        for(PropertyAssessment propertyAssessment: propertyAssessments ) {
            if(propertyAssessment.getAccountNumber() == accountNumber) {
                return propertyAssessment;
            }
        }

        return null;
    }

    /**
     * Gets a list of property assessments from a specified neighbourhood
     *
     * @param neighbourhood the specified neighbourhood
     * @return a Property Assessments object with all the property assessments from the specified neighbourhood
     */
    @Override
    public List<PropertyAssessment> getByNeighbourhood (String neighbourhood) {
        List<PropertyAssessment> neighbourhoodPropertyAssessments = new ArrayList<>();

        for(PropertyAssessment propertyAssessment: propertyAssessments) {
            if(propertyAssessment.getNeighbourhood().toLowerCase().contains(neighbourhood.toLowerCase())) {
                neighbourhoodPropertyAssessments.add(propertyAssessment);
            }
        }

        return neighbourhoodPropertyAssessments;
    }

    /**
     * Gets a list of property assessments filtered by their address
     *
     * @param address The address to filter for
     * @return A list of property assessment filtered by their address
     */
    @Override
    public List<PropertyAssessment> getByAddress(String address) {
        List<PropertyAssessment> addressPropertyAssessments = new ArrayList<>();

        for (PropertyAssessment propertyAssessment: propertyAssessments) {
            Address propertyAddress = propertyAssessment.getAddress();

            StringBuilder addressString = new StringBuilder(propertyAddress.getSuite());
            addressString.append(propertyAddress.getHouseNumber());
            addressString.append(propertyAddress.getStreet());

            if (addressString.toString().toLowerCase().contains(address.toLowerCase())) {
                addressPropertyAssessments.add(propertyAssessment);
            }
        }

        return addressPropertyAssessments;
    }

    /**
     * Gets a list of property assessments with a specified assessment class
     *
     * @param assessmentClassName the specified assessment class
     * @return a Property Assessments object with all the property assessments with a specified assessment class
     */
    @Override
    public List<PropertyAssessment> getByAssessmentClass (String assessmentClassName) {

        return propertyAssessments.stream()
                .filter(propertyAssessment -> propertyAssessment.getAssessmentClassList()
                        .stream()
                        .map(AssessmentClass::getAssessmentClassName)
                        .anyMatch(assessmentClassName::equals))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of property assessments with assessed values equal to or greater than min
     *
     * @param min The minimum assessed value of property assessments to search for
     * @return A list of property assessments with assessed values greater than or equal to min
     */
    @Override
    public List<PropertyAssessment> getByAssessedValueMinimum(int min) {
        return propertyAssessments.stream()
                .filter(propertyAssessment -> propertyAssessment.getAssessedValue() >= min)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of property assessments with assessed values equal to or less than max
     *
     * @param max The maximum assessed value of property assessments to search for
     * @return A list of property assessments with assessed values less than or equal to max
     */
    @Override
    public List<PropertyAssessment> getByAssessedValueMaximum(int max) {
        return propertyAssessments.stream()
                .filter(propertyAssessment -> propertyAssessment.getAssessedValue() <= max)
                .collect(Collectors.toList());
    }

    /**
     * Returns a copy of the list of all property assessments
     *
     * @return List of property assessments
     */
    @Override
    public List<PropertyAssessment> getPropertyAssessments() {
        return new ArrayList<>(propertyAssessments);
    }

    /**
     * Return a filtered list of property assessments
     *
     * @param filter The filter to apply to the list of property assessments
     * @return a filtered list of property assessments
     */
    @Override
    public List<PropertyAssessment> getPropertyAssessments(Filter filter) {
        List<PropertyAssessment> addressList = null;

        // Only gets filtered lists for fields whose filter isn't the default filter value
        if(!filter.getAddress().isEmpty()){
            addressList = getByAddress(filter.getAddress());
        }

        List<PropertyAssessment> neighbourhoodList = null;
        if(!filter.getNeighbourhood().isEmpty()) {
            neighbourhoodList = getByNeighbourhood(filter.getNeighbourhood());
        }

        List<PropertyAssessment> assessmentClassList = null;
        if(!filter.getAssessmentClass().isEmpty()) {
            assessmentClassList = getByAssessmentClass(filter.getAssessmentClass());
        }

        List<PropertyAssessment> minimumAssessedValueList = null;
        if (filter.getMinimumAssessedValue() >= 0) {
            minimumAssessedValueList = getByAssessedValueMinimum(filter.getMinimumAssessedValue());
        }


        List<PropertyAssessment> maxmimumAssessedValueList = null;
        if(filter.getMaximumAssessedValue() >= 0) {
            maxmimumAssessedValueList = getByAssessedValueMaximum(filter.getMaximumAssessedValue());
        }

        // Create a list of non-null lists
        List<List<PropertyAssessment>> nonNullLists = Stream
                .of(addressList, neighbourhoodList, assessmentClassList, minimumAssessedValueList, maxmimumAssessedValueList)
                .filter(Objects::nonNull)
                .toList();

        List<PropertyAssessment> filteredList;

        if(nonNullLists.size() > 0){
            // Finds the intersection of the populated lists
            filteredList = nonNullLists.get(0);

            for(int i = 1; i < nonNullLists.size(); i++) {
                filteredList.retainAll(nonNullLists.get(i));
            }
        } else {
            // If no filter was applied then return the full list of property assessments
            filteredList = new ArrayList<>(propertyAssessments);
        }

        return filteredList;
    }

    /**
     * Gets a set of all the assessment class names in the list of property assessments
     *
     * @return Set of assessment class names
     */
    @Override
    public Set<String> getAssessmentClasses() {
        return propertyAssessments.stream()
                .map(PropertyAssessment::getAssessmentClassList)
                .flatMap(List::stream)
                .map(AssessmentClass::getAssessmentClassName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsvPropertyAssessmentDAO that = (CsvPropertyAssessmentDAO) o;
        return Objects.equals(propertyAssessments, that.propertyAssessments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyAssessments);
    }
}