%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2021, <COMPANY>
%%% @doc
%%%     This module contains the code needed for the supervisor node
%%%     of a federated learning distributed algorithm.
%%%     The supervisor node spawns a server node and links ot it.
%%%     After that, the supervisor wait for information about each round, provided by the server
%%%     and propagate this information to the java server.
%%%     The supervisor node is also in charge of re-executing the server if we still have
%%%     attempts to use.
%%% @end
%%% Created : 14. nov 2021 11:48
%%%-------------------------------------------------------------------
-module(supervisorNode).
-author("BPT").
%% API
-export([start/4, start/0]).

start(ServerParams, AlgParams, MaxAttemptsServer, JavaPid) ->
  io:format("Supervisor - Spawning the server~n"),
  process_flag(trap_exit, true),
  Server = spawn_link('server', start, [ServerParams, AlgParams, self()]),
  loop(Server, ServerParams, AlgParams, MaxAttemptsServer, 0, JavaPid).

%% test
start() ->
  NClients = 3,
  NMinClients = 3,
  Dataset = "https://raw.githubusercontent.com/deric/clustering-benchmark/master/src/main/resources/datasets/artificial/xclara.arff",
  NumFeatures = 2,
  NumClusters = 3,
  Distance = "numba_norm",
  Mode = 1,
  RandomClientsSeed = 0,
  RandomClients = false,
  Epsilon = 0.05,
  SeedCenters = "100",
  NormFn = "norm_fro",
  MaxNumberRounds = 10,
  Timeout = 25000,
  MaxAttemptsServerCrash = 2,
  MaxAttemptsClientCrash = 3,
  MaxAttemptsOverallCrash = 20,
  %%ClientsHostnames = ["x@127.0.0.1","y@127.0.0.1","z@127.0.0.1"],
  ClientsHostnames = ["node@172.18.0.18","node@172.18.0.42","node@172.18.0.43"],
  start({NClients,NMinClients, Dataset, NumFeatures, ClientsHostnames, RandomClients, RandomClientsSeed, MaxNumberRounds, RandomClientsSeed, RandomClients, Timeout, MaxAttemptsClientCrash, MaxAttemptsOverallCrash, Mode}, {NumClusters, Distance, Epsilon, SeedCenters, NormFn}, MaxAttemptsServerCrash, 2).

loop(Server, ServerParams, AlgParams, MaxAttemptsServer, CurrentAttempt, JavaPid) ->
  receive
    {Server, round, Message} ->
      io:format("Supervisor - Received round message~n"),
      sendResults(round, Message, JavaPid),
      loop(Server, ServerParams, AlgParams, MaxAttemptsServer, CurrentAttempt, JavaPid);
    {Server, completed, Reason} ->
      io:format("Supervisor - Received completed message~n"),
      sendResults(completed, Reason, JavaPid),
      finished;
    {Server, reached_max_rounds} ->
      io:format("Supervisor - Received reached_max_rounds message~n"),
      Message = reached_max_rounds,
      sendResults(completed, Message, JavaPid),
      finished;
    {'EXIT', Server, Reason} ->
      io:format("Supervisor - Server has crashed for reason:~n ~w ~n", [Reason]),
      NewServer = handleServerFault(ServerParams, AlgParams, MaxAttemptsServer, CurrentAttempt, JavaPid),
      case NewServer of
        undefined -> ok;
        _ -> loop(NewServer, ServerParams, AlgParams, MaxAttemptsServer, CurrentAttempt + 1, JavaPid)
      end;
    _otherMsg ->
      loop(Server, ServerParams, AlgParams, MaxAttemptsServer, CurrentAttempt, JavaPid)
  end.

sendResults(Type, Message, JavaPid) ->
    [_|[IPList]] = string:split(atom_to_list(node()),"@"),
    Node = list_to_atom("server@" ++ IPList),
    JavaPid ! {self(), Type, Message}.

handleServerFault(ServerParams, AlgParams, MaxAttemptsServer, CurrentAttempt, JavaPid) ->
    case CurrentAttempt >= MaxAttemptsServer of
        true ->
          io:format("Max attempts reached, returning...~n"),
          Message = {reached_max_attempts, CurrentAttempt},
          sendResults(error, Message, JavaPid),
          undefined;
        false ->
          io:format("Attempt: ~w, Trying to restart the server...~n", [CurrentAttempt]),
          Message = {server_restart, CurrentAttempt},
          sendResults(error, Message, JavaPid),
          NewServer = spawn_link('server', start, [ServerParams, AlgParams, self()]),
          NewServer
      end.
