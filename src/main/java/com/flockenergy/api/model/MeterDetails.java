package com.flockenergy.api.model;

public class MeterDetails {

    private String meterId;
    private String serialNo;
    private String make;
    private String phaseType;
    private String installStatus;
    private String installationType;

    public MeterDetails(String meterId, String serialNo, String make,
                        String phaseType, String installStatus,
                        String installationType) {
        this.meterId = meterId;
        this.serialNo = serialNo;
        this.make = make;
        this.phaseType = phaseType;
        this.installStatus = installStatus;
        this.installationType = installationType;
    }

    public String getMeterId() {
        return meterId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public String getMake() {
        return make;
    }

    public String getPhaseType() {
        return phaseType;
    }

    public String getInstallStatus() {
        return installStatus;
    }

    public String getInstallationType() {
        return installationType;
    }
}