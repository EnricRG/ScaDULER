%%%% Description
%  This model aims to represent a two week schedule for a 
%  general university degree at Universitat de Girona
%  based on the High School Timetabling problem and from
%  a scheduling perspective.

include "globals.mzn";

%%%% Predicates

predicate within(var int: v, var int: min, var int: max) =
  min <= v /\ v <= max;

% Source: MiniZinc Cheat Sheet
predicate no_overlap(var int: start0, var int: duration0,
                     var int: start1, var int: duration1) =
  start0 + duration0 <= start1 \/ start1 + duration1 <= start0;

predicate concurrent(var int: e1_start, var int: e1_dur, var int: e2_start, var int: e2_dur) =
  not no_overlap(e1_start, e1_dur, e2_start, e2_dur);

%%%% Instance parameters
%%  Parameters to be filled before the scheduling process can start.


%%% Times
%  Atomic time slots to be filled by the different classes (30min each in our case). 
%  Time slots are interpreted as a consecutive vector of time, 
%  and there are no distinctions between days or weeks.
%  This will be modeled by constraints.

int: nDays; %Number of days for the schedule.
set of int: Days = 1..nDays;

int: dayDuration; %Duration of a day in time slots.

int: nTimes = dayDuration*nDays; %Number of time slots.
set of int: Times = 1..nTimes; 

%%% Resources
%  Resources used by Events at a certain Time.

%% Teachers
%int: nTeachers;
%set of int: Teachers = 1..nTeachers;

%array[Teachers] of set of Events: canPerform;

%% Rooms
% Number of available rooms at each time slot.
int: labRooms;    %Type 1
int: classRooms;  %Type 2
int: pcRooms;     %Type 3

%%% Events 
int: nEvents; % Number of events (classes) 
set of int: Events = 1..nEvents;

% For each Event we know its duration in time slots.
array[Events] of int: eventDuration;

% Array containing which events cannot run in parallel for reasons
% not concerning a resource clash.
% eventExlusions[e1,e2] means that both events cannot run in parallel for external reasons.
%array[Events, Events] of bool: eventExclusions;
array[Events] of var set of Events: eventExclusions;

% Type of the room every Event needs.
array[Events] of bool: needsLabRoom;
array[Events] of bool: needsClassRoom;
array[Events] of bool: needsPcRoom;

% Some events can have preassigned start times
array[Events] of var 0..nTimes: preassignedEventStart; %If value is set to 0, it means it has no preassigned time.

% Some events have to run later than others.
int: nPrecedences;
set of int: Precedences = 1..nPrecedences;

array[Precedences] of Events: predecessors;
array[Precedences] of Events: successors;

%%%% Model variables
array[Events] of var Times: eventStart;

%%% Auxiliary variables

% EMPTY SPACE HERE

%%%% Constraints

%%% Auxiliary variable constraints

% EMPTY SPACE HERE

%%% Hard constraints

%Preassigned times
constraint forall(e in Events) ( (preassignedEventStart[e] != 0) -> (eventStart[e] = preassignedEventStart[e]) );

% If two events are mutually exclusive, they cannot run in parallel.
% Optimisations made: only explore upper triangle matrix from exclusion matrix.
/*constraint forall(e1 in Events, e2 in e1+1..nEvents) 
  ( eventExclusions[e1,e2] -> not concurrent(eventStart[e1], eventDuration[e1], eventStart[e2], eventDuration[e2]) );
*/

constraint forall(e1 in Events, e2 in eventExclusions[e1]) (not concurrent(eventStart[e1], eventDuration[e1], eventStart[e2], eventDuration[e2]));

% If an event has to precede another, its start time has to be smaller.
constraint forall(prec in Precedences) 
  ( (eventStart[predecessors[prec]] + eventDuration[predecessors[prec]] - 1) < eventStart[successors[prec]] );

%Faster if a lot of events are mutually exclusive (far from reality)
%constraint forall(t in Times, e1 in Events, e2 in e1+1..nEvents) 
%  ( not eventExclusions[e1,e2] \/ not (eventsAtTime[t,e1] /\ eventsAtTime[t,e2]) );

% For every Time t, the resource requirements are not exceeded.
% Optimizable: All Events are checked even when they not use the resource.
%              I wanted to exploit global constraints.
constraint cumulative(eventStart, eventDuration, needsLabRoom, labRooms);
constraint cumulative(eventStart, eventDuration, needsClassRoom, classRooms);
constraint cumulative(eventStart, eventDuration, needsPcRoom, pcRooms);

/*
constraint forall(t in Times) ( sum(e in Events where within(t,eventStart[e],eventStart[e]+eventDuration[e]-1)) (bool2int(needsLabRoom[e])) <= labRooms[t] );

constraint forall(t in Times) ( sum(e in Events where within(t,eventStart[e],eventStart[e]+eventDuration[e]-1)) (bool2int(needsClassRoom[e])) <= classRooms[t] );

constraint forall(t in Times) ( sum(e in Events where within(t,eventStart[e],eventStart[e]+eventDuration[e]-1)) (bool2int(needsPcRoom[e])) <= pcRooms[t] );
*/
% An Event cannot run between day transitions.
% Optimisation to be made: precompute days of events.
array[Events] of var Days: dayOfEvent; %Auxiliar variable that determines in which day the event is running. 
constraint forall(e in Events) (dayOfEvent[e] = ( (eventStart[e]-1) div dayDuration ) + 1);

%The event runs entirely on the day it starts.
constraint forall(e in Events) ( within(eventStart[e]+eventDuration[e]-1, eventStart[e], dayOfEvent[e]*dayDuration) );



%%%% Solve

solve :: int_search(eventStart, first_fail, indomain_min, complete)
  satisfy;

output  ["\(eventStart[e])," | e in Events] ++ ["\n"] ++ 
        ["\(dayOfEvent[e]), " | e in Events];