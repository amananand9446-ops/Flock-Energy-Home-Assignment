package com.flockenergy.api.model;

public class MeterDetailsResponse {

    private MeterDetails details;
    private MeterHierarchy hierarchy;

    public MeterDetailsResponse(MeterDetails details, MeterHierarchy hierarchy) {
        this.details = details;
        this.hierarchy = hierarchy;
    }

    public MeterDetails getDetails() {
        return details;
    }

    public MeterHierarchy getHierarchy() {
        return hierarchy;
    }
}