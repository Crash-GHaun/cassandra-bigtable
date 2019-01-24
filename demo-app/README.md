#Demo App
Running simulation to insert rows into Cassandra and Bigtable:


`python simulation.py --servers=<number of servers to simulate>
--cassandra_host=<host ip> 
--cassandra_db=<cass db>
--bt_project_id=<google project id>
--bt_instance_id=<bigtable instance id>
--bt-omit`

- Test data is being inserted to cassandra by using the following sql from cqlsh:

    `select count(*) from \<keyspace\>.metrics;`
- Test data is being inserted to Bigtable by using the following cbt command

    `cbt count metrics`
    
    
Note: The code was tested using python3.7 