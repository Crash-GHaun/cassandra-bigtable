#Demo App
Running simulation to insert rows into cassandra:

1) Make sure locust is installed in your environment.
2) Run simlulation process using command:
   <br><i>locust -f src/simulation.py</i>
3) Open browser at: http://127.0.0.1:8089/
4) Start simulation by choosing 
   <br>Number of users to simulate = number of servers to simulate. 
   <br> Hatch rate = 0 to spawn all
  'servers' imediately.
   <br> and press <i>start</i> 
5) Test data is being inserted by using the following sql from cqlsh
    <br><i>select count(*) from case_ks.metrics;</i>