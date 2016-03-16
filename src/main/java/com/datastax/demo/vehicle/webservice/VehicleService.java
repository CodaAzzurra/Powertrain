package com.datastax.demo.vehicle.webservice;

import com.datastax.demo.vehicle.VehicleDao;
import com.datastax.demo.vehicle.model.Vehicle;
import com.github.davidmoten.geo.LatLong;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleService {

    private VehicleDao dao;

    public VehicleService() {
        String contactPointsStr = System.getProperty("contactPoints", "localhost");
        this.dao = new VehicleDao(contactPointsStr.split(","));
    }

    public List<Vehicle> getVehicleMovements(String vehicle, String dateString) {
        return dao.getVehicleMovements(vehicle, dateString);
    }

    public List<Vehicle> getVehiclesByTile(String tile) {

        return dao.getVehiclesByTile(tile);

    }

    public List<Vehicle> searchVehiclesByLonLatAndDistance(int distance, LatLong latLong) {
        return dao.searchVehiclesByLonLatAndDistance(distance, latLong);
    }

    public LatLong getVehicleLocation(String vehicleId) {
        return dao.getVehiclesLocation(vehicleId);
    }

    public void updateVehicleLocation(String vehicleId, LatLong latLong) {
        Map<String, LatLong> newLocation = new HashMap<String, LatLong>(1);
        newLocation.put(vehicleId, latLong);
        dao.insertVehicleLocation(newLocation);
    }

    public void addVehicleEvent(String vehicleId, String eventName, String eventValue) {
        dao.addVehicleEvent(vehicleId, eventName, eventValue);
    }
}
