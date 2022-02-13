%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%     This module contains the functions needed for the KMeans module of a
%%%     client node in a federated learning distributed algorithm.
%%% @end
%%% Created : 05. feb 2022 15:20
%%%-------------------------------------------------------------------
-module(cMeansClient).
-author("BPT").

%% API
-export([computeRound/2]).

computeRound(Chunk, Centers) ->
  Result = rpc:call('py@localhost', 'client', 'run_round', [{Chunk, Centers}]),
  case is_tuple(Result) of
    true->
      exit(failed_PYRLANG_NODE_CRASHED);
    false->
      Result
  end.

