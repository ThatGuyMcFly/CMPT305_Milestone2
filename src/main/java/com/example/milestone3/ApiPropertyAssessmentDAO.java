package com.example.milestone3;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.*;
import java.util.stream.IntStream;


public class ApiPropertyAssessmentDAO implements PropertyAssessmentDAO{

    private final String endPoint;
    private static final String appToken = "3smbloCggKmhwOlTxnPEchy3G";
    private static final String historicalApiEndpoint = "https://data.edmonton.ca/resource/qi6a-xuwt.csv";

    /**
     * Default constructor for the API Property Assessment DAO Class
     */
    public ApiPropertyAssessmentDAO(){
        this("https://data.edmonton.ca/resource/q7d6-ambg.csv");
    }

    /**
     * Constructor for the API Property Assessment DAO Class
     *
     * @param endPoint the end point from which to retrieve the property assessment data
     */
    public ApiPropertyAssessmentDAO(String endPoint){
        this.endPoint = endPoint;
    }

    /**
     * Gets the index of a string within in an array of strings
     *
     * @param stringArray The array of string being searched
     * @param str The string being searched for
     * @return The index of the string in the array, or -1 if the string wasn't found
     */
    private int getIndex(String[] stringArray, String str) {
        return IntStream.range(0, stringArray.length)
                .filter(i -> str.equals(stringArray[i]))
                .findFirst()
                .orElse(-1);
    }

    /**
     * Creates a formatted URL Query
     *
     * @param urlQuery the query to be formatted
     * @return a formatted URL Query
     */
    private String createUrl(String urlQuery) {
        String[] queryArray = urlQuery.split("&");
        StringBuilder url = new StringBuilder(endPoint);

        for (String subQuery: queryArray) {
            // avoids encoding '=' and '&' characters since that was causing issues when sending the queries
            int equalIndex = subQuery.indexOf('=');
            url.append(subQuery, 0, equalIndex + 1).append(URLEncoder.encode(subQuery.substring(equalIndex + 1), StandardCharsets.UTF_8));

            if (getIndex(queryArray, subQuery) != queryArray.length - 1){
                url.append("&");
            }
        }

        return url.toString();
    }

    /**
     * Sends a query and gets the data from the endpoint
     *
     * @param query the query to be sent to the endpoint
     * @return The data returned from the endpoint
     */
    private String getData(String query) {
        String url = createUrl(query);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e){
            return "";
        }
    }

    /**
     * Gets a property assessment with the specified account number
     *
     * @param accountNumber the account number of the property assessment
     * @return the property assessment with the specified account number or null if no property assessment was found
     */
    @Override
    public PropertyAssessment getByAccountNumber(int accountNumber) {
        String response = getData("?account_number=" + accountNumber);

        String[] propertyAssessmentStringArray = response.replaceAll("\"", "").split("\n");

        if (propertyAssessmentStringArray.length > 1){
            return new PropertyAssessment(propertyAssessmentStringArray[1].split(","));
        }

        return null;
    }

    /**
     * Processes the CSV data retrieved form the end point into a list of property assessments
     *
     * @param data The CSV data to be processed
     * @return A List of property assessments
     */
    private List<PropertyAssessment> processData(String data) {
        List<PropertyAssessment> propertyAssessmentList = new ArrayList<>();

        String[] propertyAssessmentStringArray = data.replaceAll("\"", "").split("\n");

        for (int i = 1; i < propertyAssessmentStringArray.length; i++) {
            propertyAssessmentList.add(new PropertyAssessment(propertyAssessmentStringArray[i].split(",")));
        }

        return propertyAssessmentList;
    }

    /**
     * Gets a list of property assessments with a specified neighbourhood
     *
     * @param neighbourhood The neighbourhood of the property assessments being searched for
     * @return A List of property assessments with the specified neighbourhood
     */
    @Override
    public List<PropertyAssessment> getByNeighbourhood(String neighbourhood) {
        String response = getData("?$where=" + createNeighbourhoodQuery(neighbourhood));

        return processData(response);
    }

    /**
     * Gets a list of property assessments with a specified address
     *
     * @param address The address if the property assessments being searched for
     * @return A list of property assessments with the specified address
     */
    @Override
    public List<PropertyAssessment> getByAddress(String address) {
        if(address.isEmpty()) {
            return null;
        }

        String response = getData("?where=" + createAddressQuery(address));

        return processData(response);
    }

    /**
     * Gets a list of property assessments with a specified assessment class
     *
     * @param assessmentClass The assessment class of the property assessments being search for
     * @return A list of property assessments with the specified assessment class
     */
    @Override
    public List<PropertyAssessment> getByAssessmentClass(String assessmentClass) {
        String response = getData("?$where=" + createAssessmentClassQuery(assessmentClass));

        return processData(response);
    }

    private String createDefaultQuery() {
        return "?$limit=1000&$offset=0&$order=account_number";
    }

    /**
     * Creates a query for an address
     *
     * @param address the address to be queried
     * @return a query for an address or an empty string if the address is empty
     */
    private String createAddressQuery(String address) {
        if(address.isEmpty()) {
            return "";
        }
        return "suite || ' ' || house_number || ' ' || street_name like " + "'%" + address + "%'";
    }

    /**
     * Creates a query for selecting property assessments with a specified neighbourhood
     *
     * @param neighbourhood The neighbourhood to be queried
     * @return a query for a neighbourhood or an empty string is neighbourhood is empty
     */
    private String createNeighbourhoodQuery(String neighbourhood) {
        if(neighbourhood.isEmpty()) {
            return "";
        }
        return "neighbourhood like " + "'%" + neighbourhood.toUpperCase() + "%'";
    }

    /**
     * Creates a query for selecting property assessments with a specified assessment class
     *
     * @param assessmentClass The assessment class to be queried
     * @return a query for an assessment class or an empty string is neighbourhood is empty
     */
    private static String createAssessmentClassQuery(String assessmentClass) {
        if (assessmentClass.isEmpty()) {
            return "";
        }
        return "(mill_class_1 = " + "'" + assessmentClass.toUpperCase() + "' OR mill_class_2 = '" + assessmentClass.toUpperCase() + "'" + " OR mill_class_3 = '" + assessmentClass.toUpperCase() + "')";
    }

    /**
     * Creates a query to for selecting properties with an assessed value between a min and max. If either of the values
     * is less than 0 then that value will be ignored
     *
     * @param min the minimum assessed value to be searched for
     * @param max the maximum assessed value to be searched for
     * @return A list of property assessments with assessed values between the min and max
     */
    private String createAssessedValueRangeQuery(int min, int max) {
        String minString = "";
        String maxString = "";

        if (min < 0 && max < 0) {
            return "";
        }

        if (min < 0) {
            maxString = " <= " + max;
        } else if (max < 0) {
            minString = " >= " + min;
        } else {
            minString = "between " + min;
            maxString = " and " + max;
        }

        return "assessed_value " + minString + maxString;
    }

    /**
     * Creates a query by combining all the appropriate queries as determined by the provided filter
     *
     * @param filter A filter object that contains the fields to be filtered for
     * @return An amalgamated query for all the fields specified in the provided filter
     */
    private String createFilterQueryString(Filter filter) {
        StringBuilder query = new StringBuilder();

        query.append(createAddressQuery(filter.getAddress().toUpperCase()));

        // adds an AND between queries only if there are previous queries added and the filter field isn't the
        // default value of the filter field
        if(!query.isEmpty() && !filter.getNeighbourhood().isEmpty()) {
            query.append(" AND ");
        }

        query.append(createNeighbourhoodQuery(filter.getNeighbourhood()));

        if(!query.isEmpty() && !filter.getAssessmentClass().isEmpty()) {
            query.append(" AND ");
        }

        query.append(createAssessmentClassQuery(filter.getAssessmentClass()));

        if(!query.isEmpty() && (filter.getMinimumAssessedValue() > -1 || filter.getMaximumAssessedValue() > -1)) {
            query.append(" AND ");
        }

        query.append(createAssessedValueRangeQuery(filter.getMinimumAssessedValue(), filter.getMaximumAssessedValue()));

        return query.toString();
    }

    /**
     * Gets a filtered list of property values as defined by the provided filter
     *
     * @param filter The filter that determines how the fields should be filtered
     * @return A filtered list of property assessments
     */
    @Override
    public List<PropertyAssessment> getPropertyAssessments(Filter filter) {
        String query = createDefaultQuery();

        String filterQuery = createFilterQueryString(filter);

        if(!filterQuery.isEmpty()) {
            query = "?$where=" + filterQuery;
        }

        String response = getData(query);

        return processData(response);
    }

    /**
     * Gets an unfiltered list of property assessments
     *
     * @return A List of property assessments
     */
    @Override
    public List<PropertyAssessment> getPropertyAssessments() {
        String response = getData(createDefaultQuery());

        return processData(response);
    }

    /**
     * Gets a Set of all the assessment classes in the property assessments
     *
     * @return A set of assessment classes
     */
    @Override
    public Set<String> getAssessmentClasses() {
        Set<String> assessmentClassSet = new HashSet<>();

        for (int i = 1; i < 4; i++) {
            String response = getData("?$select=distinct mill_class_" + i);

            String[] assessmentClassArray = response.replaceAll("\"", "").split("\n");

            assessmentClassSet.addAll(Arrays.asList(assessmentClassArray).subList(1, assessmentClassArray.length));
        }

        return assessmentClassSet;
    }

    /**
     * Gets a list of property assessments with assessed values equal to or greater than min
     *
     * @param min The minimum assessed value of property assessments to search for
     * @return A list of property assessments with assessed values greater than or equal to min
     */
    @Override
    public List<PropertyAssessment> getByAssessedValueMinimum(int min) {
        String response = getData(createAssessedValueRangeQuery(min, -1));

        return processData(response);
    }

    /**
     * Gets a list of property assessments with assessed values equal to or less than max
     *
     * @param max The maximum assessed value of property assessments to search for
     * @return A list of property assessments with assessed values less than or equal to max
     */
    @Override
    public List<PropertyAssessment> getByAssessedValueMaximum(int max) {
        String response = getData(createAssessedValueRangeQuery(0, max));

        return processData(response);
    }

    /**
     * Gets the highest assessment value of a set of filtered property assessments
     * @param filter The filter for how the property assessments are filtered
     * @return the largest assessed value of the set of filtered property assessments
     */
    @Override
    public double max(Filter filter) {
        return minMaxAverage("max", filter);
    }

    /**
     * Gets the highest assessment value of a set of filtered property assessments
     * @param filter The filter for how the property assessments are filtered
     * @return the largest assessed value of the set of filtered property assessments
     */
    @Override
    public double min(Filter filter) {
        return minMaxAverage("min", filter);
    }

    /**
     * Gets the highest assessment value of a set of filtered property assessments
     * @param filter The filter for how the property assessments are filtered
     * @return the largest assessed value of the set of filtered property assessments
     */
    @Override
    public double average(Filter filter) {
        return minMaxAverage("avg", filter);
    }

    /**
     * Gets the specified assessed values statistic of a set of filtered property assessments
     *
     * @param minMaxAverage the type of static to get
     *                      The assessed values statistics are as follows:
     *                          max: the largest assessed value
     *                          min: the smallest assessed value
     *                          avg: the average assessed value
     * @param filter the filter for how filter the property assessments
     * @return the specified assessed values statistic lot size of the filtered property assessments
     */
    private double minMaxAverage(String minMaxAverage, Filter filter) {
        StringBuilder query = new StringBuilder("?$select=").append(minMaxAverage).append("(assessed_value)");

        String filterQuery = createFilterQueryString(filter);

        if(!filterQuery.isEmpty()) {
            query.append(" where ").append(filterQuery);
        }

        String response = getData(query.toString());

        try{
            return Double.parseDouble(response.replaceAll("\"", "").split("\n")[1]);
        } catch (NumberFormatException error) {
            return -1;
        }
    }

    // Static class for getting historical property assessment data
    public static class HistoricalAssessmentsDAO {


        /**
         * Creates a query for selecting properties in a specific neighbourhood
         *
         * @param neighbourhood the neighbourhood to search for
         * @return A query string for filtering by neighbourhood, or an empty string if no neighbourhood is given
         */
        private static String createNeighbourhoodQuery(String neighbourhood) {
            return !neighbourhood.isEmpty() ? "neighbourhood_name like '" + neighbourhood.toUpperCase() + "%'" : "";
        }

        /**
         * Creates a query for selecting properties in a specific area of edmonton
         *
         * @param area the part of edmonton to filter by (north, south, east, west, or central)
         * @return A query string for filtering by area, or an empty string if no area is given
         */
        private static String createAreaQuery(String area) {
            return switch (area) {
                case "North" -> "latitude>53.5590";
                case "South" -> "latitude<53.5180";
                case "West" -> "longitude<-113.5405";
                case "East" -> "longitude>-113.4540";
                case "Central" ->
                        "(latitude<53.5590 and latitude>53.5180 and longitude>-113.5405 and longitude<-113.4540)";
                default -> "";
            };
        }

        /**
         * Creates a query for selecting properties built in a specific range of years
         *
         * @param min the minimum year to filter by
         * @param max the maximum year to filter by
         * @return A string for filtering by the year a property was built, or an empty string if no values are given
         */
        private static String createYearBuiltRangeQuery(int min, int max) {
            String minString = "";
            String maxString = "";

            if (min < 0 && max < 0) {
                return "";
            }

            if (min < 0) {
                maxString = " <= " + max;
            } else if (max < 0) {
                minString = " >= " + min;
            } else {
                minString = "between " + min;
                maxString = " and " + max;
            }

            return "year_built " + minString + maxString;
        }

        /**
         * Creates a query by combining all the appropriate queries as determined by the provided filter
         *
         * @param filter A filter object that contains the fields to be filtered for
         * @return An amalgamated query for all the fields specified in the provided filter
         */
        private static String createFilterQueryString(Filter filter) {
            StringBuilder query = new StringBuilder();

            String neighbourhoodQuery = createNeighbourhoodQuery(filter.getNeighbourhood());
            query.append(neighbourhoodQuery);

            // adds an AND between queries only if there are previous queries added and the filter field isn't the
            // default value of the filter field
            if(!query.isEmpty() && !filter.getArea().isEmpty()) {
                query.append(" and ");
            }
            String areaQuery = createAreaQuery(filter.getArea());
            query.append(areaQuery);

            if(!query.isEmpty() && !filter.getAssessmentClass().isEmpty()) {
                query.append(" and ");
            }
            String assessClassQuery = createAssessmentClassQuery(filter.getAssessmentClass());
            query.append(assessClassQuery);

            if(!query.isEmpty() && (filter.getMinimumYearBuilt() > -1 || filter.getMaximumYearBuilt() > -1)) {
                query.append(" and ");
            }
            String yearRange = createYearBuiltRangeQuery(filter.getMinimumYearBuilt(), filter.getMaximumYearBuilt());
            query.append(yearRange);

            return query.toString();
        }

        public static List<Integer> getMaxHistoricalValues(Filter filter) {
            return getHistoricalValues("max", filter);
        }

        public static List<Integer> getMinHistoricalValues(Filter filter) {
            return getHistoricalValues("min", filter);
        }

        public static List<Integer> getAverageHistoricalValues(Filter filter) {
            return getHistoricalValues("avg", filter);
        }

        /**
         * get a list of average property values for the past 10 years using a given filter
         *
         * @param filter A filter object that contains the fields to be filtered for
         * @return A list of integers, where List[i] is the average value of filtered properties 10 - i years ago
         */
        private static List<Integer> getHistoricalValues(String minMaxAverage, Filter filter) {
            String query = "?$$app_token=" + appToken + "&$select="+ minMaxAverage +"(assessed_value) where" +
                            createFilterQueryString(filter) + " and assessment_year=";

            int year = Year.now().getValue() - 11;
            List<Integer> values = new ArrayList<>();

            while (year < Year.now().getValue()) {
                String[] response = callEndpoint(query + year).replaceAll("\"", "").split("\n");
                values.add( response.length > 1 ? Math.round(Float.parseFloat(response[1])) : -1);
                year++;
            }
            return values;
        }

        /**
         * Gets the index of a string within in an array of strings
         *
         * @param stringArray The array of string being searched
         * @param str The string being searched for
         * @return The index of the string in the array, or -1 if the string wasn't found
         */
        private static int getIndex(String[] stringArray, String str) {
            return IntStream.range(0, stringArray.length)
                    .filter(i -> str.equals(stringArray[i]))
                    .findFirst()
                    .orElse(-1);
        }

        /**
         * Creates a formatted URL Query
         *
         * @param urlQuery the query to be formatted
         * @return a formatted URL Query
         */
        private static String createUrl(String urlQuery) {
            String[] queryArray = urlQuery.split("&");
            StringBuilder url = new StringBuilder(historicalApiEndpoint);

            for (String subQuery: queryArray) {
                // avoids encoding '=' and '&' characters since that was causing issues when sending the queries
                int equalIndex = subQuery.indexOf('=');
                url.append(subQuery, 0, equalIndex + 1).append(URLEncoder.encode(subQuery.substring(equalIndex + 1), StandardCharsets.UTF_8));

                if (getIndex(queryArray, subQuery) != queryArray.length - 1){
                    url.append("&");
                }
            }

            return url.toString();
        }

        /**
         * Makes queries to the historical data API
         * @param query The query to be made to the historical data API
         * @return the response from the API
         */
        private static String callEndpoint(String query) {
            String url = createUrl(query);

            System.out.println(url);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            try{
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (IOException | InterruptedException e){
                return "";
            }
        }

        /**
         * Gets a list of historical assessment values of a specified property assessment
         * @param account_number The account number of the specific property assessment
         * @return a list of historical assessment values where years without data are stored as -1
         */
        public static List<Integer> getHistoricalPropertyValuesByAccountNumber(int account_number) {
            String query = "?$$app_token=" + appToken + "&$select=assessed_value where account_number='" + account_number + "' and assessment_year='";

            int year = Year.now().getValue() - 11;
            List<Integer> values = new ArrayList<>();

            while (year < Year.now().getValue()) {
                String newQuery = query + year + "'";
                String[] response = callEndpoint(newQuery).replaceAll("\"", "").split("\n");
                values.add( response.length > 1 ? Math.round(Float.parseFloat(response[1])) : -1);
                year++;
            }

            return values;
        }

        /**
         * Gets the average lot size of filtered property assessments
         * @param filter the filter for how filter the property assessments
         * @return the average lot size of the filtered property assessments
         */
        public static int averageLotSize(Filter filter) { return lotSize("avg", filter); }

        /**
         * Gets the largest lot size of filtered property assessments
         * @param filter the filter for how filter the property assessments
         * @return the average lot size of the filtered property assessments
         */
        public static int maxLotSize(Filter filter) { return lotSize("max", filter); }

        /**
         * Gets the smallest lot size of filtered property assessments
         * @param filter the filter for how filter the property assessments
         * @return the average lot size of the filtered property assessments
         */
        public static int minLotSize(Filter filter) { return lotSize("min", filter); }

        /**
         * Gets the specified lot size statistic of filtered property assessments
         *
         * @param minMaxAverage the type of static to get
         *                      The lost size statistics are as follows:
         *                          max: the largest lot size
         *                          min: the smallest lot size
         *                          avg: the average lot size
         * @param filter the filter for how filter the property assessments
         * @return the specified lot size statistic lot size of the filtered property assessments
         */
        private static int lotSize(String minMaxAverage, Filter filter) {
            StringBuilder query = new StringBuilder("?$$app_token=")
                    .append(appToken)
                    .append("&$select=")
                    .append(minMaxAverage)
                    .append("(lot_size) where ")
                    .append("assessment_year='")
                    .append((Year.now().getValue() - 1))
                    .append("'");


            String filterQuery = createFilterQueryString(filter);

            if(!filterQuery.isEmpty()) {
                query.append(" AND ").append(createFilterQueryString(filter));
            }

            System.out.println(query);

            String[] response = callEndpoint(query.toString()).replaceAll("\"", "").split("\n");


            try{
                return (int) Double.parseDouble(response[1]);
            } catch (NumberFormatException error) {
                return -1;
            }
        }
    }

    /**
     * Gets the average historical value of all properties after applying a given filter
     *
     * @param filter a filter object that specifies the fields to filter by
     * @return the average value of all property assessment found after filtering, or -1 if no results were found
     */
    public int getAvgHistoricalValue(Filter filter) {

        //TODO: create separate method for building this query string, (column names are slightly different)
        String filterQuery = createFilterQueryString(filter);

        // historical data has 4.2 million rows, searching them without any filter will almost certainly cause a timeout
        if (filterQuery.isEmpty())
            return -1;

        String result = getData("?$$app_token=" + appToken + "&$select=avg(assessed_value)&$where=" + filterQuery);
        String[] values = result.replaceAll("\"", "").split("\n");

        return (values.length > 1) ? Math.round( Float.parseFloat(values[1]) ) : -1;

    }
}
