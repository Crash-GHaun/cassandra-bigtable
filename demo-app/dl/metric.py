import datetime
import ipaddress
import random


def create_metric(server_id):
    metric = dict(server_ip=ipaddress.IPv4Address(server_id % 2**32),  # mock ip address
                  sample_time=datetime.datetime.utcnow(),
                  cpu_usage=random.uniform(0, 100),
                  cpu_load=random.uniform(0.0, 3.5),
                  mem_usage=random.randrange(0, 4 * 1024),
                  processes=random.randrange(0, 1001))
    return metric


def insert_row_cassandra(metric, cass_session):
    cass_session.execute(
        """
        INSERT INTO metrics (server_ip, sample_time, cpu_usage, cpu_load, mem_usage, processes)
        VALUES (%(server_ip)s, %(sample_time)s, %(cpu_usage)s, %(cpu_load)s, %(mem_usage)s, %(processes)s)
        """,
        metric
    )


def insert_row_bigtable(metric, bt_table, column_family_id):
    row_key = '{server}@{ts}'.format(server=metric['server_ip'],
                                     ts=str(int(metric['sample_time'].timestamp() * 1e6)))

    row = bt_table.row(row_key)
    for column, value in metric.items():
        row.set_cell(column_family_id,
                     column,
                     str(value).encode(),
                     timestamp=datetime.datetime.utcnow())

    #bt_table.mutate_rows([row])
    row.commit()
