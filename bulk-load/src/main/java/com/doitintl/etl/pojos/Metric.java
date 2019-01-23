package com.doitintl.etl.pojos;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

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

@Table(keyspace = "case_ks", name = "metrics")
@Data
public class Metric implements Serializable {
    @PartitionKey
    @Column(name = "server_ip")
    String server_ip;

    @Column(name = "sample_time")
    Timestamp sample_time;

    @Column(name = "cpu_usage")
    Float cpu_usage;

    @Column(name = "cpu_load")
    Float cpu_load;

    @Column(name = "mem_usage")
    Integer mem_usage;

    @Column(name = "processes")
    Integer processes;
}
