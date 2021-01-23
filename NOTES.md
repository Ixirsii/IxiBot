# Notes, TODOs, and other things


## Terminology

**Interfaces:** A class which listens for user input and prints user visible output.
IE ConsoleInterface, DiscordInterface, TwitchInterface.  

## Event Pipelines

There are 2 separate event pipelines (should probably have 2 different EventBuses for these):
1. **Event listener bus**  
   Listeners (console, Discord, Twitch, etc.) publish events to this bus (Discord reactions, leave/join events)
1. **Input listener bus**  
   Text input listeners parse commands then publish internal events to trigger the command action

## Command pipeline

It's good for listeners to parse commands then fire off events because this keeps the listener thread available if 
messages come in quickly. However this poses a problem with executing actions then printing output.

### Option 1: Command events contain variables for all command flags

This makes the job of the parser very easy, just convert a string to a POJO.
However this makes printing output more difficult as in the case of the `help` flag being passed help text has to be 
kept somewhere other than the Command object so it can be retrieved and printed by the interfaces.
One option is to put console output in the event except this gets difficult when some other action (such as writing 
to database) has to happen first. Console output may or may not be present in the event when its fired.

### Option 2: Commands can return multiple events to be published

In this case AddRoleReaction would publish an event to write a reaction to the database as well as an event to print 
some text to the interface. However the command would not know the result of the database event at the time of 
creating the output event so success/failure status is unknown.

### Option 3: Subscribers are publishers too

In this case DatabaseSubscriber gets an AddRoleReactionEvent, writes the role reaction to the database, then 
publishes an OutputEvent with the result (success or failure) which can be printed to the interface. However this 
requires "simple" subscribers to understand the context of the published event in order to then publish the correct 
output.

### Option 4: Middleware

In this case there are no "simple" subscribers (OutputSubscriber, DatabaseSubscriber) and subscribers take a more 
complex role as a business logic layer. There would instead be an AddRoleReactionSubscriber which would call the 
database DAO directly then handle output (either through publishing an OutputEvent or calling an interface directly).
In this case output text such as help text would not live in the Commands but in the middleware subscribers.

This solution provides the desired separation of concerns but adds another layer of bot logic, potentially making code 
comprehension and maintenance more difficult.

One open question with this solution is where does command validation logic live, in the command itself passed 
through the command event, or in the middleware?

### Proposal

Option 4 with validation logic living in the middleware.
