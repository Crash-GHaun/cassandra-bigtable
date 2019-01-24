from cassandra.cluster import Cluster
from google.cloud import bigtable

import datetime
import ipaddress
import time
import random

TABLE_NAME = 'metrics'
COLUMN_FAMILY = 'metric'
CQL = """
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
    def __init__(self, host, db):
        cluster = Cluster([host])
        self.session = cluster.connect(db)

    def insert_row(self, metric):
        self.session.execute(CQL, metric.__dict__)

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
            except:
                # In case of Time out exception or if some mishappening occurs
                # Retry after 500 milliseconds
                time.sleep(.5)
                print("Exception Occurred while fetching page, retrying again with same page")
                continue
        count += len(rs.current_rows)
        return count


class BigtableMetric:
    def __init__(self, project_id, instance_id):
        bt_client = bigtable.Client(project=project_id, admin=True)
        self.bt_instance = bt_client.instance(instance_id)
        self.bt_table = self.bt_instance.table(TABLE_NAME)

    def insert_row(self, metric):
        row_key = '{server}@{ts}'.format(server=metric.server_ip,
                                         ts=str(int(metric.sample_time.timestamp() * 1e6)))

        row = self.bt_table.row(row_key)
        for column, value in metric.__dict__.items():
            row.set_cell(COLUMN_FAMILY,
                         column,
                         str(value).encode(),
                         timestamp=datetime.datetime.utcnow())

        #bt_table.mutate_rows([row])
        row.commit()


