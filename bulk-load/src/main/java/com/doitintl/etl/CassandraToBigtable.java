package com.doitintl.etl;

import com.doitintl.etl.pojos.Tweet;
import com.google.cloud.bigtable.beam.CloudBigtableIO;
import com.google.cloud.bigtable.beam.CloudBigtableTableConfiguration;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
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
	private static final byte[] FAMILY = Bytes.toBytes("cf1");

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

		PipelineResult result = runPipeline(options);
	}

	private static PipelineResult runPipeline(CassandraToBigtableOptions options){
		Pipeline pipeline = Pipeline.create(options);

		CloudBigtableTableConfiguration peopleConfig =
				new CloudBigtableTableConfiguration.Builder()
						.withProjectId(options.getBigtableProjectId())
						.withInstanceId(options.getBigtableInstanceId())
						.withTableId(options.getBigtableTableId())
						.build();

		pipeline
				.apply("Read from Cassandra",
						CassandraIO
								.<Tweet>read()
								.withHosts(Arrays.asList(options.getCassandraHostsList()))
								.withPort(options.getCassandraPort())
								.withKeyspace(options.getCassandraKeyspace())
								.withTable(options.getCassandraTable())
								.withEntity(Tweet.class)
								.withCoder(SerializableCoder.of(Tweet.class))
				)
				.apply("Transform to Bigtable",
						MapElements
								.via(new CassandraTweetsByDateToBigTableFn()))
				.apply("Write to Bigtable",
						CloudBigtableIO
								.writeToTable(peopleConfig))
				;

		return pipeline.run();
	}

	static class CassandraTweetsByDateToBigTableFn extends SimpleFunction<Tweet, Mutation>{
		private static final long serialVersionUID = -654811021949199644L;

		@Override
		public Mutation apply(Tweet row){
			Mutation mutation = new Put(Bytes.toBytes(row.getId()));

			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("user_id"), Bytes.toBytes(row.getUserId()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("created_at"), Bytes.toBytes(row.getCreatedAt().getNanos()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("tweet_text"), Bytes.toBytes(row.getTweetText()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("hashtag_entities"), Bytes.toBytes(row.getHashtagEntities()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("url_entities"), Bytes.toBytes(row.getUrlEntities()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("favorites_count"), Bytes.toBytes(row.getFavoritesCount()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("retweet_count"), Bytes.toBytes(row.getRetweetCount()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("quoted_status_id"), Bytes.toBytes(row.getQuotedStatusId()));
			((Put) mutation).addColumn(FAMILY, Bytes.toBytes("in_reply_to_status_id"), Bytes.toBytes(row.getInReplyToStatusId()));

			return mutation;
		}
	}
}
