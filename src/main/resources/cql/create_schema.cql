CREATE KEYSPACE IF NOT EXISTS powertrain WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': '1' };

USE powertrain;

CREATE TABLE IF NOT EXISTS movies(
	title text,
	year int,
	PRIMARY KEY ((title, year))
) WITH compaction = { 'class' :  'LeveledCompactionStrategy' };

TRUNCATE movies;
