package com.example.milestone2;

import java.util.Objects;

public class Address {
    private final String suite;

    private final String houseNumber;

    private final String street;

    /**
     * Constructor for the Address Class
     *
     * @param suite - the address suite
     * @param houseNumber - the address's house number
     * @param street - the address's street
     */
    public Address(String suite, String houseNumber, String street) {
        this.suite = suite;
        this.houseNumber = houseNumber;
        this.street = street;
    }

    /**
     * Getter for the address's suite
     *
     * @return the address's suite
     */
    public String getSuite() {
        return this.suite;
    }

    /**
     * Getter for the addresses house number
     *
     * @return the address's house number
     */
    public String getHouseNumber() {
        return this.houseNumber;
    }

    /**
     * Getter for the address's street
     *
     * @return the address's suite
     */
    public String getStreet() { return this.street; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(suite, address.suite) &&
                Objects.equals(houseNumber, address.houseNumber) &&
                Objects.equals(street, address.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suite, houseNumber, street);
    }

    @Override
    public String toString() {
        return "Address{" +
                "suite='" + suite + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", street='" + street + '\'' +
                '}';
    }
}
