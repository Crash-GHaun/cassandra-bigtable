PROJECT_ID = comoqa
TEMP_BUCKET = cassandra2bigtablemotti
NUM_OF_WORKERS = 2
BIGTABLE_PROJECT_ID = moti-david-playground
BIGTABLE_INSTANCE_ID = tweebeetee-bt
BIGTABLE_TABLE_ID = tweets
CASSANDRA_HOSTS_LIST = "104.196.142.225"
CASSANDRA_PORT = 9042
CASSANDRA_KEYSPACE = tweebeetee
CASSANDRA_TABLE = tweets

all:
	pass

install:
	mvn install

build: install
	mvn package

run_local: build
	mvn exec:java -Dexec.mainClass=com.doitintl.etl.CassandraToBigtable \
	-Dexec.cleanupDaemonThreads=false \
	-Dexec.args=" \
	--project=$(PROJECT_ID) \
	--tempLocation=gs://$(TEMP_BUCKET)/ \
	--gcpTempLocation=gs://$(TEMP_BUCKET)/temp \
	--stagingLocation=gs://$(TEMP_BUCKET)/staging \
	--runner=DirectRunner \
	--defaultWorkerLogLevel=DEBUG \
	--bigtableProjectId=$(BIGTABLE_PROJECT_ID) \
	--bigtableInstanceId=$(BIGTABLE_INSTANCE_ID) \
	--bigtableTableId=$(BIGTABLE_TABLE_ID) \
	--cassandraHostsList=$(CASSANDRA_HOSTS_LIST) \
	--cassandraPort=$(CASSANDRA_PORT) \
	--cassandraKeyspace=$(CASSANDRA_KEYSPACE) \
	--cassandraTable=$(CASSANDRA_TABLE) \
	"

run: build
	mvn exec:java -Dexec.mainClass=com.doitintl.etl.CassandraToBigtable \
	-Dexec.cleanupDaemonThreads=false \
	-Dexec.args=" \
	--jobName=$(JOBNAME) \
	--project=$(PROJECT_ID) \
	--tempLocation=gs://$(TEMP_BUCKET)/ \
	--gcpTempLocation=gs://$(TEMP_BUCKET)/temp \
	--stagingLocation=gs://$(TEMP_BUCKET)/staging \
	--runner=DataflowRunner \
	--defaultWorkerLogLevel=DEBUG \
	--numWorkers=$(NUM_OF_WORKERS) \
	--bigtableProjectId=$(BIGTABLE_PROJECT_ID) \
	--bigtableInstanceId=$(BIGTABLE_INSTANCE_ID) \
	--bigtableTableId=$(BIGTABLE_TABLE_ID) \
	--cassandraHostsList=$(CASSANDRA_HOSTS_LIST) \
	--cassandraPort=$(CASSANDRA_PORT) \
	--cassandraKeyspace=$(CASSANDRA_KEYSPACE) \
	--cassandraTable=$(CASSANDRA_TABLE) \
	"
