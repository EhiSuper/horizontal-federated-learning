%%%-------------------------------------------------------------------
%%% @author BPT
%%% @copyright (C) 2021, <COMPANY>
%%% @doc
%%%
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
  % register(erlangNode, self()),
  % invio al nodo dove deve inviarmi il pid così che io possa settare il link
  spawn(fun() -> os:cmd("python client.py py " ++ atom_to_list(node()) ++ " " ++ atom_to_list(erlNode)) end),
  net_kernel:connect_node('py@localhost'),
  timer:sleep(1000),
  %% Prova 1 linko uso registered name
  %%erlang:link(whereis('pyrlang')),
  %% Prova 2 ricevo Pid
  %%Pid = rpc:call('py@localhost', 'client', 'get_pid', [{2}]),
  %% io:format("Pid: ~p~n", [Pid]),
  receive
    Pid when is_atom(Pid) ->
      io:format("Received Pyrlang Pid: ~p~n", [Pid])
  after 5000 -> io:format("Can't link to the pyrlang Node~n")
  end,
  loop(Chunk, Server, Timeout).

loop(Chunk, Server, Timeout) ->
  receive
    {Server, compute_round, Params} ->
      io:format("Client ~w - Executing round~n",[self()]),
      Response = computeRound(Chunk, Params),
      Server ! {self(), results, Response},
      loop(Chunk, Server, Timeout);
    {Server, shutdown} ->
      io:format("Client ~w - Server requested me to shut down~n",[self()]);
    _otherMsg ->
      io:format("Client ~w - Invalid message received~n",[self()]),
      loop(Chunk, Server, Timeout)
  after Timeout ->
    io:format("Client ~w - Timeout elapsed! Shutting down!~n",[self()]),
    true
  end.

%% Shutting down pyrlang? rpc:call('py@127.0.0.1', 'e02_registered_process', 'shutdown', []),
%% Set Timeout
