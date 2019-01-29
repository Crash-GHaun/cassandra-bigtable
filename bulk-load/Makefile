PROJECT_ID = moti-david-playground
TEMP_BUCKET = cassandra2bigtable-df
NUM_OF_WORKERS = 2
BIGTABLE_PROJECT_ID = moti-david-playground
BIGTABLE_INSTANCE_ID = case-ks
BIGTABLE_TABLE_ID = metrics
CASSANDRA_HOSTS_LIST = "35.224.119.160"
CASSANDRA_PORT = 9042
CASSANDRA_KEYSPACE = case_ks
CASSANDRA_TABLE = metrics

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
