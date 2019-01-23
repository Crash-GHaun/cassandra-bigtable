#Demo App
Running simulation to insert rows into Cassandra and Bigtable:

<i>python simulation.py --servers=5 --cassandra_host=<host ip> 
  --cassandra_db=\<cass db\> 
  --bt_project_id=\<google project id\> 
  --bt_instance_id=\<bigtable instance id\></i>

- Test data is being inserted to cassandra by using the following sql from cqlsh
    <br><i>select count(*) from \<keyspace\>.metrics;</i>
- Test data is being inserted to Bigtable by using the following cbt command
    <br><i>cbt count metrics</i>
