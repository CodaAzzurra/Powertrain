package com.datastax.demo.vehicle;

import com.datastax.demo.utils.AsyncWriterWrapper;
import com.datastax.demo.vehicle.model.Vehicle;
import com.datastax.driver.core.*;
import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;

public class VehicleDao {

    private static Logger logger = LoggerFactory.getLogger(VehicleDao.class);

    private Session session;
    private static String keyspaceName = "vehicle_tracking_app";
    private static String vehicleTable = keyspaceName + ".vehicle_stats";
    private static String currentLocationTable = keyspaceName + ".current_location";

    private static final String INSERT_INTO_VEHICLE = "insert into " + vehicleTable + " (vehicle_id, time_period, collect_time, lat_long, tile2, speed, acceleration, fuel_level, mileage) values (?,?,?,?,?,?,?,?,?);";
    private static final String INSERT_INTO_CURRENTLOCATION = "insert into " + currentLocationTable + "(vehicle_id, tile1, tile2, lat_long, collect_time) values (?,?,?,?,?)";

    private static final String QUERY_BY_VEHICLE = "select * from " + vehicleTable + " where vehicle_id = ? and time_period = ?";

    private static LocalDateTime now = LocalDateTime.now();

    private PreparedStatement insertVehicle;
    private PreparedStatement insertCurrentLocation;
    private PreparedStatement queryVehicle;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public VehicleDao(String[] contactPoints) {

        Cluster cluster = Cluster.builder()
                .addContactPoints(contactPoints).build();

        this.session = cluster.connect();

        this.insertVehicle = session.prepare(INSERT_INTO_VEHICLE);
        this.insertCurrentLocation = session.prepare(INSERT_INTO_CURRENTLOCATION);

        this.queryVehicle = session.prepare(QUERY_BY_VEHICLE);
    }

    public void insertVehicleLocation(Map<String, LatLong> newLocations) {
        long day = 24 * 60 * 60 * 1000;
        Date today = new Date((System.currentTimeMillis() / day) * day);
        AsyncWriterWrapper wrapper = new AsyncWriterWrapper();
        Random random = new Random();
        Set<Entry<String, LatLong>> entrySet = newLocations.entrySet();

        // Update time for reading
        now = now.plusSeconds(10);
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        Date nowDate = Date.from(instant);

        for (Entry<String, LatLong> entry : entrySet) {

            String tile1 = GeoHash.encodeHash(entry.getValue(), 4);
            String tile2 = GeoHash.encodeHash(entry.getValue(), 7);

            int speed = Math.abs(random.nextInt() % 100);
            float fuelLevel = Math.abs(random.nextFloat() % 50);
            float mileage = Math.abs(random.nextFloat() % 50000);
            int acceleration = Math.abs(random.nextInt() % 100);

            wrapper.addStatement(insertVehicle.bind(entry.getKey(), today, nowDate,
                    entry.getValue().getLat() + "," + entry.getValue().getLon(), tile2, speed, acceleration, fuelLevel, mileage));

            wrapper.addStatement(insertCurrentLocation.bind(entry.getKey(), tile1, tile2,
                    entry.getValue().getLat() + "," + entry.getValue().getLon(), nowDate));
        }
        wrapper.executeAsync(this.session);
    }

    public List<Vehicle> getVehicleMovements(String vehicleId, String dateString) {
        ResultSet resultSet = null;

        try {
            // Example date time is 2001-07-04T12:08:56.235-0700
            resultSet = session.execute(this.queryVehicle.bind(vehicleId, dateFormatter.parse(dateString + "T00:00:00.000-0000")));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        List<Vehicle> vehicleMovements = new ArrayList<Vehicle>();
        List<Row> all = resultSet.all();

        for (Row row : all) {
            Date timePeriod = row.getTimestamp("time_period");
            Date collectTime = row.getTimestamp("collect_time");
            Integer acceleration = row.getInt("acceleration");
            Float fuelLevel = row.getFloat("fuel_level");
            Float mileage = row.getFloat("mileage");
            Integer speed = row.getInt("speed");
            String lat_long = row.getString("lat_long");
            String lastTile = row.getString("tile2");
            Double lat = Double.parseDouble(lat_long.substring(0, lat_long.lastIndexOf(",")));
            Double lng = Double.parseDouble(lat_long.substring(lat_long.lastIndexOf(",") + 1));

            Vehicle vehicle = new Vehicle(vehicleId, timePeriod, collectTime, acceleration, fuelLevel, new LatLong(lat, lng), mileage, speed, lastTile, "");
            vehicleMovements.add(vehicle);
        }

        return vehicleMovements;
    }

    public List<Vehicle> searchVehiclesByLonLatAndDistance(int distance, LatLong latLong) {

        String cql = "select * from " + currentLocationTable
                + " where solr_query = '{\"q\": \"*:*\", \"fq\": \"{!geofilt sfield=lat_long pt="
                + latLong.getLat() + "," + latLong.getLon() + " d=" + distance + "}\"}'  limit 1000";
        ResultSet resultSet = session.execute(cql);

        List<Vehicle> vehicleMovements = new ArrayList<Vehicle>();
        List<Row> all = resultSet.all();

        for (Row row : all) {
            vehicleMovements.add(getVehicleFromLocation(row.getString("vehicle_id"), row));
        }

        return vehicleMovements;
    }

    public List<Vehicle> getVehiclesByTile(String tile) {
        String cql = "select * from " + currentLocationTable + " where solr_query = '{\"q\": \"tile1: " + tile + "\"}' limit 1000";
        ResultSet resultSet = session.execute(cql);

        List<Vehicle> vehicleMovements = new ArrayList<Vehicle>();
        List<Row> all = resultSet.all();

        for (Row row : all) {
            vehicleMovements.add(getVehicleFromLocation(row.getString("vehicle_id"), row));
        }

        return vehicleMovements;
    }

    private Vehicle getVehicleFromLocation(String vehicleId, Row row) {
        Date collectTime = row.getTimestamp("collect_time");
        String lat_long = row.getString("lat_long");
        String tile1 = row.getString("tile1");
        String tile2 = row.getString("tile2");
        Double lat = Double.parseDouble(lat_long.substring(0, lat_long.lastIndexOf(",")));
        Double lng = Double.parseDouble(lat_long.substring(lat_long.lastIndexOf(",") + 1));

        return new Vehicle(vehicleId, null, collectTime, 0, 0.0f, new LatLong(lat, lng), 0.0f, 0, tile1, "");
    }
}