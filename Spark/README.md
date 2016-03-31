PowerTrain Vehicle Tracking App
===============================

An example of doing Rollups using Dse Analytics

The application looks for DSE in DSE_HOME
DSE_HOME should be the location such that DSE_HOME/bin/dse is a valid command

Build the application using command :

	sbt package

then submit the application using command :

dse spark-submit target/scala-2.10/powertrain_2.10-1.0.jar
