package com.cz.usbserial.adapter;

public class tyreInfo {
    private boolean LOSFlag;
    private boolean airLeakageFlag;
    private boolean highPreFlag;
    private boolean highTemperFlag;
    private boolean lowBatteryFlag;
    private boolean lowPreFlag;
    private boolean preUnbalanceFlag;
    private int tyrePressure;
    private int tyreTemperature;

    public float getTyrePressure() {
        return (float) this.tyrePressure;
    }

    public void setTyrePressure(int tyrePressure2) {
        this.tyrePressure = tyrePressure2;
    }

    public int getTyreTemperature() {
        return this.tyreTemperature;
    }

    public void setTyreTemperature(int tyreTemperature2) {
        this.tyreTemperature = tyreTemperature2;
    }

    public boolean isHighPreFlag() {
        return this.highPreFlag;
    }

    public void setHighPreFlag(boolean highPreFlag2) {
        this.highPreFlag = highPreFlag2;
    }

    public boolean isLowPreFlag() {
        return this.lowPreFlag;
    }

    public void setLowPreFlag(boolean lowPreFlag2) {
        this.lowPreFlag = lowPreFlag2;
    }

    public boolean isHighTemperFlag() {
        return this.highTemperFlag;
    }

    public void setHighTemperFlag(boolean highTemperFlag2) {
        this.highTemperFlag = highTemperFlag2;
    }

    public boolean isAirLeakageFlag() {
        return this.airLeakageFlag;
    }

    public void setAirLeakageFlag(boolean airLeakageFlag2) {
        this.airLeakageFlag = airLeakageFlag2;
    }

    public boolean isPreUnbalanceFlag() {
        return this.preUnbalanceFlag;
    }

    public void setPreUnbalanceFlag(boolean preUnbalanceFlag2) {
        this.preUnbalanceFlag = preUnbalanceFlag2;
    }

    public boolean isLowBatteryFlag() {
        return this.lowBatteryFlag;
    }

    public void setLowBatteryFlag(boolean lowBatteryFlag2) {
        this.lowBatteryFlag = lowBatteryFlag2;
    }

    public boolean isLOSFlag() {
        return this.LOSFlag;
    }

    public void setLOSFlag(boolean lOSFlag) {
        this.LOSFlag = lOSFlag;
    }
}
