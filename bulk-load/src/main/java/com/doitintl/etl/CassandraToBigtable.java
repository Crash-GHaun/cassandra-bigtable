package com.doitintl.etl;

import com.doitintl.etl.pojos.Metric;
import com.google.cloud.bigtable.beam.CloudBigtableIO;
import com.google.cloud.bigtable.beam.CloudBigtableTableConfiguration;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Arrays;

/**
 * Dataflow pipeline that imports data from Cassandra cluster to a Cloud Bigtable table. The Cloud
 * Bigtable table must be created before running the pipeline and must have a compatible table
 * schema.
 */
public class CassandraToBigtable {
	private static final byte[] FAMILY = Bytes.toBytes("metric");

	/**
	 * Runs a pipeline to import Cassandra table to a Cloud Bigtable table.
	 *
	 * @param args arguments to the pipeline
	 */
	public static void main(String[] args){
		CassandraToBigtableOptions options = PipelineOptionsFactory
				.fromArgs(args)
				.withValidation()
				.as(CassandraToBigtableOptions.class);

		Pipeline pipeline = buildPipeline(options);

		pipeline.run();
	}

	private static Pipeline buildPipeline(CassandraToBigtableOptions options){
		Pipeline pipeline = Pipeline.create(options);

		CloudBigtableTableConfiguration case_ksTable =
				new CloudBigtableTableConfiguration.Builder()
						.withProjectId(options.getBigtableProjectId())
						.withInstanceId(options.getBigtableInstanceId())
						.withTableId(options.getBigtableTableId())
						.build();

		pipeline
				.apply("Read from Cassandra",
						CassandraIO
								.<Metric>read()
								.withHosts(Arrays.asList(options.getCassandraHostsList()))
								.withPort(options.getCassandraPort())
								.withKeyspace(options.getCassandraKeyspace())
								.withTable(options.getCassandraTable())
								.withEntity(Metric.class)
								.withCoder(SerializableCoder.of(Metric.class))
				)
				.apply("Transform to Bigtable row",
						MapElements
								.via(new CassandraTweetsByDateToBigTableFn()))
				.apply("Write row to Bigtable",
						CloudBigtableIO
								.writeToTable(case_ksTable))
				;

		return pipeline;
	}

	static class CassandraTweetsByDateToBigTableFn extends SimpleFunction<Metric, Mutation>{
		private static final long serialVersionUID = -654811021949199644L;

		@Override
		public Mutation apply(Metric row){
			Mutation mutation = new Put(Bytes.toBytes(row.getServer_ip()));

            ((Put) mutation).addColumn(FAMILY, Bytes.toBytes("sample_time"), Bytes.toBytes(row.getSample_time().toString()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("cpu_usage"), Bytes.toBytes(row.getCpu_usage().toString()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("cpu_load"), Bytes.toBytes(row.getCpu_load().toString()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("mem_usage"), Bytes.toBytes(row.getMem_usage().toString()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("processes"), Bytes.toBytes(row.getProcesses().toString()));

			return mutation;
		}
	}
}
