CREATE KEYSPACE metric
WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor' : 3};

DROP TABLE IF EXISTS metric.metrics;

CREATE TABLE case_ks.metrics (
    server_ip text,
    sample_time timestamp,
    cpu_usage float,
    cpu_load float,
    mem_usage int,
    processes int,
PRIMARY KEY ((server_ip), sample_time) )
WITH CLUSTERING ORDER BY (sample_time DESC);


--CREATE INDEX met_by_ts ON metric.metrics (sample_time);