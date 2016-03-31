
import org.apache.spark._
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.cassandra._

import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector._

object Example extends App {

  val conf = new SparkConf()
    .setAppName("Vehicle RollUps")
  
  // A SparkContext
  val sc = new SparkContext(conf)

  // This Hive Context will use the DSE HiveMetaStore
  val sqlContext = new HiveContext(sc)

  import sqlContext.implicits._

  val result = sqlContext.sql("select * from vehicle_tracking_app.vehicle_stats")

  val result2 = result.groupBy($"vehicle_id",$"time_period")
        .agg(avg(result.col("acceleration")) as "acceleration_avg",min(result.col("acceleration")) as "acceleration_min",max(result.col("acceleration")) as "acceleration_max",
        avg(result.col("fuel_level")) as "fuel_level_avg",min(result.col("fuel_level")) as "fuel_level_min",max(result.col("fuel_level")) as "fuel_level_max",
        min(result.col("mileage")) as "mileage_min",max(result.col("mileage")) as "mileage_max",
         avg(result.col("speed")) as "speed_avg",min(result.col("speed")) as "speed_min",max(result.col("speed"))as "speed_max")

  result2.write
  .format("org.apache.spark.sql.cassandra")
  .options(Map( "table" -> "vehicle_stats_history", "keyspace" -> "vehicle_tracking_app"))
  .save()

// +----------+--------------------+------------------+-----------------+-----------------+-------------------+---------------+---------------+------------+------------+------------------+----------+----------+
// |vehicle_id|         time_period| AVG(acceleration)|MIN(acceleration)|MAX(acceleration)|    AVG(fuel_level)|MIN(fuel_level)|MAX(fuel_level)|MIN(mileage)|MAX(mileage)|        AVG(speed)|MIN(speed)|MAX(speed)|
// +----------+--------------------+------------------+-----------------+-----------------+-------------------+---------------+---------------+------------+------------+------------------+----------+----------+
// |      2603|2016-03-28 17:00:...| 51.14912280701754|                0|               99| 0.5205721980647037|   0.0034981966|      0.9996461|  0.01814419|   0.9932446| 52.51754385964912|         0|        99|
// |      7185|2016-03-28 17:00:...| 49.08403361344538|                0|               99|  0.430089305929777|   0.0041342974|      0.9906128| 0.031347573|    0.998426| 48.60504201680672|         0|        99|
// |      9011|2016-03-28 17:00:...| 48.14782608695652|                0|               99|  0.522721034547557|   0.0024577975|      0.9973233|0.0022397041|   0.9960209| 50.10434782608696|         0|        98|
// |      4836|2016-03-28 17:00:...| 50.08461538461538|                0|               99|0.48794252093021684|    7.161498E-4|        0.99809|  0.02206105|  0.99614185|56.184615384615384|         1|        98|
// |      2892|2016-03-28 17:00:...| 54.29752066115702|                0|               99| 0.5459238985353265|    0.029033124|      0.9983402|0.0011440516|   0.9808988| 47.85123966942149|         1|        99|
// |      1712|2016-03-28 17:00:...| 47.25581395348837|                0|               98| 0.4967966892922571|   0.0044302344|     0.97850305|0.0018367767|  0.97760963|49.007751937984494|         1|        99|
// |       856|2016-03-28 17:00:...| 47.99159663865546|                0|               99| 0.5085205736280489|     0.03656888|     0.99469316| 0.012050509|   0.9958049| 49.79831932773109|         0|        99|
// |      8076|2016-03-28 17:00:...| 52.30327868852459|                2|               99|0.47949227250990323|   0.0049682856|      0.9938699|0.0013386011|   0.9883454|48.040983606557376|         1|        99|
// |      3945|2016-03-28 17:00:...|50.517857142857146|                2|               96|0.44187524329338757|   0.0016070008|      0.9669615|0.0025550127|  0.99467963|              46.5|         0|        97|
// |      9129|2016-03-28 17:00:...|57.057377049180324|                1|               99|0.48265716091531224|   0.0017360449|     0.99852717| 0.023367107|  0.98898655|44.959016393442624|         0|        99|
// |      7842|2016-03-28 17:00:...| 48.18487394957983|                0|               97| 0.5489666241557658|    0.008322954|     0.98104453|0.0017975569|  0.99112993| 54.05042016806723|         0|        99|
// |      8571|2016-03-28 17:00:...| 48.28333333333333|                0|               99| 0.5057836944858233|    0.006485641|       0.992574| 0.013912499|  0.99632066|54.516666666666666|         1|        99|
// |      6005|2016-03-28 17:00:...| 52.25217391304348|                4|               99| 0.5385552582533463|   0.0063735843|      0.9913273| 0.009194553|  0.97938305| 58.06956521739131|         2|        99|
// |      4674|2016-03-28 17:00:...| 48.68141592920354|                3|               99| 0.4539802343444487|   4.3469667E-4|      0.9608995|0.0087662935|   0.9904214|53.017699115044245|         0|        99|
// |      6951|2016-03-28 17:00:...| 44.38095238095238|                0|               99| 0.5230392316977183|    0.014701188|     0.99921393| 0.007881939|   0.9998014| 48.87301587301587|         0|        99|
// |      8689|2016-03-28 17:00:...|  48.3859649122807|                0|               98|0.47712347643417224|    0.014300585|       0.996965| 0.012702167|   0.9760981|46.719298245614034|         0|        97|
// |       900|2016-03-28 17:00:...|44.858333333333334|                1|               99| 0.5406516174475352|   0.0032826066|     0.99019694|0.0022576451|  0.98446167| 50.65833333333333|         0|        99|
// |      6294|2016-03-28 17:00:...| 48.19130434782609|                0|               99| 0.5303063128305519|    0.011549592|      0.9939139| 0.011236548|   0.9902077|51.052173913043475|         1|        98|
// |      1055|2016-03-28 17:00:...|50.904347826086955|                0|               99|  0.508100584278936|   0.0030665994|      0.9934465| 0.016603887|   0.9888051| 46.87826086956522|         2|        98|
// |       199|2016-03-28 17:00:...|           47.9375|                0|               99| 0.5249212915077806|   1.9389391E-4|      0.9964988| 0.004615009|   0.9873213|           50.6875|         0|        99|
// +----------+--------------------+------------------+-----------------+-----------------+-------------------+---------------+---------------+------------+------------+------------------+----------+----------+

  sc.stop()
  sys.exit(0)

  //dse spark-submit target/scala-2.10/sbtvsdse_2.10-1.0.jar
}

