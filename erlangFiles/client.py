import logging
import sys
from time import sleep

import term
from term import Atom
from pyrlang.node import Node
from pyrlang.process import Process
from colors import color
from typing import Callable, Dict, List, Optional, Tuple
import numpy as np

LOG = logging.getLogger(color("EXAMPLE2", fg='lime'))
logging.getLogger("").setLevel(logging.DEBUG)

FROBENIUS_NORM = 'fro'

global p

def numba_norm(u: np.ndarray, v: np.ndarray):
    return np.linalg.norm(u - v)

class CMeansFederatedClient:
    global p
    def initialize(self, params: Dict) -> None:
        self.__dataset = params['dataset']
        self.__num_features = len(self.__dataset[0])
        self.__classes = [-1] * len(self.__dataset)
        self.__distance_fn = params['distance_fn']

    def evaluate_cluster_assignment(self, centers: List) -> List[Tuple]:
        # (1). some initialization
        num_clusters = len(centers)
        dataset = self.__dataset
        num_features = self.__num_features
        classes = self.__classes
        num_objects = len(dataset)
        get_label = self.__get_label
        nc_list = [0] * num_clusters
        lsc_list = [np.array([0.0] * num_features) for i in range(num_clusters)]
        # plt.figure()
        # plt.scatter(dataset[:,0],dataset[:,1])
        # plt.xlim([0,1])
        # plt.ylim([0,1])
        # (2) Updating the class value for each object in the dataset
        for i in range(num_objects):
            obj = dataset[i]
            label = get_label(obj, centers)
            classes[i] = label
            # updating stats for each cluster
            nc_list[label] = nc_list[label] + 1
            lsc_list[label] = lsc_list[label] + obj

        # print('ALERT'*sum([nc_list[i]<2 for i in range(num_clusters)]))
        # (3) Preparing data to return
        to_return = [[lsc_list[i].tolist(), nc_list[i]] for i in range(num_clusters)]
        return to_return

    def finalize(self) -> None:
        pass

    def __get_label(self, obj_data: np.array, centers: List[np.array]):
        max_value = 2 ** 64
        num_clusters = len(centers)
        distance_fn = self.__distance_fn
        label_idx = -1

        for i in range(num_clusters):
            center = centers[i]
            distance = distance_fn(np.array(obj_data), np.array(center))
            if (distance < max_value):
                label_idx = i
                max_value = distance

        return label_idx

        def getPid():
            return p.getPid()


def run_round(parameters):
    client_dataset = parameters[0]
    centers = parameters[1]
    params = {
        'dataset': client_dataset,
        'distance_fn': numba_norm
    }
    client = CMeansFederatedClient()
    client.initialize(params)
    result = client.evaluate_cluster_assignment(centers)
    client.finalize()
    return result

def getPid(var):
    return var

pid = "cia"
class MyProcess(Process):
    def __init__(self) -> None:
        super().__init__()
        self.get_node().register_name(self, Atom('pyrlang'))
        LOG.info("Registered as pyrlang")
        f = open("demofiled.txt", "a+")
        f.write(str(self.pid_))
        f.close()

    def handle_one_inbox_message(self, msg):
        LOG.info("Incoming %s", msg)

    # Prova 1 invio Pid
    def sendPid(self, Node):
        event_loop = Node.get_loop()
        global pid
        pid = "ciao"
        f = open("demofile2.txt", "a+")
        f.write(sys.argv[2])
        f.write(sys.argv[3])
        f.close()
        sleep(2)
        def task():
            Node.send_nowait(sender=self.pid_,
                          receiver=remote_receiver_name(sys.argv[2], sys.argv[3]),
                          message=(Atom("pid"), self.pid_))

        event_loop.call_soon(task)


async def sendPid(node):
    pid = node.register_new_process()
    await node.send(sender=pid,
                        receiver=(Atom(sys.argv[2]), Atom(sys.argv[3])),
                        message=Atom(pid))
    LOG.info("sendPid: Done")

def main():
    global p
    n = Node(node_name=sys.argv[1]+"@localhost", cookie="COOKIE")
    p = MyProcess()
    ev = n.get_loop()
    ev.create_task(sendPid(n))
    n.run()
    #pid = get_pid()
    #f = open("demofiled.txt", "a+")
    #f.write(str(pid))
    #f.close()
    #p.sendPid(n)
    #n.run()

if __name__ == "__main__":
    main()
