package com.datastax.demo.vehicle.model;

import com.github.davidmoten.geo.LatLong;

import java.util.Date;

public class Vehicle {
    private String vehicleId;
    private Date timePeriod;
    private Date collectTime;
    private int acceleration;
    private float fuelLevel;
    private LatLong latLong;
    private float mileage;
    private int speed;
    private String lastTile;
    private String currentTile;

    public Vehicle(String vehicleId, Date timePeriod, Date collectTime, int acceleration, float fuelLevel, LatLong latLong, float mileage, int speed, String lastTile, String currentTile) {
        this.vehicleId = vehicleId;
        this.timePeriod = timePeriod;
        this.collectTime = collectTime;
        this.acceleration = acceleration;
        this.fuelLevel = fuelLevel;
        this.latLong = latLong;
        this.mileage = mileage;
        this.speed = speed;
        this.lastTile = lastTile;
        this.currentTile = currentTile;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public Date getTimePeriod() {
        return timePeriod;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getFuelLevel() {
        return fuelLevel;
    }

    public LatLong getLatLong() {
        return latLong;
    }

    public float getMileage() {
        return mileage;
    }

    public int getSpeed() {
        return speed;
    }

    public String getLastTile() {
        return lastTile;
    }

    public String getCurrentTile() {
        return currentTile;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId='" + vehicleId + '\'' +
                ", timePeriod=" + timePeriod +
                ", collectTime=" + collectTime +
                ", acceleration=" + acceleration +
                ", fuelLevel=" + fuelLevel +
                ", latLong=" + latLong +
                ", mileage=" + mileage +
                ", speed=" + speed +
                ", lastTile='" + lastTile + '\'' +
                ", currentTile='" + currentTile + '\'' +
                '}';
    }
}
