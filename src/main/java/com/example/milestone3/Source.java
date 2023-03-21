package com.example.milestone3;

public enum Source {

    CSV("CSV File"),
    API("Edmonton's Open Data Portal");

    private final String source;
    Source(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
