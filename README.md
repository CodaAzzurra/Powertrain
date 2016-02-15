# Powertrain
Team Powertrain - Field Summit 2016

Please see the [LICENSE][license] and use at your own risk.

Please see the [DataStax Demo Lib][github-demolib] project.

## Configure the Cluster

Please see the [Prepare the Cluster][wiki-preparecluster] wiki for information on setting up a DataStax Enterprise cluster. We developed and tested this project against [DataStax Enterprise 4.8.4][dse484].

#### Contact Points

To specify contact points, use the `contactPoints` command line parameter. The value may contact multiple IPs in the format `IP,IP,IP`, without spaces. For example: `-DcontactPoints=192.168.25.100,192.168.25.101`.

#### Schema

To create the schema, run:

	mvn clean compile exec:java -Dexec.mainClass=com.datastax.demo.schema.SchemaSetup -DcontactPoints=localhost
	
To remove the schema, run:

	mvn clean compile exec:java -Dexec.mainClass=com.datastax.demo.schema.SchemaTeardown -DcontactPoints=localhost

## Web Services

#### Launch the Web Server

To start the web server, in another terminal run:

	mvn jetty:run


[github-demolib]: https://github.com/CodaAzzurra/datastax-demo-lib "DataStax Demo Lib"
[dse484]: http://docs.datastax.com/en/datastax_enterprise/4.8/datastax_enterprise/RNdse.html?scroll=relnotes48__484 "DataStax Enterprise 4.8.4"
[license]: LICENSE "License"
[wiki-preparecluster]: https://github.com/DC4DS/datastax-xml-demo/wiki/Prepare-the-Cluster "Prepare the Cluster"