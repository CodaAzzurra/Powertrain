package com.datastax.demo.vehicle.webservice;

import com.datastax.demo.vehicle.VehicleDao;
import com.datastax.demo.vehicle.model.Location;
import com.datastax.demo.vehicle.model.Vehicle;

import javax.servlet.ServletContext;
import java.util.List;

public class VehicleService {

    private VehicleDao dao = null;

    public VehicleService(ServletContext context) {
        dao = new ContextSessionService(context).getDAO();
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

    public void updateVehicleLocation(String vehicleId, Location location, double speed, double acceleration) {
        dao.updateVehicle(vehicleId, location, speed, acceleration);
    }

    public void addVehicleEvent(String vehicleId, String eventName, String eventValue) {
        dao.addVehicleEvent(vehicleId, eventName, eventValue);
    }
}
