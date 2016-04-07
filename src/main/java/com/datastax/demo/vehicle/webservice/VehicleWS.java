package com.datastax.demo.vehicle.webservice;

import com.datastax.demo.vehicle.model.Location;
import com.datastax.demo.vehicle.model.Vehicle;
import com.github.davidmoten.geo.LatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.List;

@WebService
@Path("/")
public class VehicleWS {
    private static final Logger logger = LoggerFactory.getLogger(VehicleWS.class);

    // Service Layer
    private VehicleService service;

    public VehicleWS() {
        logger.debug("Created WS");
    }

    @Context
    public void setServletContext(ServletContext context) {
        if (service == null) {
            service = new VehicleService(context);
        }
    }

    @GET
    @Path("/getmovements/{vehicle}/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovements(@PathParam("vehicle") String vehicle, @PathParam("date") String dateString) {
        List<Vehicle> result = service.getVehicleMovements(vehicle, dateString);
        return Response.status(201).entity(result).build();
    }

    @GET
    @Path("/getvehicles/{tile}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVehiclesByTile(@PathParam("tile") String tile) {
        List<Vehicle> result = service.getVehiclesByTile(tile);
        return Response.status(201).entity(result).build();
    }

    @GET
    @Path("/search/{lat}/{lon}/{elevation}/{distance}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchForVehicles(@PathParam("lat") double lat, @PathParam("lon") double lon, @PathParam("elevation") double elevation, @PathParam("distance") int distance) {
        List<Vehicle> result = service.searchVehiclesByLonLatAndDistance(distance, new Location(new LatLong(lat, lon), elevation));
        return Response.status(201).entity(result).build();
    }

    @GET
    @Path("/getvehiclelocation/{vehicle}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVehicleLocation(@PathParam("vehicle") String vehicle) {
        Location result = service.getVehicleLocation(vehicle);
        return Response.status(201).entity(result).build();
    }

    @GET
    @Path("/updateVehicleLocation/{vehicle}/{lon}/{lat}/{elevation}/{speed}/{acceleration}")
    @Produces("text/html")
    public Response updateVehicleLocation(@PathParam("vehicle") String vehicle, @PathParam("lon") String lon,
                                          @PathParam("lat") String lat, @PathParam("elevation") String elevation,
                                          @PathParam("speed") Integer speed, @PathParam("acceleration") Integer acceleration) {
        service.updateVehicleLocation(vehicle, new Location(new LatLong(Double.parseDouble(lat), Double.parseDouble(lon)), Double.parseDouble(elevation)), speed, acceleration);
        return Response.ok("success").build();
    }

    @GET
    @Path("/addVehicleEvent/{vehicle}/{name}/{value}")
    @Produces("text/html")
    public Response addVehicleEvent(@PathParam("vehicle") String vehicle, @PathParam("name") String name,
                                    @PathParam("value") String value) {
        service.addVehicleEvent(vehicle, name, value);
        return Response.ok("success").build();
    }
}
