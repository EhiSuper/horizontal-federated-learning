import logging
from term import Atom
from pyrlang.node import Node
from pyrlang.process import Process
from colors import color
from abc import ABC, abstractmethod
from typing import Callable, Dict, List, Optional, Tuple
import numpy as np
import pandas as pd
from scipy.io import arff
import math
import random
from numba import jit
import os
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import StratifiedKFold
from sklearn.utils import shuffle as shuffleSK
from array import array

LOG = logging.getLogger(color("EXAMPLE2", fg='lime'))
logging.getLogger("").setLevel(logging.DEBUG)

FROBENIUS_NORM = 'fro'

def norm_fro(u:np.ndarray):
    return np.linalg.norm(u, ord = FROBENIUS_NORM)

def erlang_request_generate_centers(parameters):
    # parameters is a tuple, sending from Erlang {seed, n_clusters, n_features}
    seed = parameters[0]
    n_clusters = parameters[1]
    n_features = parameters[2]
    centers = generate_random_centers(seed, n_clusters, n_features)
    centers_list = centers.tolist()
    return centers_list

def erlang_request_process_clustering_results(parameters):
    # parameters is a tuple, sending from Erlang {epsilon, max_number_rounds, n_clusters, N_features, norm_fro, centers, client_responses, all_centers_list, f_norm_values, executed_rounds}
    parameters[7].reverse()
    parameters[8].reverse()
    server_params: Dict[str, bytes] = {
            'epsilon': parameters[0],
            'max_number_rounds': parameters[1],
            'num_clusters': parameters[2],
            'num_features': parameters[3],
            'norm_fm': globals()[parameters[4]],
            'centers': parameters[7],
            'fnorms': parameters[8],
            'round': parameters[9]
    }
    server = CMeansFederatedServer()
    server.initialize(server_params)
    # client_responses is already a list of lists of tuples
    server.process_clustering_results(parameters[6])
    new_centers = server.get_centers()
    finished = int(server.next_round())
    new_fnorm = server.get_new_fnorm() #? can we do it?
    print(new_fnorm)
    for i in range(len(new_centers)):
        if(type(new_centers[i]) is not list):
            new_centers[i] = new_centers[i].tolist()
    tuple_to_response = (new_centers, new_fnorm, finished)
    print(tuple_to_response)
    return tuple_to_response

def erlang_request_get_involved_clients(parameters):
    #parameters is a tuple, sending from Erlang {NClients, NMinClients, RandomClientSeed}
    x = random.sample(range(parameters[0]), parameters[1])
    print(x)
    return x

def generate_dataset_chunks(X: np.array, Y: List, n_splits: int, shuffle: bool = False, 
                            shuffle_seed:int = None, mode: str = 'iid', iid_seed: int = None):
    if (n_splits == 1):
        return [X]
    dataset_chunks = []
    if mode == 'iid':
      skf = StratifiedKFold(n_splits = n_splits, shuffle = shuffle, random_state = iid_seed)
      for train_index, test_index in skf.split(X, Y):
          dataset_chunks.append(X[test_index])

    elif mode == 'non_iid_volume':
      X, Y = shuffleSK(X,Y, random_state = shuffle_seed)

      factor = 1.2
      total = len(X)-int(np.floor(len(X)/factor))
      print(total)
      temp = []
      for i in range(n_splits-1):
        val = np.random.randint(0, total)
        temp.append(val)
        total -= val
      temp.append(total)
      nums = [z+int(np.floor(len(X)/factor/n_splits)) for z in temp]
      cumsum = list(np.cumsum(nums))
      old = [x for x in cumsum]
      cumsum.pop(-1)
      cumsum.insert(0,0)
      for s,e in zip(cumsum,old):
        dataset_chunks.append(X[s:e])
    elif mode == 'non_iid_distr':
      dataset_chunks = [X[i:i + int(np.floor(len(X)/n_splits)),:] for i in range(0, len(X), int(np.floor(len(X)/n_splits)))]
    return dataset_chunks

def generate_chunks(n_splits: int = 10, dataset: str = "https://raw.githubusercontent.com/deric/clustering-benchmark/master/src/main/resources/datasets/artificial/xclara.arff", mode: int = 1):
    df = pd.read_csv(dataset,delimiter = ',',names = ["a","b","c"],error_bad_lines = False)
    df = df[8:]
    dataset_file = 'clara'
    df['a'] = [float(x) for x in (df['a'])]
    df['b'] = [float(x) for x in (df['b'])]
    df['c'] = [float(x) for x in (df['c'])]
    Y = df.c.tolist()
    X_original = np.array(df[['a','b']])
    rows = len(X_original)
    print(f'Dataset size {rows}')
    min_max_scaler = MinMaxScaler()
    X = min_max_scaler.fit_transform(X_original)
    # Aggiusta nome dataset, colonne, righe da skippare
    if(mode == 1):
        mode = 'non_iid_distr'
        iid_seed = None
        shuffle_seed = None
    elif(mode == 2):
        mode = 'iid'
        iid_seed = 200
        shuffle_seed = None
    elif(mode == 3):
        mode = 'non_iid_volume'
        iid_seed = None
        shuffle_seed = 100
    else:
        return None
    shuffle_dataset: bool = True
    print("Going to generate chunks")
    dataset_chunks = generate_dataset_chunks(X, Y, n_splits, shuffle = shuffle_dataset, mode = mode, shuffle_seed = shuffle_seed, iid_seed = iid_seed)
    print("Chunks generated")
    print(type(dataset_chunks))
    return np.array(dataset_chunks).tolist()

def read_conf(file_path: str = "confParams.txt"):
    with open(file_path,'r') as conf:
        x = conf.read().splitlines()
    for index, elem in enumerate(x):
        elem = elem.split(": ")[1]
        x[index] = elem
    x[5] = x[5].split(",")
    x[1] = int(x[1])
    x[2] = int(x[2])
    x[3] = int(x[3])
    x[4] = float(x[4])
    x[6] = int(x[6])
    return x

def generate_random_centers(seed, n_clusters: int, n_features: int):
    np.random.seed(int(float(seed)))
    return np.random.rand(n_clusters,n_features).tolist()

class CMeansFederatedServer:

    def __init__(self):
        self.__current_round = 0

    # UPDATE: REMOVE CLUSTER'S CENTERS RANDOM INITIALIZATION
    def initialize(self, params: Dict) -> None:
        self.__epsilon = params['epsilon']
        self.__max_number_rounds = params.get('max_number_rounds', 10)
        self.__num_clusters = params['num_clusters']
        self.__num_features = params['num_features']
        self.__norm_fm = params['norm_fm']
        self.__fnorms = params['fnorms']
        self.__cluster_centers = []
        self.__current_round = params['round']
        if(self.current_round == 0):
            self.__cluster_centers.append(params['centers'])
        else:
            for i in range(len(params['centers'])):
                self.__cluster_centers.append(params['centers'][i])

    def next_round(self) -> bool:
        num_clusters = self.__num_clusters
        cluster_centers = self.__cluster_centers
        print("i centri fino ad\'ora sono:")
        print(cluster_centers)
        num_centers = len(cluster_centers)

        if (num_centers > 1):
            centers_r = np.array(cluster_centers[-1])
            centers_r_1 = np.array(cluster_centers[-2])
            print("centro 1 calcolare la norma")
            print(centers_r)
            print("centro 2 calcolare la norma")
            print(centers_r_1)
            fnorm_value = self.__norm_fm(centers_r - centers_r_1)
            self.__fnorms.append(fnorm_value)
            if (fnorm_value < self.__epsilon):
                return False

        result = self.__current_round < self.__max_number_rounds

        self.__current_round = self.__current_round + 1 if result else 0
        return result

    def process_clustering_results(self, client_responses: List):
        num_clients = len(client_responses)
        num_clusters = self.__num_clusters
        #client_responses is a list of list of tuples where the first element of this tuple is LSC and second element is NC
        num_features = self.__num_features
        nc_list = [0] * num_clusters
        lsc_list = [np.array([0] * num_features) for i in range(num_clusters)]

        for client_idx in range(num_clients):
            # remember the response is a list of tuples where each tuple represents the (LSC, NC) for each cluster
            response = client_responses[client_idx]
            for i in range(num_clusters):
                client_lsc = response[i][0] if response[i][0] is np.array else np.array(response[i][0])
                client_nc = response[i][1]
                lsc_list[i] = lsc_list[i] + client_lsc
                nc_list[i] = nc_list[i] + client_nc

        new_cluster_centers = []
        prev_cluster_centers = self.__cluster_centers[-1]
        #modificato
        if(len(prev_cluster_centers) != num_clusters):
            prev_cluster_centers = self.__cluster_centers
        for i in range(num_clusters):
            nc = nc_list[i]
            lsc = lsc_list[i]
            if (nc == 0):
                center = prev_cluster_centers[i]
            else:
                center = lsc / (nc * 1.0)
            new_cluster_centers.append(center)
        self.__cluster_centers.append(new_cluster_centers)

    def get_centers(self) -> List:
        return self.__cluster_centers[-1]


    def get_new_fnorm(self) -> float:
        return self.__fnorms[-1].item()

    def set_fnorms(self, fnorms):
        self.__fnorms = fnorms

    @property
    def current_round(self) -> int:
        return self.__current_round

    def finalize(self, enabled_print: bool = False) -> None:
        """ HERE WE CAN SAVE FOR EACH ROUND THE VALUES OF THE CENTERS """
        centers = self.__cluster_centers[1:]
        fnorms = self.__fnorms
        if (enabled_print):
            for i in range(len(centers)):
                print(f"round {i}, Frobenius norm {fnorms[i]:.15f}")
                print(centers[i])
        return (fnorms, centers)

class MyProcess(Process):
    def __init__(self) -> None:
        Process.__init__(self)
        self.get_node().register_name(self, Atom('my_process'))  # optional
        LOG.info("Registering process - 'my_process'")

    def handle_one_inbox_message(self, msg):
        LOG.info("Incoming %s", msg)

def main():
    n = Node(node_name="py@localhost", cookie="COOKIE")
    MyProcess()
    n.run()

if __name__ == "__main__":
    main()