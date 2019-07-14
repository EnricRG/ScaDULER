%%%% Description
%  This model aims to represent a two week schedule for a 
%  general university degree at Universitat de Girona
%  based on the High School Timetabling problem seen from
%  a scheduling perspective.

include "globals.mzn";

%%%% Predicates

predicate within(var int: v, var int: min, var int: max) =
  min <= v /\ v <= max;

% Source: MiniZinc Cheat Sheet
predicate no_overlap(var int: start0, var int: duration0,
                     var int: start1, var int: duration1) =
  start0 + duration0 <= start1 \/ start1 + duration1 <= start0;

%%%% Instance parameters
%%  Parameters to be filled before the scheduling process can start.


%%% Times
%  Atomic time slots to be filled by the different classes (30min each in our case). 
%  Time slots are interpreted as a consecutive vector of time, 
%  and there are no distinctions between days or weeks.
%  This will be modeled by constraints.

int: nDays = 5; %Number of days for the schedule.
set of int: Days = 1..nDays;

int: dayDuration; %Duration of a day in time slots.

int: nTimes = dayDuration*nDays; %Number of time slots.
set of int: Times = 1..nTimes; 

enum WEEK = {A,B,W}; %A week, B week or weekly event

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
%int: nAuxEvents; % Number of
set of int: Events = 1..nEvents;

% For each Event we know its duration in time slots.
array[Events] of int: eventDuration;

array[Events] of WEEK: eventWeek;

% Array containing which events cannot run in parallel for reasons
% not concerning a resource clash.
% eventExlusions[e1,e2] means that both events cannot run in parallel for external reasons.
array[Events, Events] of bool: eventExclusions;
%array[Events] of set of Events: eventExclusions;

% Type of the room every Event needs.
array[Events] of int: needsLabRoom;
array[Events] of int: needsClassRoom;
array[Events] of int: needsPcRoom;

% Some events can have preassigned start times
int: nPreassignedEvents;
set of int: PreassignedEvents = 1..nPreassignedEvents;

array[PreassignedEvents] of Events: preassignedEventNumbers;
array[PreassignedEvents] of Times: preassignedEventStarts;

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
constraint forall(p in PreassignedEvents) 
  (eventStart[preassignedEventNumbers[p]] = preassignedEventStarts[p]);

% If an event has to precede another, its end time has to be smaller than the start time of the other event.
constraint forall(prec in Precedences) 
  (eventStart[predecessors[prec]] + eventDuration[predecessors[prec]] - 1 < eventStart[successors[prec]]);
  
% If two events are mutually exclusive, they cannot run in parallel.
%constraint forall(e1 in Events, e2 in eventExclusions[e1]) 
%  (no_overlap(eventStart[e1], eventDuration[e1], eventStart[e2], eventDuration[e2]));
constraint forall(e1 in Events, e2 in e1+1..nEvents) 
  ( eventExclusions[e1,e2] -> no_overlap(eventStart[e1], eventDuration[e1], eventStart[e2], eventDuration[e2]) );


% For every Time t, the resource requirements are not exceeded.
% Optimizable: reduce event array instead of resource requirements array

%A week
constraint cumulative(eventStart, eventDuration, [needsLabRoom[e]*(eventWeek[e]!=B) | e in Events], labRooms);
constraint cumulative(eventStart, eventDuration, [needsClassRoom[e]*(eventWeek[e]!=B) | e in Events], classRooms);
constraint cumulative(eventStart, eventDuration, [needsPcRoom[e]*(eventWeek[e]!=B) | e in Events], pcRooms);
%B week
constraint cumulative(eventStart, eventDuration, [needsLabRoom[e]*(eventWeek[e]!=A) | e in Events], labRooms);
constraint cumulative(eventStart, eventDuration, [needsClassRoom[e]*(eventWeek[e]!=A) | e in Events], classRooms);
constraint cumulative(eventStart, eventDuration, [needsPcRoom[e]*(eventWeek[e]!=A) | e in Events], pcRooms);

% An Event cannot run between day transitions.
array[Events] of var Days: dayOfEvent; %Auxiliar variable that determines in which day the event is running. 
constraint forall(e in Events) (dayOfEvent[e] = ( (eventStart[e]-1) div dayDuration ) + 1);

%The event runs entirely on the day it starts.
constraint forall(e in Events) ( within(eventStart[e]+eventDuration[e]-1, eventStart[e], dayOfEvent[e]*dayDuration) );



%%%% Solve 

solve :: int_search(eventStart, first_fail, indomain_min, complete)
  satisfy;

output  ["\(eventStart[e])," | e in Events] ++ ["\n"] ++ 
        ["\(dayOfEvent[e]), " | e in Events];