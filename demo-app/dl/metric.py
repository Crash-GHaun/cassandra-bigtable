from cassandra.cluster import Cluster
import datetime
import ipaddress
import random

CASSANDRA_ENDPOINT = '35.229.73.51'
CASSANDRA_KS = 'case_ks'

cluster = Cluster([CASSANDRA_ENDPOINT])
session = cluster.connect(CASSANDRA_KS)


def create_row(server_id):
    row = dict(server_ip=ipaddress.IPv4Address(server_id % 2**32),  # mock ip address
               sample_time=datetime.datetime.utcnow(),
               cpu_usage=random.uniform(0, 100),
               cpu_load=random.uniform(0.0, 3.5),
               mem_usage=random.randrange(0, 4 * 1024),
               processes=random.randrange(0, 1001))
    return row


def insert_row_cassandra(row):
    session.execute(
        """
        INSERT INTO metrics (server_ip, sample_time, cpu_usage, cpu_load, mem_usage, processes)
        VALUES (%(server_ip)s, %(sample_time)s, %(cpu_usage)s, %(cpu_load)s, %(mem_usage)s, %(processes)s)
        """,
        row
    )


if __name__ == "__main__":
    row = create_row(1)
    insert_row_cassandra(row)
