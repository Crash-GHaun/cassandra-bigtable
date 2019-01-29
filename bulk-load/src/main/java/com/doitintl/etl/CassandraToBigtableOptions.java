package com.doitintl.etl;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Validation;

public interface CassandraToBigtableOptions extends DataflowPipelineOptions {
	@Description("The project that contains the table to import into.")
	@Validation.Required
	String getBigtableProjectId();
	@SuppressWarnings("unused")
	void setBigtableProjectId(String projectId);

	@Description("The Bigtable instance id that contains the table to import into.")
	@Validation.Required
	String getBigtableInstanceId();
	@SuppressWarnings("unused")
	void setBigtableInstanceId(String instanceId);

	@Description("The Bigtable table id to import into.")
	@Validation.Required
	String getBigtableTableId();
	@SuppressWarnings("unused")
	void setBigtableTableId(String tableId);

	@Description("Specify the hosts of the Apache Cassandra instances")
	@Validation.Required
	String[] getCassandraHostsList();
	@SuppressWarnings("unused")
	void setCassandraHostsList(String[] cassandraHostsList);

	@Description("Specify the port number of the Apache Cassandra instances")
	@Default.Integer(9042)
	int getCassandraPort();
	@SuppressWarnings("unused")
	void setCassandraPort(int cassandraPort);

	@Description("Specify the Cassandra keyspace where to read data")
	@Validation.Required
	String getCassandraKeyspace();
	@SuppressWarnings("unused")
	void setCassandraKeyspace(String cassandraKeyspace);

	@Description("Specify the Cassandra table where to read data.")
	@Validation.Required
	String getCassandraTable();
	@SuppressWarnings("unused")
	void setCassandraTable(String cassandraTable);
}
