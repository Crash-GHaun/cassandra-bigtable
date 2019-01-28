# Demo App
Running simulation to insert rows into Cassandra and Bigtable:

## Steps to run simulation
(1) Make sure you use `python 3.6` or `3.7` and install dependencies. 
preferably use a dedicated virtualenv.  

  ``` pip install -r requirements.txt```  

(2) If you don't already have a Cassandra cluster you can choose a preinstalled 
   solution from Google Marketplace.    
This code was tested using a [Bitnami solution available on Google marketplace](https://console.cloud.google.com/marketplace/details/bitnami-launchpad/cassandra)  

(3) This code uses the simplest Cassandra remote authorization scheme 
 so make sure the following configuration properties are set in your cassandra.yaml  
 ```
 authorizer: AllowAllAuthorize
 authenticator: AllowAllAuthenticator
 start_rpc: true
 rpc_address: 0.0.0.0
 broadcast_rpc_address: XXX.XXX.XXX.XXX    <- Host ip address
```  
 If you use the marketplace solution linked above the yaml file location is
 
```/opt/bitnami/cassandra/conf/cassandra.yaml```  

 If you make changes to the yaml file restart the cluster. 
 If you use the marketplace solution linked above use
 
```sudo /opt/bitnami/ctlscript.sh restart```
 
 
(4) Make sure port 9042 is open on host.    
 If you use the marketplace solution linked above add a firewall rule
 from https://console.cloud.google.com/networking/firewalls
 
(5) Create keyspace in Cassandra or use an existing one and create 
  the *metrics* table under it. You can use cqlsh to run the commsands in:  
```demo_app/scripts/cql/create_metrics_table.cql```

(6) Make sure you have bigtable instance up and running on your GCP project.  
  The code uses the default authentication to connect to Bigtable.  
  see https://googleapis.github.io/google-cloud-python/latest/core/auth.html  

(7) Create a bigtable *metrics* table and *metric* column family using cbt.   
  see: ```demo_app/scripts/cbt/create_metrics_table.sh```  


(8) Run the simulation using the followinf command. If you want to stream data to bigtable remove ```--bt_omit```
``` 
python simulation.py --servers=<number of servers to simulate>
--cassandra_host=<host ip> 
--cassandra_db=<keyspace>
--bt_project_id=<google project id>
--bt_instance_id=<bigtable instance id>
--bt-omit
```

(9) Test data is being inserted to cassandra by using the following sql from cqlsh:

 ```select count(*) from \<keyspace\>.metrics;```  
 
(10) Test data is being inserted to Bigtable by using the following cbt command

 ```cbt count metrics```
    
    
