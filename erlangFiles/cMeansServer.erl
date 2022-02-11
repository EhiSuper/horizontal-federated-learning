%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 05. feb 2022 15:20
%%%-------------------------------------------------------------------
-module(cMeansServer).
-author("BPT").

%% API
-export([getIterationResults/6, getOutputIterationResults/1, printResults/1, getRoundParameters/1, startAlgorithm/2]).

getIterationResults({NumClusters, _, _, Epsilon, _, NormFn},  MaxNumberRounds, NumFeatures, Client_responses, Executed_rounds, {All_centers_list, RoundCenters, F_norm_values}) ->
  {New_round_centers, New_f_norm, Finished} = getClusteringResults(Epsilon, MaxNumberRounds, NumClusters, NumFeatures, NormFn, RoundCenters, Client_responses, All_centers_list, F_norm_values, Executed_rounds),
  case Executed_rounds == 0 of
    true ->
      New_All_centers_list = [New_round_centers] ++ [All_centers_list];
    false ->
      New_All_centers_list = [New_round_centers] ++ All_centers_list
  end,
  New_f_norm_values = [New_f_norm] ++ F_norm_values,
  io:format("Server - Center list of this iteration:~w~n", [New_round_centers]),
  io:format("Server - f_norm of this iteration:~w~n", [New_f_norm]),
  case Finished == 0 of %%vedere meglio
    true -> io:format("Server - f_norm under epsilon threshold!~n");
    false -> ok
  end,
  {{New_All_centers_list, New_round_centers, New_f_norm_values} ,Finished}.


%% to provide results to java server
getOutputIterationResults({_, New_round_centers, [H|_]}) ->
  {New_round_centers, H}.


printResults({All_centers_list, _, F_norm_values}) ->
  io:format("Server - Risultato finale~nCentri: ~w~nNorme: ~w~n",[All_centers_list,F_norm_values]).

getRoundParameters({_, RoundCenters, _}) -> RoundCenters.

getClusteringResults(Epsilon, Max_number_rounds, Num_cluster, Num_features, Norm_fm, Centers, Client_responses, All_centers_list, F_norm_values, Executed_rounds) ->
  rpc:call('py@localhost', 'server', 'erlang_request_process_clustering_results',  [{Epsilon, Max_number_rounds, Num_cluster, Num_features, Norm_fm, Centers, Client_responses, All_centers_list, F_norm_values, Executed_rounds}]).

startAlgorithm({NumClusters, _, _, _, SeedCenters, _}, NumFeatures) ->
  io:format("Server - Generating random initial centers~n"),
  Centers = rpc:call('py@localhost', 'server', 'generate_random_centers', [SeedCenters,NumClusters,NumFeatures]),
  printCenters(Centers),
  {Centers, Centers, []}.

printCenters([]) -> ok;
printCenters([H | T]) ->
  io:format("Center: ~p~n", [H]),
  printCenters(T).
