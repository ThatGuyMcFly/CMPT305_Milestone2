package com.example.milestone2;

public class Filter {
    private final String address;
    private final String neighbourhood;
    private final String assessmentClass;
    private final int minimumAssessedValue;
    private final int maximumAssessedValue;

    /**
     * Builder class for the Filter class
     */
    public static class Builder {
        private String address = "";
        private String neighbourhood = "";
        private String assessmentClass = "";
        private int minimumAssessedValue = -1;
        private int maximumAssessedValue = -1;

        /**
         * Set address value
         *
         * @param val the address value
         * @return a reference to this Builder
         */
        public Builder address(String val) {
            address = val; return this;
        }

        /**
         * Set neighbourhood value
         *
         * @param val the neighbourhood value
         * @return a reference to this Builder
         */
        public Builder neighbourhood(String val) {
            neighbourhood = val; return this;
        }

        /**
         * Set assessmentClass value
         *
         * @param val the assessmentClass value
         * @return a reference to this Builder
         */
        public Builder assessmentClass(String val) {
            assessmentClass = val; return this;
        }

        /**
         * Set minimumAssessedValue value
         *
         * @param val the minimumAssessedValue value
         * @return a reference to this Builder
         */
        public Builder minimumAssessedValue(int val) {
            minimumAssessedValue = val; return this;
        }

        /**
         * Set maximumAssessedValue value
         *
         * @param val the maximumAssessedValue value
         * @return a reference to this Builder
         */
        public Builder maximumAssessedValue(int val) {
            maximumAssessedValue = val; return this;
        }

        /**
         * The function for building the filter
         *
         * @return a new filter object
         */
        public Filter build() {
            return new Filter(this);
        }
    }

    /**
     * Constructor for the Filter class
     *
     * @param builder The Builder object used to build the filter
     */
    private Filter(Builder builder) {
        address = builder.address;
        neighbourhood = builder.neighbourhood;
        assessmentClass = builder.assessmentClass;
        minimumAssessedValue = builder.minimumAssessedValue;
        maximumAssessedValue = builder.maximumAssessedValue;
    }

    /**
     * Getter for the address
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Getter for the neighbourhood
     *
     * @return the neighbourhood
     */
    public String getNeighbourhood() {
        return neighbourhood;
    }

    /**
     * Getter for the assessment class
     *
     * @return the assessment class
     */
    public String getAssessmentClass() {
        return assessmentClass;
    }

    /**
     * Getter for the minimum assessed value
     *
     * @return the minimum assessed value
     */
    public int getMinimumAssessedValue() {
        return minimumAssessedValue;
    }

    /**
     * Getter for the maximum assessed value
     *
     * @return the maximum assessed value
     */
    public int getMaximumAssessedValue() {
        return maximumAssessedValue;
    }
}
