%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2021, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 14. nov 2021 11:48
%%%-------------------------------------------------------------------
-module(supervisorNode).
-author("BPT").
%% API
-import('cMeansSupervisorNode', [updateMetrics/1, printInfo/1]).
-export([start/3, start/0, currentDirectory/0]).

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
  ClientsHostnames = ["x@localhost","y@localhost","z@localhost", "h@localhost"], %% "h@localhost","k@localhost","a@localhost"] "b@localhost","c@localhost","d@localhost","e@localhost"
  start({NClients,NMinClients, Dataset, NumFeatures, ClientsHostnames, RandomClients, RandomClientsSeed, MaxNumberRounds, RandomClientsSeed, RandomClients, Timeout, MaxAttemptsClientCrash, MaxAttemptsOverallCrash}, {NumClusters, Distance, Mode, Epsilon, SeedCenters, NormFn}, MaxAttemptsServerCrash).

start(Params, AlgParams, MaxAttemptsServer) ->
  io:format("Supervisor - Spawning the server~n"),
  process_flag(trap_exit, true),
  Server = spawn_link('server', start, [Params, AlgParams, self()]),
  loop(Server, Params, AlgParams, MaxAttemptsServer, 0).

loop(Server, Params, AlgParams, MaxAttemptsServer, CurrentAttempt) ->
  receive
    {Server, round, Message} ->
      io:format("Supervisor - received round message:~n"),
      %%printInfo(Message),
      {javaServer, server@localhost} ! {self(), round, Message},
      loop(Server, Params, AlgParams, MaxAttemptsServer, CurrentAttempt);
    {Server, norm_under_epsilon} ->
      io:format("Supervisor - received norm_under_epsilon message:~n"),
      Message = norm_under_epsilon,
      {javaServer, server@localhost} ! {self(), completed, Message};
    {Server, reached_max_rounds} ->
      io:format("Supervisor - received reached_max_rounds message~n"),
      Message = reached_max_rounds,
      {javaServer, server@localhost} ! {self(), completed, Message};
    {'EXIT', Server, Reason} ->
      io:format("Supervisor - Server has crashed for reason:~n ~w ~n", [Reason]),
      case CurrentAttempt >= MaxAttemptsServer of
        true ->
          io:format("Max attempts reached, returning...~n"),
          Message = {reached_max_attempts, CurrentAttempt},
          {javaServer, server@localhost} ! {self(), error, Message};
        false ->
          io:format("Attempt: ~w, Trying to restart the server...~n", [CurrentAttempt]),
          Message = {server_restart, CurrentAttempt},
          {javaServer, server@localhost} ! {self(), error, Message},
          NewServer = spawn_link('server', start, [Params, AlgParams, self()]),
          NewAlgParams = updateMetrics(AlgParams),
          loop(NewServer, Params, NewAlgParams, MaxAttemptsServer, CurrentAttempt + 1)
      end;
    _otherMsg ->
      loop(Server, Params, AlgParams, MaxAttemptsServer, CurrentAttempt)
  end.

currentDirectory() ->
  {ok, CurrDir} = file:get_cwd(),
  io:format("La directory corrente è: ~p ~n", [CurrDir]).