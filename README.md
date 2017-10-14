# Miscellaneous-Projects
Miscellaneous projects in image processing, multi-threading, text mining, etc.

Image Processing - Gaussian Blur

• Program that applies Gaussian blurring to an image.  Here, instead of using box blur (2D kernel), we use an improved algorithm where we make 2 passes using a 1D kernel.  This brings the runtime down to O(width•height•radius), instead of radius^2.

Word Counter

• This program takes a text file, and determines the frequency of each word in the file. By word, I mean set of letters surrounded by spaces. Note that something like '|' or '^&.' will not count, English letters only. Also, the word "peoples'" will become "peoples", and "don't" will remain "don't". Finally, all characters are made to be lower case. The program makes one pass through the file, meaning O(N) time, with N being the number of words. However, each word must be cleaned of leading and trailing characters such as periods, so something like O(N*longest word legnth) is more accurate.

To complete the actual counting, I use a hashmap of String -> Integer, where Integer is the frequency of a given word. After scanning the file, I use a class that includes a word along with its frequency, and add it to a priority queue (max heap) and poll as many times as needed. Removing k words then has a runtime of O(k*log(N)).

Call Center Simulation

• A program that simulates a type of call center.  Uses multi-threading and semaphores.

Game Of Life

• Implements Conway's Game of Life.  Uses a small trick that allows updates to be done in constant space, and performs bit manipulation of bytes rather than using ints.  More information on the game can be found at https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life

Screensaver Graphic

• A graphic that acts like a screensaver.  Lines of various colours snake around the screen with continuous boundary conditions.

Twitter Bot

• A bot that listens to tweets using Twitter's API.  This example listened during a sporting event to detect tweet frequency and location relevant to the event.


