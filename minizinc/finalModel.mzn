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
%  This distinction will be modelled by constraints.

%Number of days for the schedule.  
int: nDays; 
set of int: Days = 1..nDays;

%Duration of a day in time slots.
int: dayDuration; 

%Number of time slots.
int: nTimes = dayDuration*nDays; 
set of int: Times = 1..nTimes; 

enum WEEK = {A,B,W}; %A week, B week or weekly event


%%% Resources
%  Resources used by Events at a certain Time.
int: nResources;
set of int: Resources = 1..nResources;

% resourceQuantity[r] = x means x is the maximum capacity of resource r. 
array[Resources] of int: resourceQuantity;


%%% Events 
% Events to be scheduled.
int: nEvents; % Number of events.
set of int: Events = 1..nEvents;

% For each Event we know its duration in time slots.
% eventDuration[e] = i if event e has duration i.
array[Events] of int: eventDuration;

% For each Event we know in which week it must be performed.
% eventWeek[e] = w if event e must be performed during week w.
array[Events] of WEEK: eventWeek;

% Array containing which events cannot run in parallel for reasons not concerning a resource clash.
% eventExlusions[e1,e2] = true when both events cannot run in parallel for external reasons.
% Precondition: this matrix is symetric.
array[Events, Events] of bool: eventExclusions;

% Type of resource this event needs to be performed.
% resourceNeeded[e,r] = true when event e needs resource r.
array[Events, Resources] of bool: resourceNeeded;

% Some events can have preassigned start times.
int: nPreassignedEvents;
set of int: PreassignedEvents = 1..nPreassignedEvents;

% preassignedEventNumbers[n] = e if preassignment n refers to event e.
array[PreassignedEvents] of Events: preassignedEventNumbers;
% preassignedEventStarts[n] = t if preassignment n assigns the event preassignedEventNumbers[n] to start at time t.
array[PreassignedEvents] of Times: preassignedEventStarts;

% Some events must run later than others.
int: nPrecedences;
set of int: Precedences = 1..nPrecedences;

% predecessors[p] = e1 if event e1 is the first event of the precedence p.
array[Precedences] of Events: predecessors;
% predecessors[p] = e2 if event e2 is the following event of the precedence p.
array[Precedences] of Events: successors;



%%%% Model variables

% Tells us at which time interval the event starts.
% eventStart[e] = t if event e is assigned to start at time t.
array[Events] of var Times: eventStart;

%%% Auxiliary variables

% Determines in which day the event is running. 
array[Events] of var Days: dayOfEvent; 


%%%% Constraints

%%% Auxiliary variable constraints

% Compute the day the event is running.
constraint forall(e in Events) (dayOfEvent[e] = ( (eventStart[e]-1) div dayDuration ) + 1);


%%% Hard constraints

%Preassigned times. For all preassignations, the preassigned event starts at preassigned time.
constraint forall(p in PreassignedEvents) 
  (eventStart[preassignedEventNumbers[p]] = preassignedEventStarts[p]);

% If an event must precede another, its end time must be smaller than the start time of the other event (strict precedence).
constraint forall(prec in Precedences) 
  (eventStart[predecessors[prec]] + eventDuration[predecessors[prec]] - 1 < eventStart[successors[prec]]);
  
% If two events are mutually exclusive, they cannot run in parallel.
% Because eventExclusions is a symmetric matrix, we can skip a part of the matrix.
constraint forall(e1 in Events, e2 in e1+1..nEvents) 
  ( eventExclusions[e1,e2] -> no_overlap(eventStart[e1], eventDuration[e1], eventStart[e2], eventDuration[e2]) );

% For every Time t, the resource requirements are not exceeded.
% Optimizable: reduce event array instead of resource requirements array

%A week: compute together A and W week.
constraint forall(r in Resources)
  (cumulative(eventStart, eventDuration, [resourceNeeded[e,r]*(eventWeek[e]!=B) | e in Events], resourceQuantity[r]));

%B week: compute together B and W week.
constraint forall(r in Resources)
  (cumulative(eventStart, eventDuration, [resourceNeeded[e,r]*(eventWeek[e]!=A) | e in Events], resourceQuantity[r]));

%% An Event cannot run between day transitions. The event runs entirely on the day it starts.
constraint forall(e in Events) ( within(eventStart[e]+eventDuration[e]-1, eventStart[e], dayOfEvent[e]*dayDuration) );



%%%% Solve 

solve :: int_search(eventStart, first_fail, indomain_min, complete)
  satisfy;

% Easy-to-parse output format.
output  ["\(e)," | e in Events] ++ ["\n"] ++ ["\(eventStart[e])," | e in Events]


