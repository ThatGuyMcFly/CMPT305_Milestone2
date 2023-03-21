package com.example.milestone3;

import java.util.Objects;

public class Location {

    private final double latitude;

    private final double longitude;

    /**
     * Constructor for the Location Class
     *
     * @param latitude - the location's latitude
     * @param longitude - the location longitude
     */
    public Location (String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    /**
     * Getter for the location's latitude
     *
     * @return the location's latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter for the location's longitude
     *
     * @return the location's longitude
     */
    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(latitude, location.latitude) &&
                Objects.equals(longitude, location.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
