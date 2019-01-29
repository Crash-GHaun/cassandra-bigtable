package com.doitintl.etl;

import com.doitintl.etl.pojos.BaseRow;
import com.doitintl.etl.pojos.Metric;
import com.google.bigtable.v2.Mutation;
import com.google.protobuf.ByteString;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.io.gcp.bigtable.BigtableIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;

import java.util.Arrays;

/**
 * Dataflow pipeline that imports data from Cassandra cluster to a Cloud Bigtable table. The Cloud
 * Bigtable table must be created before running the pipeline and must have a compatible table
 * schema.
 */
public class CassandraToBigtable {
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

		BigtableIO.Write writeToBigtable =
				BigtableIO.write()
						.withProjectId(options.getBigtableProjectId())
						.withInstanceId(options.getBigtableInstanceId())
						.withTableId(options.getBigtableTableId());

		pipeline
				.apply("Read from Cassandra",
						CassandraIO
								.<Metric>read()
								.withHosts(Arrays.asList(options.getCassandraHostsList()))
								.withPort(options.getCassandraPort())
								.withKeyspace(options.getCassandraKeyspace())
								.withTable(options.getCassandraTable())
								.withEntity(Metric.class) // A class that represent the row we want to get
								.withCoder(SerializableCoder.of(Metric.class))
				)
				.apply("Transform to Bigtable row",
						MapElements
								.via(new CassandraTweetsByDateToBigTableFn()))
				.apply("Write row to Bigtable",
						writeToBigtable)
				;

		return pipeline;
	}

	static class CassandraTweetsByDateToBigTableFn extends SimpleFunction<BaseRow, KV<ByteString, Iterable<Mutation>>>{
		private static final long serialVersionUID = -654811021949199644L;

		@Override
		public KV<ByteString, Iterable<Mutation>> apply(BaseRow row){
			return row.createBigTableRow();
		}
	}
}
