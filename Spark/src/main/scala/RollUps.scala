
import org.apache.spark._
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.cassandra._

import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector._

object VehicleRollUps extends App {

  val conf = new SparkConf()
    .setAppName("Vehicle RollUps")

  // A SparkContext
  val sc = new SparkContext(conf)

  // This Hive Context will use the DSE HiveMetaStore
  val sqlContext = new HiveContext(sc)

  import sqlContext.implicits._

  val result = sqlContext.sql("select * from vehicle_tracking_app.vehicle_stats")

  val result2 = result.groupBy("vehicle_id", "time_period")
    .agg(avg(result.col("acceleration")) as "acceleration_avg", min(result.col("acceleration")) as "acceleration_min", max(result.col("acceleration")) as "acceleration_max",
      avg(result.col("fuel_level")) as "fuel_level_avg", min(result.col("fuel_level")) as "fuel_level_min", max(result.col("fuel_level")) as "fuel_level_max",
      min(result.col("mileage")) as "mileage_min", max(result.col("mileage")) as "mileage_max",
      avg(result.col("speed")) as "speed_avg", min(result.col("speed")) as "speed_min", max(result.col("speed")) as "speed_max")

  result2.write
    .format("org.apache.spark.sql.cassandra")
    .options(Map("table" -> "vehicle_stats_history", "keyspace" -> "vehicle_tracking_app"))
    .save()

//  vehicle_id | time_period              | acceleration_avg | acceleration_max | acceleration_min | fuel_level_avg | fuel_level_max | fuel_level_min | mileage_max | mileage_min | speed_avg | speed_max | speed_min
// ------------+--------------------------+------------------+------------------+------------------+----------------+----------------+----------------+-------------+-------------+-----------+-----------+-----------
//       Ankur | 2016-04-14 00:00:00+0000 |         0.628767 |                3 |                0 |       0.517561 |       0.990344 |     9.5367e-06 |    0.984165 |    0.027597 |   5.48501 |        10 |         0
  
  sc.stop()
  sys.exit(0)
}

