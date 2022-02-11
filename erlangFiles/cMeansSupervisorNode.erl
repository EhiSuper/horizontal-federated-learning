%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 05. feb 2022 15:20
%%%-------------------------------------------------------------------
-module(cMeansSupervisorNode).
-author("BPT").

%% API
-export([updateMetrics/1, printInfo/1]).

%% set epsilon somehow
updateMetrics({NumClusters, Distance, Mode, Epsilon, SeedCenters, NormFn}) ->
  {NumClusters, Distance, Mode, Epsilon + 0.5 * Epsilon, SeedCenters, NormFn}.

printInfo({IterationElements, NumCrashes, AllClients, InvolvedClients, Executed_rounds})->
  NewClients = removeChunkInfo(AllClients, []),
  NewInvolvedClients = removeChunkInfo(InvolvedClients, []),
  {CentersList, _, NormList} = IterationElements,
  io:format("Supervisor -~n
             round: ~w~n
             centers: ~w~n
             norms: ~w~n
             crashes: ~w~n
             clients: ~w~n
             involvedclients: ~w~n", [Executed_rounds, CentersList, NormList, NumCrashes, NewClients, NewInvolvedClients]).

removeChunkInfo([], List) -> List;
removeChunkInfo([H|T], List) ->
  {Hostname, Pid, _, Attempts} = H,
  NewList = [{Hostname, Pid, Attempts} | List],
  removeChunkInfo(T, NewList).
