import datetime
import ipaddress
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

    def insert_row_cassandra(self, cass_session):
        cass_session.execute(CQL, self.__dict__)

    def insert_row_bigtable(self, bt_table):
        row_key = '{server}@{ts}'.format(server=self.server_ip,
                                         ts=str(int(self.sample_time.timestamp() * 1e6)))

        row = bt_table.row(row_key)
        for column, value in self.__dict__.items():
            row.set_cell(COLUMN_FAMILY,
                        column,
                        str(value).encode(),
                        timestamp=datetime.datetime.utcnow())

        #bt_table.mutate_rows([row])
        row.commit()



