package com.doitintl.etl.pojos;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;

import java.io.Serializable;
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
public class Metric implements Serializable {
	private static final long serialVersionUID = -4539019641852859948L;
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
}
