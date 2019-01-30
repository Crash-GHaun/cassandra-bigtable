package com.doitintl.etl.pojos;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.bigtable.v2.Mutation;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.beam.sdk.values.KV;

import java.text.SimpleDateFormat;
import java.util.Date;

/**CREATE TABLE case_ks.metrics (
        server_ip text,
        sample_time timestamp,
        cpu_usage float,
        cpu_load float,
        mem_usage int,
        processes int,
        PRIMARY KEY ((server_ip), sample_time) )
        WITH CLUSTERING ORDER BY (sample_time DESC);
 */

@Table(name = "metrics")
@Data
@EqualsAndHashCode(callSuper=true)
public class Metric extends BaseRow {
	private static final long serialVersionUID = -4539019641852859948L;

	private static final String FAMILY = "metric";

	@PartitionKey
	@Column(name = "server_ip")
	String server_ip;

	@ClusteringColumn
	@Column(name = "sample_time")
	Date sample_time;

	@Column(name = "cpu_usage")
	Float cpu_usage;

	@Column(name = "cpu_load")
	Float cpu_load;

	@Column(name = "mem_usage")
	Integer mem_usage;

	@Column(name = "processes")
	Integer processes;

	public Date cellTimeStamp(){
		return this.sample_time;
	}

	public KV<ByteString, Iterable<Mutation>> createBigTableRow(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = formatter.format(this.getSample_time());

		ByteString key = ByteString.copyFromUtf8(this.getServer_ip()+"#"+date);
		// BulkMutation doesn't split rows. Currently, if a single row contains more than 100,000
		// mutations, the service will fail the request.
		ImmutableList.Builder<Mutation> mutations = ImmutableList.builder();


		mutations.add(getMutation(FAMILY,"server_ip", this.getServer_ip()));
		mutations.add(getMutation(FAMILY,"sample_time", this.getSample_time().toString()));
		mutations.add(getMutation(FAMILY,"cpu_usage", this.getCpu_usage().toString()));
		mutations.add(getMutation(FAMILY,"cpu_load", this.getCpu_load().toString()));
		mutations.add(getMutation(FAMILY,"mem_usage", this.getMem_usage().toString()));
		mutations.add(getMutation(FAMILY,"processes", this.getProcesses().toString()));

		return KV.of(key, mutations.build());
	}

}
