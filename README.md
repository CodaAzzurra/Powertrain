#  PowerTrain Vehicle Tracking App

This demo traces moving vehicles as they pass through geohash tiles. It also keeps track of a vehicle movements on a day to day basis. Similar to a vessel tracking.  

The application 

1. Allows the user to track a vehicles movements per day.

2. Find all vehicles per tile. Tiles have 2 sizes. Tile1 is large, Tile2 is small. 

3. Find all vehicles within a given radius of any vehicle

To specify contact points use the contactPoints command line parameter e.g. '-DcontactPoints=192.168.25.100,192.168.25.101'
The contact points can take multiple points in the IP,IP,IP (no spaces).
 
## Setup

To create the schema, run the following

  ```
  cqlsh -f  src/main/resources/cql/create_schema.cql
  ```
  
To create the Solr core, run 

  ```
  dsetool unload_core vehicle_tracking_app.current_location
  dsetool create_core vehicle_tracking_app.current_location reindex=true coreOptions=src/main/resources/solr/rt.yaml schema=src/main/resources/solr/geo.xml solrconfig=src/main/resources/solr/solrconfig.xml
  ```
  	
If you want to also query on where vehicles where at a certain time. 

  ```
  dsetool unload_core vehicle_tracking_app.vehicle_stats
  dsetool create_core vehicle_tracking_app.vehicle_stats reindex=true coreOptions=src/main/resources/solr/rt.yaml schema=src/main/resources/solr/geo_vehicle.xml solrconfig=src/main/resources/solr/solrconfig.xml	
  ```
  	
To continuously update the locations of the vehicles run 

  ```
  mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.vehicle.Main" -DcontactPoints=localhost
  ```
  	
To start the web server, in another terminal run 
  ```
  mvn jetty:run
  ```
Or on a different port with 
  ```
  mvn  -Djetty.port=8081 jetty:run
  ```

Having started the webserver, you can hit the game UI by going to:

    http://localhost:8080/vehicle-tracking-app/game

  	
To find all movements of a vehicle use http://localhost:8080/vehicle-tracking-app/rest/getmovements/{vehicle}/{date} e.g.

  ```
  curl http://localhost:8080/vehicle-tracking-app/rest/getmovements/1/2016-03-11
  ```
  
Or

  ```sql
  select * from vehicle_tracking_app.vehicle_stats where vehicle_id = '1' and time_period = '2016-03-11';
  ```
  
To find all vehicle movement, use the rest command http://localhost:8080/vehicle-tracking-app/rest/getvehicles/{tile} e.g.

  ```
  curl http://localhost:8080/vehicle-tracking-app/rest/getvehicles/u10e
  ```
  
or 

  ```sql
  select * from vehicle_tracking_app.current_location where solr_query = '{"q": "tile1:u10e"}' limit 1000;
  ```

To find all vehicles within a certain distance of a latitude and longitude, http://localhost:8080/vehicle-tracking-app/rest/search/{lat}/{long}/{distance}

  ```
  curl http://localhost:8080/vehicle-tracking-app/rest/search/52.53956077140064/-0.20225833920426117/5
  ```
  	
Or

  ```sql
  select * from vehicle_tracking_app.current_location where solr_query = '{"q": "*:*", "fq": "{!geofilt sfield=lat_long pt=52.53956077140064,-0.20225833920426117 d=5}"}' limit 1000;
  ```	
 	
If you have created the core on the vehicle table as well, you can run a query that will allow a user to search vehicles in a particular region in a particular time. 

  ```sql
  select * from vehicle_tracking_app.vehicle_stats where solr_query = '{"q": "*:*", "fq": "time_period:[2016-02-11T12:32:00.000Z TO 2016-03-11T12:34:00.000Z] AND {!bbox sfield=lat_long pt=51.404970234124800,-.206445841245690 d=1}"}' limit 1000;
  ```
  
To remove the tables and the schema, run the following.

  ```
  mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaTeardown"
  ```
    
To update a vehicle pass its id, location (lon + lat + elevation), speed and acceleration. This will add one if it doesn't already exist

  ```
  curl -X PUT http://localhost:8080/vehicle-tracking-app/rest/updateVehicleLocation/1683/22.53956077140064/-0.20225833920426117/10.00/55/5
  ```
  	
To get a vehicles location (lon + lat)

  ```
  curl http://localhost:8080/vehicle-tracking-app/rest/getvehiclelocation/199
  ```
  	
To steer a vehicle just change its location and if it crashes then its location will remain the same

To add a vehicles event

  ```
  curl -X PUT http://localhost:8080/vehicle-tracking-app/rest/addVehicleEvent/199/braking/12.5
  ```
  
To load test the application use cassandra-stress as follows;

  ```
  cassandra-stress user profile=stress.yaml ops\(insert=)
  ```

