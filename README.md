# Miscellaneous-Projects
Miscellaneous projects in image processing, multi-threading, text mining, etc.

Image Processing - Gaussian Blur

• Program that applies Gaussian blurring to an image.  Here, instead of using box blur (2D kernel), we use an improved algorithm where we make 2 passes using a 1D kernel.  This brings the runtime down to O(width•height•radius), instead of radius^2.

Call Center Simulation

• A program that simulates a type of call center.  Uses multi-threading and semaphores.

Game Of Life

• Implements Conway's Game of Life.  Uses a small trick that allows updates to be done in constant space, and performs bit manipulation of bytes rather than using ints.  More information on the game can be found at https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life

Screensaver Graphic

• A graphic that acts like a screensaver.  Lines of various colours snake around the screen with continuous boundary conditions.

Twitter Bot

• A bot that listens to tweets using Twitter's API.  This example listened during a sporting event to detect tweet frequency and location relevant to the event.


