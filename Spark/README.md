PowerTrain Vehicle Tracking App
===============================

An example of doing Rollups using DSE Analytics

The application looks for DSE in DSE_HOME
DSE_HOME should be the location such that DSE_HOME/bin/dse is a valid command

1. Ensure you are in the 'Spark' folder

2. Build the application using command :

		sbt package

3. Submit the application using command :

		dse spark-submit target/scala-2.10/powertrain_2.10-1.0.jar
