package com.flockenergy.api.model;

public class MeterHierarchy {

    private String zone;
    private String circle;
    private String division;
    private String subdivision;
    private String subStation;
    private String feeder;
    private String dt;

    public MeterHierarchy(String zone, String circle, String division,
                          String subdivision, String subStation,
                          String feeder, String dt) {
        this.zone = zone;
        this.circle = circle;
        this.division = division;
        this.subdivision = subdivision;
        this.subStation = subStation;
        this.feeder = feeder;
        this.dt = dt;
    }

    public String getZone() {
        return zone;
    }

    public String getCircle() {
        return circle;
    }

    public String getDivision() {
        return division;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public String getSubStation() {
        return subStation;
    }

    public String getFeeder() {
        return feeder;
    }

    public String getDt() {
        return dt;
    }
}