import logging
import sys
from time import sleep

from term import Atom
from pyrlang.node import Node
from pyrlang.process import Process
from colors import color
from typing import Dict, List, Tuple
import numpy as np

LOG = logging.getLogger(color("LOGGER client.py", fg='lime'))
logging.getLogger("").setLevel(logging.DEBUG)

def numba_norm(u: np.ndarray, v: np.ndarray):
    return np.linalg.norm(u - v)

class CMeansFederatedClient:
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
        # (2) Updating the class value for each object in the dataset
        for i in range(num_objects):
            obj = dataset[i]
            label = get_label(obj, centers)
            classes[i] = label
            # updating stats for each cluster
            nc_list[label] = nc_list[label] + 1
            lsc_list[label] = lsc_list[label] + obj

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

class MyProcess(Process):
    def __init__(self) -> None:
        super().__init__()
        self.get_node().register_name(self, Atom('pyrlang'))

    def handle_one_inbox_message(self, msg):
        LOG.info("Incoming %s", msg)

def main():
    n = Node(node_name=sys.argv[1]+"@localhost", cookie="COOKIE")
    n.run()

if __name__ == "__main__":
    main()
