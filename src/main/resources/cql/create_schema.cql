CREATE KEYSPACE IF NOT EXISTS vehicle_tracking_app WITH replication = {'class':'NetworkTopologyStrategy', 'DC1': 3};


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
  
CREATE TABLE IF NOT EXISTS vehicle_tracking_app.vehicle_stats_history (
    vehicle_id text,
    time_period timestamp,
    vehicle_data blob,
    PRIMARY KEY (vehicle_id, time_period)
) WITH CLUSTERING ORDER BY (time_period DESC);

TRUNCATE vehicle_tracking_app.vehicle_stats_history;
  
CREATE TABLE IF NOT EXISTS vehicle_tracking_app.current_location (
	vehicle_id text PRIMARY KEY,
	tile1 text,
	tile2 text,	
	lat_long text,
	collect_time timestamp
) WITH COMPACTION = { 'class' :  'LeveledCompactionStrategy'  };

TRUNCATE vehicle_tracking_app.current_location;
