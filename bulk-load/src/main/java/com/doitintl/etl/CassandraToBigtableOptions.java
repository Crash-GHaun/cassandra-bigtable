package com.doitintl.etl;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Description;

public interface CassandraToBigtableOptions extends DataflowPipelineOptions {
	@Description("The project that contains the table to import into.")
	String getBigtableProjectId();
	@SuppressWarnings("unused")
	void setBigtableProjectId(String projectId);

	@Description("The Bigtable instance id that contains the table to import into.")
	String getBigtableInstanceId();
	@SuppressWarnings("unused")
	void setBigtableInstanceId(String instanceId);

	@Description("The Bigtable table id to import into.")
	String getBigtableTableId();
	@SuppressWarnings("unused")
	void setBigtableTableId(String tableId);

	@Description("Specify the hosts of the Apache Cassandra instances")
	String[] getCassandraHostsList();
	@SuppressWarnings("unused")
	void setCassandraHostsList(String[] cassandraHostsList);

	@Description("Specify the port number of the Apache Cassandra instances")
	int getCassandraPort();
	@SuppressWarnings("unused")
	void setCassandraPort(int cassandraPort);

	@Description("Specify the Cassandra keyspace where to read data")
	String getCassandraKeyspace();
	@SuppressWarnings("unused")
	void setCassandraKeyspace(String cassandraKeyspace);

	@Description("Specify the Cassandra table where to read data.")
	String getCassandraTable();
	@SuppressWarnings("unused")
	void setCassandraTable(String cassandraTable);
}
