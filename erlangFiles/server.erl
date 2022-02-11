%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2021, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 27. nov 2021 10:57
%%%-------------------------------------------------------------------
-module(server).
-author("BPT").

%% API
-import('net_adm', [ping/1]).
-import('rpc', [call/3, call/4]).
-import('cMeansServer', [getIterationResults/6, getOutputIterationResults/1, printResults/1, getRoundParameters/1, startAlgorithm/2]).

-export([start/3]).

start({NClients,NMinClients, Dataset, NumFeatures, ClientsHostnames, RandomClients, RandomClientsSeed, MaxNumberRounds, RandomClientsSeed, RandomClients, Timeout, MaxAttemptsClientCrash, MaxAttemptsOverallCrash}, AlgParams, Supervisor) ->
  io:format("Server - Launching python server...~n"),
  spawn(fun() -> os:cmd("python \"erlangFiles/server.py\"") end),
  timer:sleep(5000),
  io:format("Server - Generating chunks...~n"),
  DatasetChunks = rpc:call('py@localhost', 'server', 'generate_chunks',  [NClients,Dataset]),
  IterationParameters = startAlgorithm(AlgParams, NumFeatures),
  io:format("Server - Spawning clients~n"),
  CompleteListClients = spawnClients(NClients, DatasetChunks, ClientsHostnames, [], Timeout),
  loop(NClients, NMinClients, CompleteListClients, DatasetChunks, NumFeatures, RandomClientsSeed, RandomClients,
    ClientsHostnames, MaxNumberRounds, RandomClientsSeed, Timeout, MaxAttemptsClientCrash, MaxAttemptsOverallCrash, 0, IterationParameters, 0, AlgParams, Supervisor),
  terminate().

printList([]) -> ok;
printList([H | T]) ->
  {_, Pid, _, _ } = H,
  io:format("Element: ~p~n", [Pid]),
  printList(T).

spawnClients(0, _, _, ListClients, _) -> ListClients;
spawnClients(NClients, [ChunkHead | ChunkTail], [HostnameHead | HostnameTail], ListClients, Timeout) ->
  ServerPID = self(),
  {Client, _} = spawn_monitor(list_to_atom(HostnameHead), 'client', start, [ChunkHead, ServerPID, Timeout]),
  io:format("Server - Spawned ~p~n", [Client]),
  ClientInfo = {HostnameHead, Client, ChunkHead, 0},
  List = ListClients ++ [ClientInfo],
  spawnClients(NClients-1, ChunkTail, HostnameTail, List, Timeout).

loop(NClients, NMinClients, ListClients, DatasetChunks, NumFeatures, RandomClientsSeed, RandomClients,
    ClientsHostnames, MaxNumberRounds, RandomClientsSeed, Timeout, MaxAttemptsClientCrash, MaxAttemptsOverallCrash,
    NumCrashes, IterationElements, Executed_rounds, AlgParams, Supervisor)->
  case MaxNumberRounds =< Executed_rounds of %% max rounds reached ?
    true ->
      Supervisor ! {self(), reached_max_rounds},
      io:format("Server - Max number rounds reached~n"),
      io:format("Server - Iterations terminated~n"),
      io:format("Server - Shutting down all nodes~n"),
      shutdownClients(ListClients);
    false ->
      io:format("Server - Getting into the ~p-th iteration of the loop~n",[Executed_rounds + 1]),
      io:format("Server - Selecting nodes to be involved~n"),
      InvolvedClients = getInvolvedClients(NClients, NMinClients, ListClients, RandomClientsSeed, RandomClients),
      io:format("Server - Selected clients:~n"),
      printList(InvolvedClients),
      io:format("Server - Sending execution request to the nodes~n"),
      RoundParameters = getRoundParameters(IterationElements),
      performRound(InvolvedClients, RoundParameters),
      {Client_responses, UpdatedClients, AvailableHostnames, NewNumCrashes} = waitResponses(Timeout, RoundParameters, MaxAttemptsClientCrash, MaxAttemptsOverallCrash, NumCrashes, ClientsHostnames, ListClients, InvolvedClients, []),
      io:format("Server - Received all results~n"),
      io:format("Server - Joining all results~n"),
      {NewIterationElements, Finished} = getIterationResults(AlgParams, MaxNumberRounds, NumFeatures, Client_responses, Executed_rounds, IterationElements),
      Supervisor ! {self(), round, {getOutputIterationResults(NewIterationElements), NewNumCrashes, InvolvedClients, UpdatedClients, Executed_rounds+1}},
      case Finished == 0 of %%norm under epsilon
        true ->
          Supervisor ! {self(), norm_under_epsilon},
          io:format("Server - Shutting down all nodes~n"),
          shutdownClients(UpdatedClients);
        false ->
          loop(NClients, NMinClients, UpdatedClients, DatasetChunks, NumFeatures, RandomClientsSeed, RandomClients,
            AvailableHostnames, MaxNumberRounds, RandomClientsSeed, Timeout, MaxAttemptsClientCrash,
            MaxAttemptsOverallCrash, NewNumCrashes, NewIterationElements, Executed_rounds+1, AlgParams, Supervisor)
      end
  end.

% è una lista
%getOutputClientResults({HostnameHead, Client, _, NumCrashes}) ->
% {HostnameHead, Client, NumCrashes}.

getInvolvedClients(NClients, NMinClients, ListClients, RandomClientSeed, RandomClients) ->
  case NClients == NMinClients of
    false when RandomClients == true ->
      InvolvedClientsIndexes = rpc:call('py@localhost', 'server', 'erlang_request_get_involved_clients',  [{NClients, NMinClients, RandomClientSeed}]),
      selectClients(ListClients, InvolvedClientsIndexes);
    false when RandomClients == false ->
      lists:sublist(ListClients, NMinClients);
    true ->
      ListClients
  end.

selectClients(_, []) -> [];
selectClients(ListClients, [FirstClientIndex | OtherClientIndexes]) ->
  [lists:nth(FirstClientIndex + 1, ListClients) | selectClients(ListClients, OtherClientIndexes)].

shutdownClients([]) -> io:format("Server - Sent shutdown command to all nodes~n");
shutdownClients([H|T]) ->
  {_, Pid, _, _} = H,
  Pid ! {self(), shutdown},
  shutdownClients(T).

performRound([], _) -> ok;
performRound([H|T], RoundParameters) ->
  {_, Pid, _, _} = H,
  io:format("Performing round ~w~n", [Pid]),
  Pid ! {self(), compute_round, RoundParameters},
  performRound(T, RoundParameters).

waitResponses(_, _, _, _, NumCrashes, ClientsHostnames, UpdatedClients, [],ResultsList) -> {ResultsList, UpdatedClients, ClientsHostnames, NumCrashes};
waitResponses(Timeout, RoundParameters, MaxAttemptsClientCrash, MaxAttemptsOverallCrash, NumCrashes, ClientsHostnames, ListClients, PidList, ResultsList) ->
  receive
    {From, results, Results} ->
      case lists:keymember(From, 2, PidList) of
        true ->
          NewPidList = lists:keydelete(From, 2, PidList),
          NewResultsList = lists:append([Results], ResultsList),
          waitResponses(Timeout, RoundParameters,MaxAttemptsClientCrash, MaxAttemptsOverallCrash, NumCrashes, ClientsHostnames, ListClients, NewPidList, NewResultsList);
        false ->
          waitResponses(Timeout, RoundParameters, MaxAttemptsClientCrash, MaxAttemptsOverallCrash, NumCrashes, ClientsHostnames, ListClients, PidList, ResultsList)
      end;
    {'DOWN', _, _, Pid, Reason} ->
      io:format("Server - Client ~w has crashed for reason: ~w ~n", [Pid, Reason]),
      NewNumCrashes = checkOverallCrashes(NumCrashes, MaxAttemptsOverallCrash),
      {Hostname, Pid, Chunk, Attempts} = lists:keyfind(Pid, 2, ListClients),
      NewPidList = lists:keydelete(Pid, 2, PidList), %%delete member from list of nodes that have to answer this round
      UpdatedClients = lists:keydelete(Pid, 2, ListClients), %%delete member from list of nodes in general
      case Attempts >= MaxAttemptsClientCrash of
        true ->
          io:format("Server - Reached max attempts for node ~w at location ~p ~n", [Pid, Hostname]),
          AvailableHostnames = lists:delete(Hostname, ClientsHostnames),
          NextHostname = selectNextLocation(ListClients, AvailableHostnames),
          ClientInfo = initializeAtHostname(NextHostname, Chunk, Timeout, 0, RoundParameters),
          List = NewPidList ++ [ClientInfo],
          UpdatedList = UpdatedClients ++ [ClientInfo],
          waitResponses(Timeout, RoundParameters, MaxAttemptsClientCrash, MaxAttemptsOverallCrash, NewNumCrashes, AvailableHostnames, UpdatedList, List, ResultsList);
        false ->
          timer:sleep(10000),
          ClientInfo = initializeAtHostname(Hostname, Chunk, Timeout, Attempts, RoundParameters),
          List = NewPidList ++ [ClientInfo],
          UpdatedList = UpdatedClients ++ [ClientInfo],
          waitResponses(Timeout, RoundParameters, MaxAttemptsClientCrash, MaxAttemptsOverallCrash, NewNumCrashes, ClientsHostnames, UpdatedList, List, ResultsList)
      end;
    _othermsg ->
      waitResponses(Timeout, RoundParameters, MaxAttemptsClientCrash, MaxAttemptsOverallCrash, NumCrashes, ClientsHostnames, ListClients, PidList, ResultsList)
  end.

checkOverallCrashes(NumCrashes, MaxAttemptsOverallCrash) ->
  NewNumCrashes = NumCrashes + 1,
  case NewNumCrashes > MaxAttemptsOverallCrash of
    true->
      exit(failed_MAX_CRASHES_REACHED);
    false->
      io:format("Server - Number of crashes: ~w ~n", [NewNumCrashes]),
      NewNumCrashes
  end.

initializeAtHostname(Hostname, Chunk, Timeout, Attempts, RoundParameters) ->
  {Client, _} = spawn_monitor(list_to_atom(Hostname), 'client', start, [Chunk, self(), Timeout]),
  io:format("Server - Spawned ~p at location ~p. Attempt ~p ~n", [Client, Hostname, Attempts]),
  ClientInfo = {Hostname, Client, Chunk, Attempts + 1},
  Client ! {self(), compute_round, RoundParameters},
  ClientInfo.

selectNextLocation(_, []) -> exit(failed_NO_LOC_AVAILABLE);
selectNextLocation(ListClients, [HostnamesHead | HostnamesTail]) ->
  case lists:keymember(HostnamesHead, 1, ListClients) of
    true->
      selectNextLocation(ListClients, HostnamesTail);
    false->
      HostnamesHead
  end.

terminate() ->
  init:stop(). %controlla se termina


%% Unregister alla fien facendo Process.exit
%% Handle exception from rpc

%% readParameters() ->
% [Distance| [ Mode| [RandomClientsSeed|[ RandomClients| [Epsilon| [ClientsHostnames| [MaxNumberRounds| [NormFn | [SeedCenters]]]]]]]]] = rpc:call('py@localhost', 'server', 'read_conf',  []),
%{Distance, Mode, RandomClientsSeed, RandomClients, Epsilon, ClientsHostnames, MaxNumberRounds, NormFn, SeedCenters }.
