/*
 * Conway's Game of Life with random initial conditions.
 */

import java.awt.Graphics;
import java.awt.Color;
import javax.swing.*;
import java.util.Random;

public class GameOfLife extends JFrame {
	
	// Resolution & circle size
	private final int circleSize = 15;
	private final int YRES = 50*circleSize + 45;
	private final int XRES = 50*circleSize + 20;
	
	byte[][] board;
	
	public GameOfLife(){
		
		board = new byte[50][50];
		
		setBackground(Color.WHITE);
		setSize(XRES,YRES);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		// Random initial conditions.
		Random r = new Random();
		for(int i = 0 ; i < board.length ; i++){
			for(int j = 0 ; j < board[0].length ; j++){
				board[i][j] |= r.nextInt(2);
			}
		}

	}
	
	// Updates the board for the next round
	public void update(){
		
		// Gets the next state (in place)
		for(int i = 0 ; i < board.length ; i++){
			for(int j = 0 ; j < board[0].length ; j++){
				
				// Live next round: activate 2nd bit, Dead next round: second bit remains 0.
				int live = getLive(board, i+1, j) + getLive(board,i, j+1) + getLive(board, i-1, j) + getLive(board, i, j-1) +
						getLive(board, i+1, j+1) + getLive(board,i-1, j+1) + getLive(board, i-1, j-1) + getLive(board, i+1, j-1);
				
				// If currently dead:
				if(board[i][j]== 0){
					if(live == 3){board[i][j] |= 2;}
				}
				// If currently live:
				if(board[i][j] == 1){
					if(live == 2 || live == 3){board[i][j] |= 2;}
				}
				
			}
		}
		
		// Transform to the next states
		for(int i = 0 ; i < board.length ; i++){
			for(int j = 0 ; j < board[0].length ; j++){
				board[i][j] >>= 1;
			}
		}
	}
	
	// Returns if an element is alive or not
	static int getLive(byte[][] board, int row, int col){
		if(row < 0 || row >= board.length || col < 0 || col >= board[0].length){return 0;}
		else{return (board[row][col] & 1);}
	}
	
	
	
	public void paint(Graphics g){
		while(true){
			
			for(int i = 0 ; i < board.length ; i++){
				for(int j = 0 ; j < board[0].length ; j++){
					
					// Gray = dead, Black = live
					if(board[i][j] == 0){g.setColor(Color.GRAY);}
					else{g.setColor(Color.BLACK);}

					g.fillOval(9+i*circleSize, 35+j*circleSize, circleSize, circleSize);
				}
			}
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			update();
		}
	}

	public static void main(String[] args){
		GameOfLife game = new GameOfLife();
	}
	

}
