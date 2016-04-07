package com.datastax.demo.vehicle.webservice;

import com.datastax.demo.vehicle.VehicleDao;
import com.datastax.demo.vehicle.SessionService;
import com.datastax.demo.vehicle.containermanaged.ContextSessionService;
import com.datastax.demo.vehicle.model.Location;
import com.datastax.demo.vehicle.model.Vehicle;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleService {

    private VehicleDao dao;

    public VehicleService() {
    }

    public List<Vehicle> getVehicleMovements(String vehicle, String dateString) {
        return getVehicleDao().getVehicleMovements(vehicle, dateString);
    }

    public List<Vehicle> getVehiclesByTile(String tile) {
        return getVehicleDao().getVehiclesByTile(tile);
    }

    public List<Vehicle> searchVehiclesByLonLatAndDistance(int distance, Location location) {
        return getVehicleDao().searchVehiclesByLonLatAndDistance(distance, location);
    }

    public Location getVehicleLocation(String vehicleId) {
        return getVehicleDao().getVehiclesLocation(vehicleId);
    }

    public void updateVehicleLocation(String vehicleId, Location location) {
        Map<String, Location> newLocation = new HashMap<>(1);
        newLocation.put(vehicleId, location);
        getVehicleDao().insertVehicleLocation(newLocation);
    }

    public void addVehicleEvent(String vehicleId, String eventName, String eventValue) {
        getVehicleDao().addVehicleEvent(vehicleId, eventName, eventValue);
    }

    private VehicleDao getVehicleDao() {
        if (dao == null) {
            dao = new VehicleDao(new ContextSessionService().getSession());
        }
        return dao;
    }
}
