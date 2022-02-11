%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
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

