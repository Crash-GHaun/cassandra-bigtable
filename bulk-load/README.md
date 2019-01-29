# Bulk Load Tool

[Goolge Bigtable](https://cloud.google.com/bigtable/) is a compressed, high performance, data storage system built on [Google File System](https://en.wikipedia.org/wiki/Google_File_System), [Chubby Lock Service](https://ai.google/research/pubs/pub27897), SSTable (log-structured storage like [LevelDB](https://en.wikipedia.org/wiki/LevelDB)) and a few other Google technologies.

Bulk-load aim to provide an easy way to transfer records from [Cassandra](http://cassandra.apache.org/) into Google's BigTable.
To achieve this we are using Apache Beam on top of Google's DataFlow as our backend engine.
The code here is a baseline for any transformation graph you would like to create in the future. You can always extend the buildPipeline method in CassandraToBigtable to get some funky stuff into the graph :-)

Our deployment is driven by a Makefile so you don't need to type too much to get things running...

## Prerequisites
* [Java(TM) SE Runtime Environment 1.8](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Maven 3.5](https://maven.apache.org/) and above
* [Google Cloud SDK](https://cloud.google.com/sdk/)

## Setup
[![Open in Cloud Shell][shell_img]][shell_link]

[shell_img]: http://gstatic.com/cloudssh/images/open-btn.png
[shell_link]: https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/doitintl/cassandra-bigtable&page=editor&open_in_editor=README.md

## Configuration
* Project ID: PROJECT_ID=<YOUR_PROJECT_ID>
* Temp bucket: TEMP_BUCKET=<YOUR_TEMP_BUCKET>
* Number of workers to run when executing the pipeline: NUM_OF_WORKERS=<NUM_OF_WORKERS>
* The project ID where BigTable exist: BIGTABLE_PROJECT_ID=<PROJECT_ID>
* BigTable's instance ID: BIGTABLE_INSTANCE_ID=<INSTANCE_ID>
* Table's ID: BIGTABLE_TABLE_ID=<TABLE_ID>
* Cassandra server's comma separated IP addresses list: CASSANDRA_HOSTS_LIST=<HOST1,HOST2,...>
* Cassandra server's port. Default 9042: CASSANDRA_PORT=<Cassandra's port>
* Cassandra keyspace name: CASSANDRA_KEYSPACE=<KEYSPACE>
* Cassandra table name: CASSANDRA_TABLE<TABLE_NAME>

All bucket names should contain only the name. No 'gs://' prefix.

## Running
### Running locally
```
make run_local PROJECT_ID=my-playground TEMP_BUCKET=cassandra2bigtable-bucket NUM_OF_WORKERS=2 BIGTABLE_PROJECT_ID=my-playground BIGTABLE_INSTANCE_ID=bt_instance BIGTABLE_TABLE_ID=my_table CASSANDRA_HOSTS_LIST="1.1.1.1,2.2.2.2,3.3.3.3" CASSANDRA_PORT=9042 CASSANDRA_KEYSPACE=my_ks CASSANDRA_TABLE=bigtable

```

### Running on Google's Dataflow
```
make run PROJECT_ID=my-playground TEMP_BUCKET=cassandra2bigtable-bucket NUM_OF_WORKERS=2 BIGTABLE_PROJECT_ID=my-playground BIGTABLE_INSTANCE_ID=bt_instance BIGTABLE_TABLE_ID=my_table CASSANDRA_HOSTS_LIST="1.1.1.1,2.2.2.2,3.3.3.3" CASSANDRA_PORT=9042 CASSANDRA_KEYSPACE=my_ks CASSANDRA_TABLE=bigtable

```

## Adding the tables
The process your won table, you'll need to create a new POJO and add it to the pipeline.

### Adding a new POJO
> CassandraIO provides a source to read and returns a bounded collection of entities as PCollection<Entity>. An entity is built by Cassandra mapper (com.datastax.driver.mapping.EntityMapper) based on a POJO containing annotations (as described http://docs.datastax.com/en/developer/java-driver/2.1/manual/object_mapper/creating/").  

In this project I have defined BaseRow as a base class. This class is suppose to give the basic transformations utilities.  
To create your own POJO, you will need to mimic Metric.java. This includes:  
* Extend BaseRow.java
* Define your table
* Define your column
* Implement createBigTableRow(...)

Once you have your POJO, you can change CassandraToBigtable and get CassandraIO to bring your new class.
