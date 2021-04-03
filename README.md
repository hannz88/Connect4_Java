# Connect 4 Game
[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)](https://forthebadge.com)

## Introduction
This is a Connect 4 I wrote using Java. The objective of the game is to make 4 consecutive tokens either horizontally, vertically or diagonally. I have attempted to make a responsive computer player. The result is a "semi-intelligent" player but I'm hoping to improve this when I get the time. 

<p align="center">
    <img src="https://github.com/hannz88/Connect4_Java/blob/main/Images/connect4.gif" alt="Gif of connect4 in large version in real life">
</p>

## Table of Content

- [Where](#where)
- [How to  play](#how-to-play)


## Where
To play the game, simply click on this [link](https://replit.com/@hannz88/Connect4Java). It'll bring you to a repl page. Click  play button and follow the prompts.

## How to play
### 1. Set a character to represent player
The setup is simpler compared to Obliteration and Battleship. Firstly, you'll be asked to set a SINGLE alpha-numerical character to represent you the player on the board.

<p align="center">
    <img src="https://github.com/hannz88/Connect4_Java/blob/main/Images/ask_player_symbol.png" alt="Key in characters to represent players">
</p>

### 2. Announcing the details
After setting the symbol, you'll be informed of the size of the board you chosen, the character that represents you and the character that represents the computer. Then, the state of the board will be printed. In the example below, yu could see that the board is 6x7 (just like the normal Conect4), the player's character is "w" and the computer's character is "5". Note: the colour display on replit might look a bit different.

<p align="center">
    <img src="https://github.com/hannz88/Connect4_Java/blob/main/Images/introduction.png" alt="Introducing the nuances of the game">
</p>

### 3. Playing the game
Player will start first. Player will be asked which column they want to drop their token at. In the example below, the player chose column 2.

<p align="center">
    <img src="https://github.com/hannz88/Connect4_Java/blob/main/Images/ask_player_turn.png" alt="Ask player which column to drop the tokens">
</p>

The computer will then take its turn and the player will be informed which column the computer has chosen. In the example below, the computer chose column 3.

<p align="center">
    <img src="https://github.com/hannz88/Connect4_Java/blob/main/Images/computer_makes_a_move.png" alt="Computer makes a move">
</p>


## Class diagram
If you're interested in how the classes are related to each other, here's a class diagram that summarised the relationships between the classes!

<p align="center">
    <img src="https://github.com/hannz88/Connect4_Java/blob/main/Images/Connect4_New.png" alt="Connect4 class diagrame">
</p>

Have fun! And if you have any suggestion for improvements, let me know!
