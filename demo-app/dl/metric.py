
from cassandra.cluster import Cluster
from google.cloud import bigtable
from google.cloud.bigtable import column_family
from google.cloud.bigtable import row_filters

import datetime
import ipaddress
import random

CASSANDRA_ENDPOINT = '104.196.142.225'
CASSANDRA_KS = 'case_ks'
BT_PROJECT_ID = 'moti-david-playground'
BT_INSTANCE_ID = 'tweebeetee'
BT_TABLE = 'metrics'
BT_COLUMN_FAMILY = 'metric'

# Cassandra connection
cluster = Cluster([CASSANDRA_ENDPOINT])
session = cluster.connect(CASSANDRA_KS)
# Bigtable connection
bt_client = bigtable.Client(project=BT_PROJECT_ID, admin=True)
bt_instance = bt_client.instance(BT_INSTANCE_ID)

table = bt_instance.table(BT_TABLE)
max_versions_rule = column_family.MaxVersionsGCRule(2)
column_family_id = BT_COLUMN_FAMILY
column_families = {column_family_id: max_versions_rule}

# Bigtable cache
bt_cache_count = [0]
bt_cache_rows = [[]]


def create_metric(server_id):
    metric = dict(server_ip=ipaddress.IPv4Address(server_id % 2**32),  # mock ip address
               sample_time=datetime.datetime.utcnow(),
               cpu_usage=random.uniform(0, 100),
               cpu_load=random.uniform(0.0, 3.5),
               mem_usage=random.randrange(0, 4 * 1024),
               processes=random.randrange(0, 1001))
    return metric


def insert_row_cassandra(metric):
    session.execute(
        """
        INSERT INTO metrics (server_ip, sample_time, cpu_usage, cpu_load, mem_usage, processes)
        VALUES (%(server_ip)s, %(sample_time)s, %(cpu_usage)s, %(cpu_load)s, %(mem_usage)s, %(processes)s)
        """,
        metric
    )


def insert_row_bigtable(metric):
    row_key = '{server}@{ts}'.format(server=metric['server_ip'],
                                     ts=str(int(metric['sample_time'].timestamp() * 1e6)))

    row = table.row(row_key)
    for column, value in metric.items():
        row.set_cell(column_family_id,
                     column,
                     str(value).encode(),
                     timestamp=datetime.datetime.utcnow())

    table.mutate_rows([row])
    #row.commit()


if __name__ == "__main__":
    metric = create_metric(1)
    insert_row_bigtable(metric)

