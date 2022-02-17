%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%     This module contains the functions needed for the KMeans module of a
%%%     server node in a federated learning distributed algorithm.
%%% @end
%%% Created : 05. feb 2022 15:20
%%%-------------------------------------------------------------------
-module(cMeansServer).
-author("BPT").

%% API
-export([getResults/6, getOutputIterationResults/1, getFinishedReason/0, getRoundParameters/1, startAlgorithm/2]).

% starting from the results of the clients in the different rounds, extract collective results
getResults({NumClusters, _, Epsilon, _, NormFn},  MaxNumberRounds, NumFeatures, Client_responses, Executed_rounds, {All_centers_list, RoundCenters, F_norm_values}) ->
  {New_round_centers, New_f_norm, Finished} = getClusteringResults(Epsilon, MaxNumberRounds, NumClusters, NumFeatures, NormFn, RoundCenters, Client_responses, All_centers_list, F_norm_values, Executed_rounds),
  case Executed_rounds == 0 of
    true ->
      New_All_centers_list = [New_round_centers] ++ [All_centers_list];
    false ->
      New_All_centers_list = [New_round_centers] ++ All_centers_list
  end,
  New_f_norm_values = [New_f_norm] ++ F_norm_values,
  io:format("Server - Center list of this iteration: ~w~n", [New_round_centers]),
  io:format("Server - f_norm of this iteration: ~w~n", [New_f_norm]),
  case Finished == 0 of
    true -> io:format("Server - f_norm under epsilon threshold!~n");
    false -> ok
  end,
  {{New_All_centers_list, New_round_centers, New_f_norm_values} ,Finished}.

% return specific algorithm's reason of termination
getFinishedReason() ->
    norm_under_epsilon.

% return part of the iteration results to use for logging
getOutputIterationResults({_, New_round_centers, [H|_]}) ->
  {New_round_centers, H}.

% get parameters of the round starting from the parameters of this iteration
getRoundParameters({_, RoundCenters, _}) -> RoundCenters.

getClusteringResults(Epsilon, Max_number_rounds, Num_cluster, Num_features, Norm_fm, Centers, Client_responses, All_centers_list, F_norm_values, Executed_rounds) ->
  [_|[IPList]] = string:split(atom_to_list(node()),"@"),
  Node = list_to_atom("py@" ++ IPList),
  rpc:call(Node, 'server', 'erlang_request_process_clustering_results',  [{Epsilon, Max_number_rounds, Num_cluster, Num_features, Norm_fm, Centers, Client_responses, All_centers_list, F_norm_values, Executed_rounds}]).

% start the algorithm and provide the parameters for the first iteration
startAlgorithm({NumClusters, _, _, SeedCenters, _}, NumFeatures) ->
  io:format("Server - Generating random initial centers~n"),
  [_|[IPList]] = string:split(atom_to_list(node()),"@"),
  Node = list_to_atom("py@" ++ IPList),
  Centers = rpc:call(Node, 'server', 'generate_random_centers', [SeedCenters, NumClusters, NumFeatures]),
  printCenters(Centers),
  {Centers, Centers, []}.

printCenters([]) -> ok;
printCenters([H | T]) ->
  io:format("Server - Center: ~p~n", [H]),
  printCenters(T).
