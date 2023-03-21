package com.example.milestone3;

import java.util.Objects;

public record Address(String suite, String houseNumber, String street) {
    /**
     * Constructor for the Address Class
     *
     * @param suite       - the address suite
     * @param houseNumber - the address's house number
     * @param street      - the address's street
     */
    public Address {
    }

    /**
     * Getter for the address's suite
     *
     * @return the address's suite
     */
    @Override
    public String suite() {
        return this.suite;
    }

    /**
     * Getter for the addresses house number
     *
     * @return the address's house number
     */
    @Override
    public String houseNumber() {
        return this.houseNumber;
    }

    /**
     * Getter for the address's street
     *
     * @return the address's suite
     */
    @Override
    public String street() {
        return this.street;
    }

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
    public String toString() {
        return "Address{" +
                "suite='" + suite + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", street='" + street + '\'' +
                '}';
    }
}
