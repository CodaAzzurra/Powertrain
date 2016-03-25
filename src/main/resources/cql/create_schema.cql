// ------------------------------------
// Vechicle tracking application schema
// ------------------------------------

// Create keyspace for vehicle tracking

CREATE KEYSPACE IF NOT EXISTS vehicle_tracking_app WITH replication = {'class':'SimpleStrategy', 'replication_factor': 1};

// Create table to capture regular vehicle statistics

CREATE TABLE IF NOT EXISTS vehicle_tracking_app.vehicle_stats (
    vehicle_id text,
    time_period timestamp,
    collect_time timestamp,
    acceleration int,
    fuel_level float,
    lat_long text,
    mileage float,
    speed int,
    tile2 text,
    PRIMARY KEY ((vehicle_id, time_period), collect_time)
) WITH CLUSTERING ORDER BY (collect_time DESC)
  AND default_time_to_live = 86400;

TRUNCATE vehicle_tracking_app.vehicle_stats;

// Create table to store historical vehicle statistics

CREATE TABLE IF NOT EXISTS vehicle_tracking_app.vehicle_stats_history (
    vehicle_id text,
    time_period timestamp,
    vehicle_data blob,
    PRIMARY KEY (vehicle_id, time_period)
) WITH CLUSTERING ORDER BY (time_period DESC);

TRUNCATE vehicle_tracking_app.vehicle_stats_history;

// Create table to store car location information

CREATE TABLE IF NOT EXISTS vehicle_tracking_app.current_location (
  vehicle_id text,
  tile1 text,
  tile2 text,
  lat_long text,
  collect_time timestamp,
  PRIMARY KEY ((vehicle_id))
);

TRUNCATE vehicle_tracking_app.current_location;

// Captures significant vehicle events, braking hard etc.

CREATE TABLE IF NOT EXISTS vehicle_tracking_app.vehicle_events (
  vehicle_id text,
  time_period timestamp,
  collect_time timestamp,
  event_name text,
  event_value text,
  PRIMARY KEY ((vehicle_id, time_period), collect_time)
) WITH CLUSTERING ORDER BY (collect_time DESC);

TRUNCATE vehicle_tracking_app.vehicle_events;

