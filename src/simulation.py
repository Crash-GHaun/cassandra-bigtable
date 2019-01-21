from locust import Locust, TaskSet, task

from dl import metric


class ServerBehavior(TaskSet):

    @task
    def sample(self):
        metric.insert_row_cassandra(metric.create_row(id(self)))
        print('id {}'.format(id(self)))


class Server(Locust):
    task_set = ServerBehavior
    min_wait = 5000
    max_wait = 9000

