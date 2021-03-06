# MATRICULATION

Author: Emre Shively

Link: http://eshiv901.kutztown.edu:8080/Matriculation/
Documentation: https://kuvapcsitrd01.kutztown.edu/~eshiv901/
Phase 2 version, for comparison: http://eshiv901.kutztown.edu:8080/MatriculationOld/

## HOW TO PLAY:
- Rules of the game are at the bottom of this file.
- Start a new game by clicking the button labeled "Start New Game" at the bottom. Choose whether you want to play versus the AI or an online human player.
- To discard a card, control-click it.
- To see the contents of a pile, click on it.
- To activate cheats, click on the opponent's fifth card and press 'c'.
- The cheat panel provides a way to manually clear out the online session, since the server cannot currently recognize disconnects.

## Design changes I planned but didn't have time to implement
- Separating the UI and Controller classes
- Creating an interface for the client controller with separate implementations for online play and solo play, rather than using lots of if-else statements.
- A MatriculationController interface that MatriculationServer and MatriculationClient would both implement
- Multiple online games running at once
-- This would be accomplished with a server controller separate from the main server class. The main server class would have a mapping between client names and their associated game controllers.
-- Randomized client IDs so that the same name can be used multiple times
- The ability to reconnect to a game by storing and retrieving session IDs in a cookie

## Unresolved problems
- I couldn't figure out how to get a result from a remote procedure call and use it as a return value for a function. I worked my way around it but being able to do this would make my code less messy. This also prevented me from using polymorphism well.
- How to detect client disconnects. This probably requires the use of sockets.

## THE RULES OF MATRICULATION

Matriculation is a game where the goal is to accumulate credit hours, term by term, and is played until one player graduates. It is played using a deck of cards designed specifically for this game. Each player maintains four piles, one for present status named control, one for probation, one for credit hours, and one for exceptions. These will be better described following description of the deck.

There are four classifications of playing cards for this game. These classifications, and a description of each card therein, including how many of that card (in parenthesis) are suggested to appear in the deck, follow:
- Term Cards: Each term cards carries a credit hour value. When played, the player places that card in their credit hour pile and adds that many credit hours to their transcript. There are cards for the following values:
    - 6 credit hours (10)
    - 9 credit hours (10)
    - 12 credit hours (8)
    - 15 credit hours (15)
    - 18 credit hours (5)
    - 21 credit hours (3)
- Setbacks: Setbacks are events that cause a player to be unable to continue accumulating credit hours. A setback is played on an opponent’s control pile, except the ogre prof card, which is played on the opponent’s credit hour pile.
    - Caught Cheating (3)
    - Car not working (can’t get to class) (3)
    - Alarm clock broken (3)
    - Probation – limit 9 credit hours per term (4)
    - Ogre Prof –credit hour total is reduced by 6 hours (3)
- Fixes: Fixes are cards that a player must obtain and use in order to continue play after a setback occurs on their control pile. Each fix remedies a particular setback.
    - Reinstated - removes caught cheating (6)
    - Car fixed (6)
    - New alarm clock (6)
    - GPA ok – removes probation (6)
    - Princess Fiona - removes ogre prof (6)
- Exceptions: Exceptions are cards that when played insure that a particular setback can’t occur to that player. Once played, a player is safe from the specified exception. There is one of each exception in the deck.
    - Sainthood – abrogates caught cheating
    - Golden Car – car that always works
    - Bulletproof Alarm Clock – always rings, sets itself
    - Straight A’s – probation-proof
    - Teacher’s Pet – No more ogre profs

Game play commences with each player being dealt 7 cards. Play begins with the opponent of the dealer, who picks a card off the deck and then chooses a card to play or discard. A player may play setbacks, fixes to alleviate setbacks, or exceptions whenever appropriate and permitted within game rules. Term cards can only be played when a player’s control pile does not have a setback on top. If a player can’t play any of the cards in their hand, they must discard one card. After any turn where there are cards remaining in the deck, a player retains seven cards in their hand.

Play continues until either a player reaches or exceeds 120 credit hours, or the deck is exhausted. If the deck becomes exhausted, play continues, in order, where players are permitted to play one card in their hand, if possible. Otherwise the player passes their turn. Players who can’t play in this situation do not discard. Once a player reaches 120 credit hours or no one can continue, the game ends.

It is theoretically possible for a player who did not graduate to win the game (or who graduates to lose; just like in real life). At the end of a hand, players are awarded points. All players who qualify get points, as follows:
- 5 points per credit hour
- 100 points for graduating
- 200 bonus for graduating in 10 terms or less
- 100 points per exception played
- 200 bonus for playing all exceptions

Notes:
- A control setback can’t be played if another setback is on the opponent’s control pile.
- If the setback abrogated by the exception is in effect, the setback is removed.
