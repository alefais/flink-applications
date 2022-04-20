# Benchmarching Applications - Flink version

This repository contains a collection of Data Stream Processing applications implemented with [Apache Storm](http://storm.apache.org/) and adapted to be executed on [Apache Flink](https://ci.apache.org/projects/flink/flink-docs-release-1.7/) by means of the [Storm Compatibility API](https://ci.apache.org/projects/flink/flink-docs-release-1.7/dev/libs/storm_compatibility.html).

Applications can be run in local mode, further information can be found in each application README and in the [official documentation](https://ci.apache.org/projects/flink/flink-docs-release-1.7/tutorials/local_setup.html).

The Storm version of the applications can be found in the [storm-applications](https://github.com/alefais/storm-applications) repository. One more implementation has been provided, using [WindFlow](https://github.com/ParaGroup/WindFlow) C++17 library, and can be found in the [windflow-applications](https://github.com/alefais/windflow-applications) repository.

## Dependencies 
In order to run the applications contained in this project, the following dependencies are needed:
* Apache Storm version 1.1.3 -> see [here](https://storm.apache.org/index.html)
* Apache Flink version 1.7.2 -> see [here](https://nightlies.apache.org/flink/flink-docs-release-1.7/)
* Java JDK version 1.8 -> see [here](https://openjdk.java.net/install/)
* [OSGeo GeoTools library](https://staging.www.osgeo.org/projects/geotools/) version 11.1 -> see [here](https://sourceforge.net/projects/geotools/files/GeoTools%2011%20Releases/)
