from cassandra.cluster import Cluster
from google.cloud import bigtable
from google.cloud.bigtable import column_family

import datetime
import ipaddress
import time
import random

TABLE_NAME = 'metrics'
COLUMN_FAMILY = 'metric'


CQL_CREATE_KEYSPACE = "CREATE KEYSPACE IF NOT EXISTS {} "
CQL_REPLICATION_CLAUSE = " WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '2' }"

CQL_CREATE_TABLE = """
    CREATE TABLE IF NOT EXISTS {}.{} (
        server_ip text,
        sample_time timestamp,
        cpu_usage float,
        cpu_load float,
        mem_usage int,
        processes int,
    PRIMARY KEY ((server_ip), sample_time) )
    WITH CLUSTERING ORDER BY (sample_time DESC);
"""

CQL_INSERT_ROW = """
    INSERT INTO {} (server_ip, sample_time, cpu_usage, cpu_load, mem_usage, processes)
    VALUES (%(server_ip)s, %(sample_time)s, %(cpu_usage)s, %(cpu_load)s, %(mem_usage)s, %(processes)s)
""".format(TABLE_NAME)


class Metric:
    def __init__(self, server_id):
        self.server_ip = ipaddress.IPv4Address(server_id % 2 ** 32)
        self.sample_time = datetime.datetime.utcnow()
        self.cpu_usage = random.uniform(0, 100)
        self.cpu_load = random.uniform(0.0, 3.5)
        self.mem_usage = random.randrange(0, 4 * 1024)
        self.processes = random.randrange(0, 1001)


class CassandraMetric:
    def __init__(self, host, keyspace):
        cluster = Cluster([host])

        self.session = cluster.connect()
        self.setup(keyspace)

    def setup(self, keyspace):
        try:
            print("Creating Cassandra keyspace if not exists already...")
            self.session.execute(CQL_CREATE_KEYSPACE.format(keyspace) + CQL_REPLICATION_CLAUSE)

            print("Setting Cassandra keyspace...")
            self.session.set_keyspace(keyspace)

            print("Creating Cassandra metrics table if not exists...")
            self.session.execute(CQL_CREATE_TABLE.format(keyspace, TABLE_NAME))
        except Exception:
            print('Error in setting up Cassandra environment')
            raise

    def insert_row(self, metric):
        try:
            self.session.execute(CQL_INSERT_ROW, metric.__dict__)
        except Exception:
            print('Exception occurred while inserting row into Cassandra')
            raise

    def full_table_scan(self):
        query = "SELECT * FROM {}".format(TABLE_NAME)
        rs = self.session.execute(query)
        count = 0
        while rs.has_more_pages:
            try:
                rows = rs.current_rows
                count += len(rows)
                # Try fetching next page
                rs.fetch_next_page()
            except Exception:
                # In case of Time out exception or if some mishappening occurs
                # Retry after 1 second
                time.sleep(1)
                print("Exception occurred while fetching page from Cassandra, retrying again with same page")
                continue
        count += len(rs.current_rows)
        return count


class BigtableMetric:
    def __init__(self, project_id, instance_id):
            bt_client = bigtable.Client(project=project_id, admin=True)
            self.bt_instance = bt_client.instance(instance_id)
            self.bt_table = self.bt_instance.table(TABLE_NAME)
            self.setup()

    def setup(self):
        try:
            print('Creating Bigtable column family with Max Version GC rule...')
            max_versions_rule = column_family.MaxVersionsGCRule(2)
            column_family_id = COLUMN_FAMILY
            column_families = {column_family_id: max_versions_rule}
            print("Creating Bigtable metrics table if not exists...")
            if not self.bt_table.exists():
                self.bt_table.create(column_families=column_families)
        except Exception:
            print('Exception occurred while inserting row into Cassandra')
            raise

    def insert_row(self, metric):
        row_key = '{server}@{ts}'.format(server=metric.server_ip,
                                         ts=str(int(metric.sample_time.timestamp() * 1e6)))

        row = self.bt_table.row(row_key)
        for column, value in metric.__dict__.items():
            row.set_cell(COLUMN_FAMILY,
                         column,
                         str(value).encode(),
                         timestamp=datetime.datetime.utcnow())
        try:
            row.commit()
        except Exception:
            print('Exception occurred while inserting row into Bigtable')
            raise

    def full_table_scan(self):
        count = 0
        try:
            partial_rows = self.bt_table.read_rows()
        except Exception:
            print("Exception occurred while fetching rows from Bigtable")
            raise

        for row in partial_rows:
            row_data = {key.decode(): row.cells[COLUMN_FAMILY][key][len(row.cells[COLUMN_FAMILY][key]) - 1].
                        value.decode() for key in row.cells[COLUMN_FAMILY]}

            count += 1
        return count

    def get_server_metrics(self):
        partial_rows = self.bt_table.read_rows()
        i = 0
        for row in partial_rows:
            i += 1
            if i == 100:
                break
            print(row.row_key)
            print({k: v[0].value for k, v in row.to_dict().items()})

