from dl import metric as metric_api

from cassandra.cluster import Cluster
from google.cloud import bigtable

import argparse
import random
import threading
import time


def emit_metrics(server, cass_session, bt_table, column_family_id):
    count = 0
    while True:
        count += 1
        metric = metric_api.create_metric(server)
        metric_api.insert_row_cassandra(metric, cass_session)
        metric_api.insert_row_bigtable(metric, bt_table, column_family_id)
        print('Server {} emitted total {} metric'.format(server, count))
        time.sleep(random.randrange(5, 9))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.ArgumentDefaultsHelpFormatter)

    parser.add_argument(
        '--servers',
        type=int,
        help='Number of servers to simulate',
        default=1)
    parser.add_argument(
        '--cassandra_host',
        help='Cassandra endpoint',
        required=True)
    parser.add_argument(
        '--cassandra_db',
        help='Cassandra keyspace',
        required=True)
    parser.add_argument(
        '--bt_project_id',
        help='Bigtable project id',
        required=True)
    parser.add_argument(
        '--bt_instance_id',
        help='Bigtable instance id',
        required=True)
    parser.add_argument(
        '--bt_table',
        help='Bigtable table name',
        required=True)
    parser.add_argument(
        '--bt_column_family',
        help='Bigtable column family',
        required=True)

    args = parser.parse_args()

    # Cassandra connection
    cass_cluster = Cluster([args.cassandra_host])
    cass_session = cass_cluster.connect(args.cassandra_db)
    # Bigtable connection
    bt_client = bigtable.Client(project=args.bt_project_id, admin=True)
    bt_instance = bt_client.instance(args.bt_instance_id)
    bt_table = bt_instance.table(args.bt_table)
    column_family_id = args.bt_column_family

    num_of_servers = args.servers

    servers = [random.randrange(0, 2**32) for _ in range(num_of_servers)]
    print('=bringing up servers {}'.format(servers))
    for server in servers:
        thread = threading.Thread(target=emit_metrics, args=(server, cass_session, bt_table, column_family_id))
        thread.start()

