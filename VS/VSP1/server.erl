-module(server).
-import(werkzeug, [get_config_value/2, logging/2]).
-import(cmem, [initCMEM/2, getClientNNr/2, updateClient/4, dellExpired/1]).
-import(lifetimeTimer, [resetTimer/1]).
-export([start/1]).
-define(LOGFILE, lists:flatten(io_lib:format("~p.log", [node()]))).

%Die Nummern in den Kommentaren beziehen sich auf:
%Das Diagramm "Serverkomponente"

%Einsteigspunkt, wird vom Modul lifetimeTimer aufgerufen.
start(Timer) ->
	{Clientlifetime, Servername, HBQname, HBQnode} = readCfg(),
	erlang:register(Servername, self()),
	CMEM = initHBQCMEM(Clientlifetime, Servername, HBQname, HBQnode),
	loop(Timer, 1, HBQname, HBQnode, CMEM).

%Steuernde Werte aus server.cfg lesen.
readCfg() ->
	{ok, ConfigListe} = file:consult("server.cfg"),
   	{ok, Clientlifetime} = get_config_value(clientlifetime, ConfigListe),
    	{ok, Servername} = get_config_value(servername, ConfigListe),
    	{ok, HBQname} = get_config_value(hbqname, ConfigListe),
    	{ok, HBQnode} = get_config_value(hbqnode, ConfigListe),
    	{Clientlifetime, Servername, HBQname, HBQnode}.

initHBQCMEM(Clientlifetime, Servername, HBQname, HBQnode) ->
	{HBQname, HBQnode} ! {self(), {request, initHBQ}},
	CMEM = initCMEM(Clientlifetime, ?LOGFILE),
	receive
		{reply, ok} ->
			logging(?LOGFILE, lists:flatten(io_lib:format("Server: HBQ konnte initialisiert werden. ~n", [])))
	end,
	CMEM.

%1.) Auf Anfrage warten.
loop(Timer, INNR, HBQname, HBQnode, CMEM) ->
	receive 
		%2.) Neue Nahrichtennummer angefordert.
		{Pid, getmsgid} ->
			%3.) Nachrichtennummer senden.
			Pid ! {nid, INNR},
			%4.) Timer aktualisieren & zurück zu 1.)
			loop(resetTimer(Timer), INNR + 1, HBQname, HBQnode, CMEM);
		%5.) Nachricht speichern.
		{dropmessage, [INNRn, Msg, TSclientout]} ->
			%6.) Zeitstempel hinzufügen in HBQ realisiert.
			%7.) Nachricht zur HBQ (Queuemanager) senden.
			{HBQname, HBQnode} !  {self(), {request,pushHBQ,[INNRn,Msg,TSclientout]}},
							
			receive
				{reply, ok} ->
					logging(?LOGFILE, lists:flatten(io_lib:format("Nachricht erfolgreich in HBQ eingetragen ~n", [])))
			end,
			%4.) Timer aktualisieren & zurückzu 1.)
			loop(resetTimer(Timer), INNR, HBQname, HBQnode, CMEM);
		%8.) Nachricht zustellen (Anfrage).
		{Pid, getmessages} ->
			%9.) Infos vom Clientmanager Abfragen (NNr).
			%10.) - 12.) in CMEM realisiert.			
			NNr = getClientNNr(CMEM, Pid),
			%14.) Nachricht absenden (zur HBQ).
			{HBQname, HBQnode} ! {self(), {request,deliverMSG,NNr,Pid}},
			%auf Antwort der HBQ warten. Ist die SendNNr > 0, handelt es sich um eine tatsaechlich
			%gesendete Nachricht. Da bei einer Dummy-Nachricht ein -1 zureuck kommt.
			receive
				{reply, SendNNr} when SendNNr > 0 ->
					%13.) Clientmanager aktualisieren.
					CMEMNew = updateClient(CMEM, Pid, SendNNr, ?LOGFILE);
				{reply, _SendNNr} ->
					CMEMNew = CMEM
			end,

			%4.) Timer aktualisieren und zurück zu 1.)
			loop(resetTimer(Timer), INNR, HBQname, HBQnode, CMEMNew);
		{dellExpired, Servername} -> 
			CMEMnew = dellExpired(CMEM),
			%dellExpired des CMEM soll alle 1 Sekunden ausgeführt werden (beliebig gewählt).
			timer:send_after(1000*1000, Servername, {dellExpired, Servername}),
			loop(resetTimer(Timer), INNR, HBQname, HBQnode, CMEMnew);
		%16.) Lebenszeit abgelaufen -> Terminieren.
		{srvtimeout} ->
			
			{HBQname, HBQnode} ! {self(), {request, dellHBQ}},
			receive 
				{reply, ok} ->
					io:format("Server Timeout! ~n")
			end
			
	end.
