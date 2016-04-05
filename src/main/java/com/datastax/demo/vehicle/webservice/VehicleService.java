package com.datastax.demo.vehicle.webservice;

import com.datastax.demo.vehicle.VehicleDao;
import com.datastax.demo.vehicle.model.Location;
import com.datastax.demo.vehicle.model.Vehicle;
import com.github.davidmoten.geo.LatLong;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleService {

    private static final VehicleDao dao = new VehicleDao(System.getProperty("contactPoints", "127.0.0.1").split(","));

    public VehicleService() {
    }

    public List<Vehicle> getVehicleMovements(String vehicle, String dateString) {
        return dao.getVehicleMovements(vehicle, dateString);
    }

    public List<Vehicle> getVehiclesByTile(String tile) {
        return dao.getVehiclesByTile(tile);
    }

    public List<Vehicle> searchVehiclesByLonLatAndDistance(int distance, Location location) {
        return dao.searchVehiclesByLonLatAndDistance(distance, location);
    }

    public Location getVehicleLocation(String vehicleId) {
        return dao.getVehiclesLocation(vehicleId);
    }

    public void updateVehicleLocation(String vehicleId, Location location) {
        Map<String, Location> newLocation = new HashMap<String, Location>(1);
        newLocation.put(vehicleId, location);
        dao.insertVehicleLocation(newLocation);
    }

    public void addVehicleEvent(String vehicleId, String eventName, String eventValue) {
        dao.addVehicleEvent(vehicleId, eventName, eventValue);
    }
}
