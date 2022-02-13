%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2021, <COMPANY>
%%% @doc
%%%     This module contains the functions needed for the client of a
%%%     federated learning distributed algorithm
%%% @end
%%% Created : 10. dic 2021 17:40
%%%-------------------------------------------------------------------
-module('client').
-author("BPT").

%% API
-import('cMeansClient', [computeRound/2]).
-export([start/3]).

start(Chunk, Server, Timeout) ->
  io:format("Client ~w - Launching python client...~n", [self()]),
  createPythonClient(),
  loop(Chunk, Server, Timeout).

createPythonClient() ->
    spawn(fun() -> os:cmd("python3 client.py py " ++ atom_to_list(node()) ++ " " ++ atom_to_list(erlNode)) end),
    net_kernel:connect_node('py@localhost'),
    timer:sleep(2000).

loop(Chunk, Server, Timeout) ->
  receive
    {Server, compute_round, Params} ->
      io:format("Client ~w - Executing round~n", [self()]),
      Response = computeRound(Chunk, Params),
      Server ! {self(), results, Response},
      loop(Chunk, Server, Timeout);
    {Server, shutdown} ->
      io:format("Client ~w - Server requested me to shut down~n", [self()]);
    _otherMsg ->
      io:format("Client ~w - Invalid message received~n", [self()]),
      loop(Chunk, Server, Timeout)
  after Timeout ->
    io:format("Client ~w - Timeout elapsed! Shutting down!~n", [self()]),
    true
  end.

